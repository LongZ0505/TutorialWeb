package com.tutor.project.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleResponse {
    String id;
    String courseId;
    String courseTitle;
    DayOfWeek dayOfWeek;
    LocalDateTime startTime;
    LocalDateTime endTime;
    String meetingLink;
}
