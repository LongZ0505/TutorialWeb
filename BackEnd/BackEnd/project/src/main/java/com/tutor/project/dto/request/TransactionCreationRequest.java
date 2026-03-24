package com.tutor.project.dto.request;

import com.tutor.project.entity.Enrollment;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionCreationRequest {
    String enrollmentId;
    String updateRoleRequestId;
    double amount;
}
