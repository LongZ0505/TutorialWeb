package com.tutor.project.repository;

import com.tutor.project.entity.ReportSchedule;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportScheduleRepository extends JpaRepository<ReportSchedule,String> {
}
