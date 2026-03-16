package com.tutor.project.controller;

import com.tutor.project.dto.request.ScheduleCreationRequest;
import com.tutor.project.dto.request.ScheduleUpdateRequest;
import com.tutor.project.dto.response.ApiResponse;
import com.tutor.project.dto.response.ScheduleResponse;
import com.tutor.project.service.ScheduleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedules")
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {
    ScheduleService service;
    @PostMapping
    public ApiResponse<String> create(@RequestBody ScheduleCreationRequest request){
        return ApiResponse.<String>builder()
                .result(service.createSchedule(request))
                .build();
    }
    @DeleteMapping("/{scheduleId}")
    public ApiResponse<?> create(@PathVariable("scheduleId") String scheduleId){
        service.deleteSchedule(scheduleId);
        return ApiResponse.<String>builder()
                .message("deleted")
                .build();
    }
    @PutMapping
    public ApiResponse<?> updated(@RequestBody ScheduleUpdateRequest request){
        service.updateSchedule(request);
        return ApiResponse.builder()
                .message("updated ")
                .build();
    }
    @GetMapping
    public ApiResponse<List<ScheduleResponse>> getSchedules(){
        return ApiResponse.<List<ScheduleResponse>>builder()
                .result(service.mySchedule())
                .build();
    }
}
