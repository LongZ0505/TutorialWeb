package com.tutor.project.entity;

import com.tutor.project.constant.ReviewStatus;
import com.tutor.project.constant.StatusBatch;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @ManyToOne
    @JoinColumn(name = "course_id",nullable = false)
    Course course;
    double price;
    int sessions;
    int maxStudent;
    @Enumerated(EnumType.STRING)
    StatusBatch statusBatch;
    @Enumerated(EnumType.STRING)
    ReviewStatus reviewStatus;
    @OneToMany(mappedBy = "courseBatch")
    List<Schedule> schedules;
}
