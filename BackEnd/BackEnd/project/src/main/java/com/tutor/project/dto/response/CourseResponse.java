package com.tutor.project.dto.response;

import com.tutor.project.constant.ReviewStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseResponse {
    String id;
    int sessions;
    double price;
    String subjectId;
    String title;
    String description;
    String imgURL;
    int maxStudent;
    List<ScheduleResponse> scheduleResponseList;
    LocalDateTime createdAt;
    ReviewStatus reviewStatus;
}
