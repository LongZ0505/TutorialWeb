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
            " WHERE u.user.id=:userId and u.reviewStatus = 2 or u.reviewStatus= 1 ")
    Optional<UpdateRoleRequest> findByUserId(@Param("userId") String userId);
    @Query("SELECT u FROM UpdateRoleRequest u " +
            "WHERE u.paymentStatus= 0 and u.paymentDeadline > :time")
    Optional<UpdateRoleRequest>findAllExpiredRequest(@Param("time") LocalDateTime time);
}
