package com.tutor.project.repository;

import com.tutor.project.constant.ReviewStatus;
import com.tutor.project.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;


import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course,String> {
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.courseBatches WHERE c.reviewStatus=:status ")
    List<Course> findByReviewStatus(@Param("status") ReviewStatus status);
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.courseBatches WHERE c.user.id=:userId")
    List<Course> findByUserIdFetchCourseBatch(@Param("userId") String userId);
    List<Course> findByUserId(String userId);
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.courseBatches")
    List<Course> findAllFetchCourseBatch();
}
