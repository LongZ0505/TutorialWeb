package com.tutor.project.service;

import com.tutor.project.constant.PaymentStatus;
import com.tutor.project.constant.Status;
import com.tutor.project.dto.request.ScheduleCreationRequest;
import com.tutor.project.dto.request.ScheduleUpdateRequest;
import com.tutor.project.dto.response.ScheduleResponse;
import com.tutor.project.entity.*;
import com.tutor.project.exception.AppException;
import com.tutor.project.exception.ErrorCode;
import com.tutor.project.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor
public class ScheduleService {
    ScheduleRepository scheduleRepository;
    UserRepository userRepository;
    CourseBatchRepository courseBatchRepository;
    CourseRepository courseRepository;
    EnrollmentRepository enrollmentRepository;
    // for Tutor
    @PreAuthorize("hasAuthority('SCOPE_ROLE_TUTOR')")
    public String createSchedule(ScheduleCreationRequest request) {
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new AppException(ErrorCode.INVALID_DATA);
        }
        var check = scheduleRepository.findByCourseBatchId(request.getCourseBatchId());
        if (check.stream().anyMatch(schedule ->
                schedule.equals(Schedule.builder()
                        .startTime(request.getStartTime())
                        .endTime(request.getEndTime())
                        .build()))) {
            throw new AppException(ErrorCode.SCHEDULE_EXISTED);
        }
        CourseBatch courseBatch = courseBatchRepository.findById(request.getCourseBatchId()).
                orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));
        Schedule schedule = Schedule.builder()
                .courseBatch(courseBatch)
                .status(Status.PENDING) // status of class -> all done then tutor can draw
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .meetingLink(request.getMeetingLink())
                .build();
        scheduleRepository.save(schedule);
        return "create Schedule successful";
    }

    public List<ScheduleResponse> mySchedule() {
        var userId=SecurityContextHolder.getContext().getAuthentication().getName();
        var roles= SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        // student then check enrollment || tutor check courseBatch
        List<Schedule> lst= new ArrayList<>();
        if(roles.stream().anyMatch(grantedAuthority ->
                grantedAuthority.getAuthority().equals("SCOPE_ROLE_TUTOR"))){
            var courses= courseRepository.findAllFetchCourseBatch();
            List<String> ids=courses.stream().map(Course::getId).toList();
            courseBatchRepository.findByCourseIdInFetchSchedule(ids).forEach(
                    courseBatch -> lst.addAll(courseBatch.getSchedules()));
        }else{
            var enrollments= enrollmentRepository.findByUserId(userId);
            List<String> ids= enrollments.stream().map(Enrollment::getId).toList();
            courseBatchRepository.findByIdInFetchSchedule(ids).forEach(
                    courseBatch -> lst.addAll(courseBatch.getSchedules()));
        }
        return lst.stream()
                .map(schedule -> ScheduleResponse.builder()
                                    .id(schedule.getId())
                                    .courseBatchId(schedule.getCourseBatch().getId())
                                    .dayOfWeek(schedule.getStartTime().getDayOfWeek())
                                    .endTime(schedule.getEndTime())
                                    .startTime(schedule.getStartTime())
                                    .meetingLink(schedule.getMeetingLink())
                                    .build()).toList();

    }

    public void deleteSchedule(String scheduleId) {
        scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_EXISTED));
        scheduleRepository.deleteById(scheduleId);
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_TUTOR')")
    public void updateSchedule(ScheduleUpdateRequest request) {
        var schedule = scheduleRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_EXISTED));
        // send email to student in enrollment
        schedule.setEndTime(request.getEndTime());
        schedule.setStartTime(request.getStartTime());
        schedule.setMeetingLink(request.getMeetingLink());
        scheduleRepository.save(schedule);
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void expiredRequest() {
        log.info("checking report class");
        // check report

        // check report
        List<Schedule> lst = scheduleRepository.findByDate(LocalDateTime.now().minusHours(24)
                , LocalDateTime.now());
        lst.forEach(schedule -> schedule.setStatus(Status.DONE));
    }
}
