package com.tutor.project.service;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.tutor.project.constant.PaymentStatus;
import com.tutor.project.constant.TransactionStatus;
import com.tutor.project.constant.TransactionType;
import com.tutor.project.dto.request.TransactionCreationRequest;
import com.tutor.project.entity.*;
import com.tutor.project.exception.AppException;
import com.tutor.project.exception.ErrorCode;
import com.tutor.project.repository.*;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor
public class PaymentService {
    CourseBatchRepository courseBatchRepository;
    TransactionRepository transactionRepository;
    UserRepository userRepository;
    PaymentRepository paymentRepository;
    EnrollmentRepository enrollmentRepository;
    UpdateRoleRequestRepository updateRoleRequestRepository;
    @NonFinal
    @Value("${stripe.secretKey}")
    String SECRET_KEY;
    @NonFinal
    @Value("${stripe.signingKey}")
    String endpointSecret;
    UserService userService;
    EnrollmentService enrollmentService;
    @NonFinal
    @Value("${fee.price}")
    long price;

    public String createTransaction(TransactionCreationRequest request) throws StripeException {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (StringUtils.isBlank(request.getUpdateRoleRequestId()) &&
                StringUtils.isBlank(request.getEnrollmentId())) {
            throw new AppException(ErrorCode.INVALID_DATA);
        }
        String type = request.getEnrollmentId().isEmpty()
                ? "TUTOR_UPGRADE" : "COURSE_PAYMENT";
        String name = type.equals("TUTOR_UPGRADE") ? "Update role" : "enroll course batch";
        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Enrollment enrollment = new Enrollment();
        UpdateRoleRequest updateRoleRequest = new UpdateRoleRequest();
        if (type.equals("COURSE_PAYMENT")) {
            enrollment = enrollmentRepository.findById(request.getEnrollmentId())
                    .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_EXISTED));
            var batch= courseBatchRepository.findById(enrollment.getCourseBatch().getId())
                    .orElseThrow(()->new AppException(ErrorCode.COURSE_BATCH_NOT_EXISTED));
            if(batch.getPrice()!=request.getAmount()){
                throw new AppException(ErrorCode.INVALID_DATA);
            }
        } else {
            updateRoleRequest = updateRoleRequestRepository.findById(request.getUpdateRoleRequestId())
                    .orElseThrow(() -> new AppException(ErrorCode.UPDATE_ROLE_REQUEST_NOT_EXISTED));
            if(request.getAmount()!=price)
                throw new AppException(ErrorCode.INVALID_DATA);
        }
        Transaction transaction = Transaction.builder()
                .createdAt(LocalDateTime.now())
                .amount(request.getAmount())
                .user(user)
                .enrollment(Objects.isNull(enrollment.getId()) ? null : enrollment)
                .updateRoleRequest(Objects.isNull(updateRoleRequest.getId())
                        ? null : updateRoleRequest)
                .gateway("STRIPE")
                .status(TransactionStatus.PENDING)
                .type(TransactionType.valueOf(type))
                .build();
        transactionRepository.save(transaction);
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:3000/success")
                        .setCancelUrl("http://localhost:3000/cancel")
                        .setExpiresAt(Instant.now().plusSeconds(1800).getEpochSecond()) // 10 minutes
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("vnd")
                                                        .setUnitAmount((long) request.getAmount())
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName(name)
                                                                        .build())
                                                        .build())
                                        .build())
                        .build();
        Stripe.apiKey = SECRET_KEY;
        Session session = Session.create(params);
        transaction.setGatewayTransactionId(session.getId());
        transactionRepository.save(transaction);
        return session.getUrl();
    }

    @Transactional
    public String handleWebhook(String payload, String signHeader) throws SignatureVerificationException {
        Event event = Webhook.constructEvent(payload, signHeader, endpointSecret);
        log.info("event:{}", event);
        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject()
                    .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
            if (!"paid".equals(session.getPaymentStatus()))
                return "not paid";
            var transaction = transactionRepository.findByGatewayTransactionId(session.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_EXISTED));
            if (transaction.getStatus().equals(TransactionStatus.SUCCESS)) {
                return "already transaction";
            }
            if (transaction.getType().equals(TransactionType.COURSE_PAYMENT)) {
                log.info("COURSE_PAYMENT");
                Enrollment enrollment = enrollmentRepository.findById(transaction
                        .getEnrollment().getId()).orElseThrow(() -> new AppException(
                        ErrorCode.ENROLLMENT_NOT_EXISTED
                ));
                enrollmentService.payEnrollment(enrollment.getId());
            } else {
                UpdateRoleRequest updateRoleRequest = updateRoleRequestRepository.findById(
                        transaction.getUpdateRoleRequest().getId()
                ).orElseThrow(() -> new AppException(ErrorCode.UPDATE_ROLE_REQUEST_NOT_EXISTED));
                userService.completedPayment(updateRoleRequest.getId());
            }
            transaction.setStatus(TransactionStatus.SUCCESS);
            transaction.setUpdatedAt(Instant.ofEpochSecond(event.getCreated())
                    .atZone(ZoneId.systemDefault()).toLocalDateTime());
            Payment payment = Payment.builder()
                    .gatewayPaymentId(session.getId())
                    .method(session.getPaymentMethodTypes().getFirst())
                    .currency(session.getCurrency())
                    .amount(session.getAmountTotal())
                    .transaction(transaction)
                    .paidAt(Instant.ofEpochSecond(session.getCreated())
                            .atZone(ZoneId.systemDefault()).toLocalDateTime())
                    .build();
            transactionRepository.save(transaction);
            paymentRepository.save(payment);

        } else if ("checkout.session.expired".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject()
                    .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
            var transaction = transactionRepository.findByGatewayTransactionId(session.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_EXISTED));
            if (transaction.getStatus().equals(TransactionStatus.SUCCESS)) {
                return "already transaction";
            }
            transaction.setStatus(TransactionStatus.EXPIRED);
            transactionRepository.save(transaction);
        }
        return "success";
    }

}
