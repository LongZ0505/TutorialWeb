package com.tutor.project.dto.response;

import com.tutor.project.constant.ReviewStatus;
import com.tutor.project.constant.StatusBatch;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseBatchResponse {
    String id;
    int sessions;
    double price;
    int maxStudent;
    List<ScheduleResponse> scheduleResponseList;
    ReviewStatus reviewStatus;
    StatusBatch statusBatch;
}
