package com.tutor.project.dto.response;

import com.tutor.project.constant.PaymentStatus;
import com.tutor.project.constant.ReviewStatus;
import com.tutor.project.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateRoleResponse {
    String id;
    PaymentStatus paymentStatus;
    LocalDateTime paymentDeadline;
    LocalDateTime requestedAt;
    ReviewStatus reviewStatus;
    String cvImage;
}
