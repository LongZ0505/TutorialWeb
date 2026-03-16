package com.tutor.project.dto.response;

import com.tutor.project.constant.PaymentStatus;
import com.tutor.project.constant.ReviewStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollmentResponse {
    String id;
    String titleOfCourse;
    String username;
    ReviewStatus reviewStatus;
    PaymentStatus paymentStatus;
    LocalDateTime createdAt;
    LocalDateTime paymentDeadline;
}
