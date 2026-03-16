package com.tutor.project.repository;

import com.tutor.project.constant.ReviewStatus;
import com.tutor.project.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;


import java.util.List;

public interface CourseRepository extends JpaRepository<Course,String> {
    @Query("SELECT c from Course c LEFT JOIN FETCH c.schedules WHERE c.reviewStatus=:status")
    List<Course> findAllAvailable(@Param("status") ReviewStatus reviewStatus);
    List<Course> findByReviewStatus(ReviewStatus status);
    List<Course> findByUserId(String userId);
}
