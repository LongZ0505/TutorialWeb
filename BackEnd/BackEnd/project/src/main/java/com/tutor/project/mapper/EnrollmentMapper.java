package com.tutor.project.mapper;

import com.tutor.project.dto.response.EnrollmentResponse;
import com.tutor.project.entity.Enrollment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EnrollmentMapper {
    public EnrollmentResponse toEnrollmentResponse(Enrollment enrollment){
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .paymentStatus(enrollment.getPaymentStatus())
                .username(enrollment.getUser().getUsername())
                .reviewStatus(enrollment.getReviewStatus())
                .createdAt(enrollment.getCreatedAt())
                .paymentDeadline(enrollment.getPaymentDeadline())
                .build();
    }
}
