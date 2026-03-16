package com.tutor.project.dto.request;

import com.tutor.project.entity.Subject;
import com.tutor.project.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseCreationRequest {
    double price;
    int sessions;
    String subjectId;
    String title;
    String description;
    String imgURL;
    int maxStudent;
    LocalDateTime createdAt;
}
