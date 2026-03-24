package com.tutor.project.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.tutor.project.dto.request.TransactionCreationRequest;
import com.tutor.project.dto.response.ApiResponse;
import com.tutor.project.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    PaymentService paymentService;
    @PostMapping
    public ApiResponse<String> createTransaction
            (@RequestBody TransactionCreationRequest request) throws StripeException {
        return ApiResponse.<String>builder()
                .result(paymentService.createTransaction(request))
                .build();
    }
    @PostMapping("/webhook")
    public ApiResponse<String> handleWebhook
            (@RequestBody String payload,
             @RequestHeader("Stripe-Signature") String signHeader) throws SignatureVerificationException {
        log.info("???");
        return ApiResponse.<String>builder()
                .result(paymentService.handleWebhook(payload,signHeader))
                .build();
    }
}
