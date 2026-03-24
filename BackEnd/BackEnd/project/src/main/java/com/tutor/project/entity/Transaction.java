package com.tutor.project.entity;

import com.tutor.project.constant.TransactionStatus;
import com.tutor.project.constant.TransactionType;
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
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
    @ManyToOne
    @JoinColumn(name = "enrollment_id")
    Enrollment enrollment;
    @ManyToOne
    @JoinColumn(name = "updateRoleRequest_id")
    UpdateRoleRequest updateRoleRequest;
    double amount;
    @Enumerated(EnumType.STRING)
    TransactionStatus status;
    @Enumerated(EnumType.STRING)
    TransactionType type;
    String gateway;
    String gatewayTransactionId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
