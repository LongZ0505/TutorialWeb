package com.tutor.project.service;

import com.tutor.project.constant.PaymentStatus;
import com.tutor.project.constant.ReviewStatus;
import com.tutor.project.dto.request.EnrollmentCreationRequest;
import com.tutor.project.dto.response.EnrollmentResponse;
import com.tutor.project.entity.*;
import com.tutor.project.exception.AppException;
import com.tutor.project.exception.ErrorCode;
import com.tutor.project.mapper.EnrollmentMapper;
import com.tutor.project.repository.CourseBatchRepository;
import com.tutor.project.repository.CourseRepository;
import com.tutor.project.repository.EnrollmentRepository;
import com.tutor.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.ErrorManager;

@Slf4j
@Service
@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor
public class EnrollmentService {
    CourseService courseService;
    EnrollmentRepository enrollmentRepository;
    CourseBatchRepository courseBatchRepository;
    UserRepository userRepository;
    EnrollmentMapper enrollmentMapper;
    CourseRepository courseRepository;

    public String enrollCourse(EnrollmentCreationRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        var check = enrollmentRepository.findByUserIdAndCourseBatchId(userId, request.getCourseBatchId());
        if (check.isPresent()) {
            throw new AppException(ErrorCode.ENROLLMENT_EXISTED);
        }
        int currentStudent = courseService.quantityEnrollment(request.getCourseBatchId());
        User user = userRepository.findById(userId).
                orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        CourseBatch courseBatch = courseBatchRepository.findById(request.getCourseBatchId()).
                orElseThrow(() -> new AppException(ErrorCode.COURSE_BATCH_NOT_EXISTED));
        if (++currentStudent > courseBatch.getMaxStudent()) {
            throw new AppException(ErrorCode.INVALID_DATA);
        }
        Enrollment enrollment = Enrollment.builder()
                .courseBatch(courseBatch)
                .user(user)
                .createdAt(LocalDateTime.now())
                .reviewStatus(ReviewStatus.PENDING)
                .paymentStatus(PaymentStatus.UNPAID)
                .build();
        enrollmentRepository.save(enrollment);
        return "create enrollment successful";
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_TUTOR')")
    public void handleEnrollCourse(String enrollmentId, boolean isApproved) {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        var request = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_EXISTED));
        CourseBatch courseBatch = courseBatchRepository.findById(request.getCourseBatch().getId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_BATCH_NOT_EXISTED));
        Course course = courseRepository.findById(courseBatch.getCourse().getId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));
        if (!userId.equals(course.getUser().getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        if (isApproved) {
            request.setReviewStatus(ReviewStatus.APPROVED);
            request.setPaymentDeadline(LocalDateTime.now().plusDays(3));
        } else {
            request.setReviewStatus(ReviewStatus.REJECTED);
        }
        enrollmentRepository.save(request);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_TUTOR')")
    public List<EnrollmentResponse> enrollmentOfCourse(String courseBatchId) {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        CourseBatch courseBatch = courseBatchRepository.findById(courseBatchId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_BATCH_NOT_EXISTED));
        Course course = courseRepository.findById(courseBatch.getCourse().getId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));
        if (!userId.equals(course.getUser().getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        var lstEnrollments = enrollmentRepository.findByCourseBatchIdFetch(courseBatchId);
        return lstEnrollments.stream().map(enrollmentMapper::toEnrollmentResponse).toList();
    }

    public List<EnrollmentResponse> myEnrollment() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        var lstEnrollments = enrollmentRepository.findByUserIdFetch(userId);
        return lstEnrollments.stream().map(enrollmentMapper::toEnrollmentResponse).toList();

    }

    public void payEnrollment(String enrollmentId) {
        var request = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_EXISTED));
        if (!request.getReviewStatus().equals(ReviewStatus.APPROVED) ||
                request.getPaymentStatus().equals(PaymentStatus.EXPIRED) ||
                request.getPaymentStatus().equals(PaymentStatus.PAID))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        request.setPaymentStatus(PaymentStatus.PAID);
        enrollmentRepository.save(request);
    }

    public void cancelEnroll(String enrollmentId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        var check = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_EXISTED));
        if (check.getUser().getId().equals(userId))
            throw new AppException(ErrorCode.UNAUTHORIZED);
        enrollmentRepository.deleteById(enrollmentId);
    }

    // 1 a.m every day
    @Scheduled(cron = "0 0 1 * * ?")
    public void expiredRequest() {
        log.info("setting expired enrollment ");
        List<Enrollment> lst = enrollmentRepository.
                findAllExpiredEnroll(LocalDateTime.now())
                .stream().toList();
        log.info("size: {}", lst.size());
        lst.forEach(request -> request.setPaymentStatus(PaymentStatus.EXPIRED));
        enrollmentRepository.saveAll(lst);
    }
}
