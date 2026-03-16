package com.tutor.project.repository;

import com.tutor.project.entity.Enrollment;
import com.tutor.project.entity.UpdateRoleRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment,String> {
    List<Enrollment> findByCourseId(String courseId);
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.user " +
            "JOIN FETCH e.course WHERE e.course.id=:courseId")
    List<Enrollment> findByCourseIdFetch(@Param("courseId")String courseId);
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.user " +
            "JOIN FETCH e.course WHERE e.user.id=:userId")
    List<Enrollment> findByUserIdFetch(@Param("userId")String userId);
    Optional<Enrollment> findByUserIdAndCourseId(String userId, String courseId);
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id=:courseId " +
            "and e.reviewStatus = APPROVED and e.paymentStatus = PAID")
    int quantityStudentOfCourse(@Param("courseId") String courseId);
    @Query("SELECT e FROM Enrollment e " +
            "WHERE e.paymentStatus = UNPAID and e.paymentDeadline > :time")
    Optional<Enrollment>findAllExpiredEnroll(@Param("time") LocalDateTime time);

}
