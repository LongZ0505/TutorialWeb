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
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    Course course;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;
    @Enumerated(EnumType.STRING)
    ReviewStatus reviewStatus;
    @Enumerated(EnumType.STRING)
    PaymentStatus paymentStatus;
    LocalDateTime paymentDeadline;
    LocalDateTime createdAt;
}
