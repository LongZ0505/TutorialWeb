package com.tutor.project.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseBatchCreationRequest {
    String courseId;
    double price;
    int sessions;
    int maxStudent;
}
