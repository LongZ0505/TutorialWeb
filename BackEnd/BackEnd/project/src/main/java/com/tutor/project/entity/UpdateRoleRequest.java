package com.tutor.project.entity;

import com.tutor.project.constant.PaymentStatus;
import com.tutor.project.constant.ReviewStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateRoleRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @ManyToOne
    @JoinColumn(name="user_id",nullable = false)
    User user;
    String cvImage;
    ReviewStatus reviewStatus;
    PaymentStatus paymentStatus;
    LocalDateTime paymentDeadline;
    LocalDateTime requestedAt;
}
