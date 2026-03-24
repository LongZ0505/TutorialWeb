package com.tutor.project.mapper;

import com.tutor.project.dto.response.CourseBatchResponse;
import com.tutor.project.dto.response.CourseResponse;
import com.tutor.project.dto.response.ScheduleResponse;
import com.tutor.project.entity.Course;
import com.tutor.project.entity.CourseBatch;
import com.tutor.project.entity.Schedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CourseMapper {
    public CourseResponse toCourseResponse(Course course){
        return CourseResponse.builder()
                .id(course.getId())
                .subjectId(course.getSubject().getId())
                .title(course.getTitle())
                .imgURL(course.getImgURL())
                .courseBatchResponses(course.getCourseBatches().stream()
                        .map(courseBatch -> CourseBatchResponse.builder()
                                .price(courseBatch.getPrice())
                                .statusBatch(courseBatch.getStatusBatch())
                                .sessions(courseBatch.getSessions())
                                .maxStudent(courseBatch.getMaxStudent())
                                .id(courseBatch.getId())
                                .reviewStatus(courseBatch.getReviewStatus())
                                .statusBatch(courseBatch.getStatusBatch())
                                .build()).toList())
                .description(course.getDescription())
                .reviewStatus(course.getReviewStatus())
                .build();
    }
    public CourseBatchResponse toCourseBatchResponse(CourseBatch courseBatch){
        return CourseBatchResponse.builder()
                .price(courseBatch.getPrice())
                .statusBatch(courseBatch.getStatusBatch())
                .sessions(courseBatch.getSessions())
                .maxStudent(courseBatch.getMaxStudent())
                .id(courseBatch.getId())
                .reviewStatus(courseBatch.getReviewStatus())
                .statusBatch(courseBatch.getStatusBatch())
                .scheduleResponseList(courseBatch.getSchedules().stream().map(schedule ->
                        ScheduleResponse.builder()
                                .courseBatchId(schedule.getCourseBatch().getId())
                                .endTime(schedule.getEndTime())
                                .startTime(schedule.getStartTime())
                                .meetingLink(schedule.getMeetingLink())
                                .dayOfWeek(schedule.getStartTime().getDayOfWeek())
                                .build()
                        ).toList())
                .build();
    }
}
