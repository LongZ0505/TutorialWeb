package com.tutor.project.service;

import com.tutor.project.repository.ReportScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor
public class ReportScheduleService {
    ReportScheduleRepository reportScheduleRepository;
}
