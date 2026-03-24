package com.tutor.project.controller;

import com.tutor.project.dto.request.CourseBatchCreationRequest;
import com.tutor.project.dto.request.CourseCreationRequest;
import com.tutor.project.dto.request.UpdateCourseBatchRequest;
import com.tutor.project.dto.response.ApiResponse;
import com.tutor.project.dto.response.CourseBatchResponse;
import com.tutor.project.dto.response.CourseResponse;
import com.tutor.project.dto.response.ScheduleResponse;
import com.tutor.project.service.CourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class CourseController {
    CourseService courseService;
    @PostMapping
    public ApiResponse<String> create(@RequestBody CourseCreationRequest request){
        return ApiResponse.<String>builder()
                .result(courseService.create(request))
                .build();
    }
    @PostMapping("/batches")
    public ApiResponse<String> createCourseBatch(@RequestBody CourseBatchCreationRequest request){
        return ApiResponse.<String>builder()
                .result(courseService.createCourseBatch(request))
                .build();
    }
    @DeleteMapping("/{courseId}")
    public ApiResponse<?> delete(@PathVariable("courseId") String courseId){
        courseService.delete(courseId);
        return ApiResponse.builder()
                .result("deleted")
                .build();
    }
    @DeleteMapping("/batches/{courseBatchId}")
    public ApiResponse<?> deleteCourseBatch(@PathVariable("courseBatchId") String courseBatchId){
        courseService.cancelCourseBatch(courseBatchId);
        return ApiResponse.builder()
                .result("deleted")
                .build();
    }
    @PutMapping("/review/{courseId}")
    public ApiResponse<?> handleStatus(@PathVariable("courseId") String courseId
            , @RequestParam("isApproved") boolean isApproved){
        courseService.handleCreationCourse(courseId,isApproved);
        return ApiResponse.builder()
                .result("handled")
                .build();
    }
    @PutMapping("/reviewBatch/{courseBatchId}")
    public ApiResponse<?> handleStatusBatch(@PathVariable("courseBatchId") String courseBatchId
            , @RequestParam("isApproved") boolean isApproved){
        courseService.handleCreationCourseBatch(courseBatchId,isApproved);
        return ApiResponse.builder()
                .result("handled")
                .build();
    }
    @PutMapping
    public ApiResponse<?> update(@RequestBody UpdateCourseBatchRequest request){
        courseService.updateCourseBatch(request);
        return ApiResponse.builder()
                .result("updated")
                .build();
    }
    @GetMapping("/{userId}")
    public ApiResponse<List<CourseResponse>> getAllByUserId
            (@PathVariable("userId")String userId){
        return ApiResponse.<List<CourseResponse>>builder()
                .result(courseService.getAllByUserId(userId))
                .build();
    }
    @GetMapping("/status")
    public ApiResponse<List<CourseResponse>> getAllByStatus
            (@RequestParam("status") String status){
        return ApiResponse.<List<CourseResponse>>builder()
                .result(courseService.getALlByStatus(status))
                .build();
    }
    @GetMapping
    public ApiResponse<List<CourseResponse>> getAllCourse(){
        return ApiResponse.<List<CourseResponse>>builder()
                .result(courseService.getAll())
                .build();
    }
    @GetMapping("/schedule/{courseId}")
    public ApiResponse<List<ScheduleResponse>> getSchedule
            (@PathVariable("courseId") String courseId){
        return ApiResponse.<List<ScheduleResponse>>builder()
                .result(courseService.getListSchedule(courseId))
                .build();
    }
    @GetMapping("/myself")
    public ApiResponse<List<CourseResponse  >> myCourse(){
        return ApiResponse.<List<CourseResponse>>builder()
                .result(courseService.myCourse())
                .build();
    }
    @GetMapping("/details/{courseId}")
    public ApiResponse<List<CourseBatchResponse>> myCourse(
            @PathVariable("courseId") String courseId){
        return ApiResponse.<List<CourseBatchResponse>>builder()
                .result(courseService.detailCourse(courseId))
                .build();
    }
    @PutMapping("/completed/{courseBatchId}")
    public ApiResponse<?> completedCourseBatch(@PathVariable("courseBatchId") String courseBatchId){
        courseService.completedCourseBatch(courseBatchId);
        return ApiResponse.builder()
                .message("done courseBatch")
                .build();
    }
}
