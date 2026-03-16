package com.tutor.project.controller;

import com.tutor.project.dto.request.EnrollmentCreationRequest;
import com.tutor.project.dto.response.ApiResponse;
import com.tutor.project.dto.response.EnrollmentResponse;
import com.tutor.project.service.EnrollmentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enrollments")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class EnrollmentController {
    EnrollmentService enrollmentService;

    @PostMapping
    public ApiResponse<String> enrollCourse(
            @RequestBody EnrollmentCreationRequest request) {
        return ApiResponse.<String>builder()
                .result(enrollmentService.enrollCourse(request))
                .build();
    }

    @PutMapping("/handle/{enrollmentId}")
    public ApiResponse<?> handledEnrollment
            (@PathVariable("enrollmentId") String enrollmentId,
             @RequestParam("isApproved") boolean isApproved) {
        enrollmentService.handleEnrollCourse(enrollmentId,isApproved);
        return ApiResponse.builder()
                .message("handled enrollment")
                .build();
    }
    @PutMapping("/payment/{enrollmentId}")
    public ApiResponse<?> payment(@PathVariable("enrollmentId") String enrollmentId){
        enrollmentService.payEnrollment(enrollmentId);
        return ApiResponse.builder()
                .message("paid for enrollment")
                .build();
    }
    @GetMapping("/courseId/{courseId}")
    public ApiResponse<List<EnrollmentResponse>> enrollmentOfCourse
            (@PathVariable("courseId") String courseId){
        return ApiResponse.<List<EnrollmentResponse>>builder()
                .result(enrollmentService.enrollmentOfCourse(courseId))
                .build();
    }
    @GetMapping("/userId")
    public ApiResponse<List<EnrollmentResponse>> enrollmentOfUser(){
        return ApiResponse.<List<EnrollmentResponse>>builder()
                .result(enrollmentService.myEnrollment())
                .build();
    }
    @DeleteMapping("/{enrollmentId}")
    public ApiResponse<?> delete(@PathVariable("enrollmentId") String enrollmentId){
        enrollmentService.cancelEnroll(enrollmentId);
        return ApiResponse.builder()
                .message("deleted")
                .build();
    }
}
