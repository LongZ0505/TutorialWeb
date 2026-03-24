package com.tutor.project.repository;


import com.tutor.project.constant.ReviewStatus;
import com.tutor.project.entity.UpdateRoleRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UpdateRoleRequestRepository extends JpaRepository<UpdateRoleRequest,String> {
    @Query("SELECT u FROM UpdateRoleRequest u " +
            " WHERE u.user.id=:userId and u.reviewStatus = APPROVED or u.reviewStatus= PENDING ")
    Optional<UpdateRoleRequest> findByUserId(@Param("userId") String userId);
    @Query("SELECT u FROM UpdateRoleRequest u " +
            "WHERE u.paymentStatus = UNPAID and u.paymentDeadline < :time")
    Optional<UpdateRoleRequest>findAllExpiredRequest(@Param("time") LocalDateTime time);
}
