package com.tutor.project.repository;

import com.tutor.project.entity.CourseBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseBatchRepository extends JpaRepository<CourseBatch,String> {
    List<CourseBatch> findByCourseId(String courseId);
    @Query("SELECT cb FROM CourseBatch cb LEFT JOIN FETCH cb.schedules WHERE cb.course.id=:courseId")
    List<CourseBatch> findByCourseIdAndFetchSchedule(@Param("courseId") String courseId);
    @Query(" SELECT DISTINCT cb FROM CourseBatch cb LEFT " +
            "JOIN FETCH cb.schedules WHERE cb.id IN :ids")
    List<CourseBatch> findByIdInFetchSchedule(@Param("ids") List<String> ids);
    @Query(" SELECT DISTINCT cb FROM CourseBatch cb LEFT " +
            "JOIN FETCH cb.schedules WHERE cb.course.id IN :courseIds")
    List<CourseBatch> findByCourseIdInFetchSchedule(@Param("courseIds") List<String> courseIds);
}
