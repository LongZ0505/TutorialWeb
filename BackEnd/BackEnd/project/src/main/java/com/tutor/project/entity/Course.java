package com.tutor.project.entity;

import com.tutor.project.constant.ReviewStatus;
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
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @ManyToOne
    @JoinColumn(name = "subject_id",nullable = false)
    Subject subject;
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    User user;
    String title;
    String description;
    String imgURL;
    double price ;
    int sessions;
    @Enumerated(EnumType.STRING)
    ReviewStatus reviewStatus;
    int maxStudent;
    LocalDateTime createdAt;
    @OneToMany(mappedBy = "course")
    List<Schedule> schedules;
}
