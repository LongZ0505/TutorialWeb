package com.tutor.project.repository;

import com.tutor.project.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,String> {
    List<Schedule> findByCourseId(String courseId);
    Optional<Schedule> findByUserIdAndCourseId(String userId,String courseId);
    @Query("SELECT s FROM Schedule s JOIN FETCH s.course WHERE s.user.id=:userId")
    List<Schedule> findByUserId(@Param("userId") String userId);
    @Query("SELECT s FROM Schedule s WHERE s.startTime >:startDate " +
            "and s.startTime<:endDate ")
    List<Schedule> findByDate(@Param("startDate")LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate);
}
