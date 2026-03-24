package com.tutor.project.entity;

import com.tutor.project.constant.ReviewStatus;
import com.tutor.project.constant.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @ManyToOne
    @JoinColumn(name = "courseBatch_id", nullable = false)
    CourseBatch courseBatch;
    @Enumerated(EnumType.STRING)
    Status status;
    LocalDateTime startTime;
    LocalDateTime endTime;
    String meetingLink;
    public boolean equals(Schedule schedule) {
        if (this.getStartTime().equals(schedule.getStartTime())
                && this.getEndTime().equals(schedule.getEndTime()))
            return true;
        return false;
    }
}
