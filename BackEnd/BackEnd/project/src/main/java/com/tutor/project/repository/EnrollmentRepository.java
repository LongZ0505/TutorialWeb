package com.tutor.project.repository;

import com.tutor.project.entity.Enrollment;
import com.tutor.project.entity.UpdateRoleRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment,String> {
    @Query("SELECT e FROM Enrollment e WHERE e.courseBatch.id=:courseBatchId" +
            " AND e.paymentStatus = PAID")
    List<Enrollment> findByCourseBatchIdAndPaid(@Param("courseBatchId") String courseBatchId);
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.user " +
            "JOIN FETCH e.courseBatch WHERE e.courseBatch.id=:courseBatchId")
    List<Enrollment> findByCourseBatchIdFetch(@Param("courseBatchId")String courseBatchId);
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.user " +
            "JOIN FETCH e.courseBatch WHERE e.user.id=:userId ")
    List<Enrollment> findByUserIdFetch(@Param("userId")String userId);
    @Query("SELECT e FROM Enrollment e WHERE e.user.id=:userId AND e.courseBatch.id=:courseBatchId" +
            " AND e.paymentStatus != EXPIRED")
    Optional<Enrollment> findByUserIdAndCourseBatchId(@Param("userId") String userId,
                                                 @Param("courseBatchId") String courseBatchId);
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.courseBatch.id=:courseBatchId " +
            "and e.reviewStatus = APPROVED and e.paymentStatus = PAID")
    int quantityStudentOfCourseBatch(@Param("courseBatchId") String courseBatchId);
    @Query("SELECT e FROM Enrollment e " +
            "WHERE e.paymentStatus = UNPAID and e.paymentDeadline < :time")
    Optional<Enrollment>findAllExpiredEnroll(@Param("time") LocalDateTime time);
    List<Enrollment> findByUserId(String userId);

}
