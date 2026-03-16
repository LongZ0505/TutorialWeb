package com.tutor.project.controller;

import com.tutor.project.dto.request.SubjectCreationRequest;
import com.tutor.project.dto.response.ApiResponse;
import com.tutor.project.dto.response.SubjectResponse;
import com.tutor.project.service.SubjectService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subjects")
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class SubjectController {
    SubjectService service;
    @PostMapping
    public ApiResponse<String> create(@RequestBody SubjectCreationRequest request){
        return ApiResponse.<String>builder()
                .result(service.createSubject(request))
                .build();
    }
    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable("id") String id){
        service.deleteSubject(id);
        return ApiResponse.builder()
                .message("Subject has been deleted")
                .build();
    }
    @GetMapping
    public ApiResponse<List<SubjectResponse>> getAll(){
        return ApiResponse.<List<SubjectResponse>>builder()
                .result(service.getAll())
                .build();
    }
}
