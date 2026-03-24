package com.tutor.project.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleCreationRequest {
    String courseBatchId;
    LocalDateTime startTime;
    LocalDateTime endTime;
    String meetingLink;
}
