package com.tutor.project.mapper;

import com.tutor.project.dto.response.CourseResponse;
import com.tutor.project.dto.response.ScheduleResponse;
import com.tutor.project.entity.Course;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CourseMapper {
    public CourseResponse toCourseResponse(Course course){
        return CourseResponse.builder()
                .id(course.getId())
                .price(course.getPrice())
                .sessions(course.getSessions())
                .subjectId(course.getSubject().getId())
                .title(course.getTitle())
                .imgURL(course.getImgURL())
                .maxStudent(course.getMaxStudent())
                .createdAt(course.getCreatedAt())
                .scheduleResponseList(course.getSchedules().stream().map(
                        schedule -> ScheduleResponse.builder()
                                .id(schedule.getId())
                                .courseId(schedule.getCourse().getId())
                                .dayOfWeek(schedule.getStartTime().getDayOfWeek())
                                .endTime(schedule.getEndTime())
                                .startTime(schedule.getStartTime())
                                .meetingLink(schedule.getMeetingLink())
                                .build()).toList()
                )
                .description(course.getDescription())
                .reviewStatus(course.getReviewStatus())
                .build();
    }
}
