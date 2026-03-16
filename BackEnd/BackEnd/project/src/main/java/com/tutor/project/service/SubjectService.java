package com.tutor.project.service;

import com.tutor.project.dto.request.SubjectCreationRequest;
import com.tutor.project.dto.response.SubjectResponse;
import com.tutor.project.entity.Subject;
import com.tutor.project.exception.AppException;
import com.tutor.project.exception.ErrorCode;
import com.tutor.project.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor
public class SubjectService {
    SubjectRepository subjectRepository;
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public String createSubject(SubjectCreationRequest request) {
        var check = subjectRepository.findByName(request.getName());
        if (check.isPresent()) {
            throw new AppException(ErrorCode.SUBJECT_EXISTED);
        }
        subjectRepository.save(Subject.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build());
        return "created Subject successful";
    }
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public void deleteSubject(String id){
        subjectRepository.findById(id).
                orElseThrow(() -> new AppException(ErrorCode.SUBJECT_NOT_EXISTED));
        subjectRepository.deleteById(id);
    }
    public List<SubjectResponse> getAll(){
        return subjectRepository.findAll().stream()
                .map(subject -> SubjectResponse.builder().id(subject.getId()).name(subject.getName())
                        .description(subject.getDescription()).build()).toList();
    }
}
