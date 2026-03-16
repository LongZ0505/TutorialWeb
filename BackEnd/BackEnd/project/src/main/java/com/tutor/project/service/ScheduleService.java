package com.tutor.project.service;

import com.tutor.project.constant.PaymentStatus;
import com.tutor.project.constant.Status;
import com.tutor.project.dto.request.ScheduleCreationRequest;
import com.tutor.project.dto.request.ScheduleUpdateRequest;
import com.tutor.project.dto.response.ScheduleResponse;
import com.tutor.project.entity.Course;
import com.tutor.project.entity.Schedule;
import com.tutor.project.entity.UpdateRoleRequest;
import com.tutor.project.entity.User;
import com.tutor.project.exception.AppException;
import com.tutor.project.exception.ErrorCode;
import com.tutor.project.repository.CourseRepository;
import com.tutor.project.repository.ScheduleRepository;
import com.tutor.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor
public class ScheduleService {
    ScheduleRepository scheduleRepository;
    UserRepository userRepository;
    CourseRepository courseRepository;

    // for Tutor
    @PreAuthorize("hasAuthority('SCOPE_ROLE_TUTOR')")
    public String createSchedule(ScheduleCreationRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new AppException(ErrorCode.INVALID_DATA);
        }
        var check = scheduleRepository.findByUserId(userId);
        if (check.stream().anyMatch(schedule ->
                schedule.equals(Schedule.builder()
                        .startTime(request.getStartTime())
                        .endTime(request.getEndTime())
                        .build()))) {
            throw new AppException(ErrorCode.SCHEDULE_EXISTED);
        }
        User user = userRepository.findById(userId).
                orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Course course = courseRepository.findById(request.getCourseId()).
                orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));
        Schedule schedule = Schedule.builder()
                .course(course)
                .status(Status.PENDING) // status of class -> all done then tutor can draw
                .user(user)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .meetingLink(request.getMeetingLink())
                .build();
        scheduleRepository.save(schedule);
        return "create Schedule successful";
    }

    public List<ScheduleResponse> mySchedule() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return scheduleRepository.findByUserId(userId).stream()
                .map(schedule ->
                        ScheduleResponse.builder()
                                .id(schedule.getId())
                                .courseId(schedule.getCourse().getId())
                                .courseTitle(schedule.getCourse().getTitle())
                                .dayOfWeek(schedule.getStartTime().getDayOfWeek())
                                .endTime(schedule.getEndTime())
                                .startTime(schedule.getStartTime())
                                .meetingLink(schedule.getMeetingLink())
                                .build()).toList();
    }

    public void deleteSchedule(String scheduleId) {
        String userId=SecurityContextHolder.getContext().getAuthentication().getName();
        var check=scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_EXISTED));
        if(!check.getUser().getId().equals(userId))
            throw new AppException(ErrorCode.UNAUTHORIZED);
        scheduleRepository.deleteById(scheduleId);
    }
    @PreAuthorize("hasAuthority('SCOPE_ROLE_TUTOR')")
    public void updateSchedule(ScheduleUpdateRequest request) {
        String userId=SecurityContextHolder.getContext().getAuthentication().getName();
        var schedule = scheduleRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_EXISTED));
        if(!schedule.getUser().getId().equals(userId))
            throw new AppException(ErrorCode.UNAUTHORIZED);
        // send email to student
        schedule.setEndTime(request.getEndTime());
        schedule.setStartTime(request.getStartTime());
        schedule.setMeetingLink(request.getMeetingLink());
        scheduleRepository.save(schedule);
    }
    @Scheduled(fixedRate = 3600000)
    public void expiredRequest() {
        log.info("checking report class");
        // check report

        // check report
        List<Schedule> lst= scheduleRepository.findByDate(LocalDateTime.now().minusHours(24)
                ,LocalDateTime.now());
        lst.forEach(schedule -> schedule.setStatus(Status.DONE));
    }
}
