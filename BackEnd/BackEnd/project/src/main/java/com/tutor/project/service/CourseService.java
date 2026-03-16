package com.tutor.project.service;

import com.tutor.project.constant.ReviewStatus;
import com.tutor.project.dto.request.CourseCreationRequest;
import com.tutor.project.dto.request.UpdateCourseRequest;
import com.tutor.project.dto.response.CourseResponse;
import com.tutor.project.dto.response.ScheduleResponse;
import com.tutor.project.entity.Course;
import com.tutor.project.entity.Subject;
import com.tutor.project.entity.User;
import com.tutor.project.exception.AppException;
import com.tutor.project.exception.ErrorCode;
import com.tutor.project.mapper.CourseMapper;
import com.tutor.project.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor
public class CourseService {
    CourseRepository courseRepository;
    SubjectRepository subjectRepository;
    UserRepository userRepository;
    CourseMapper courseMapper;
    ScheduleRepository scheduleRepository;
    EnrollmentRepository enrollmentRepository;
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public void handleCreationCourse(String courseId, boolean isApproved){
        var request=courseRepository.findById(courseId)
                .orElseThrow(()-> new AppException(ErrorCode.COURSE_NOT_EXISTED));
        if(isApproved){
            request.setReviewStatus(ReviewStatus.APPROVED);
        }else{
            request.setReviewStatus(ReviewStatus.REJECTED);
        }
        courseRepository.save(request);
    }
    @PreAuthorize("hasAuthority('SCOPE_ROLE_TUTOR') or hasAuthority('SCOPE_ROLE_ADMIN') ")
    public String create(CourseCreationRequest request){
        var userId= SecurityContextHolder.getContext().getAuthentication().getName();
        var check = courseRepository.findByUserId(userId);
        if(check.stream().anyMatch(course -> course.getTitle().equalsIgnoreCase(request.getTitle()))){
            throw new AppException(ErrorCode.COURSE_EXISTED);
        }
        User user=userRepository.findById(userId).orElseThrow(
                ()->new AppException(ErrorCode.USER_NOT_EXISTED));
        Subject subject =subjectRepository.findById(request.getSubjectId()).orElseThrow(
                () -> new AppException(ErrorCode.SUBJECT_NOT_EXISTED));
        Course course= Course.builder()
                .title(request.getTitle())
                .price(request.getPrice())
                .sessions(request.getSessions())
                .createdAt(LocalDateTime.now())
                .description(request.getDescription())
                .maxStudent(request.getMaxStudent())
                .imgURL(request.getImgURL())
                .subject(subject)
                .user(user)
                .reviewStatus(ReviewStatus.PENDING)
                .build();
        courseRepository.save(course);
        return "created Course successful";
    }
    @PreAuthorize("hasAuthority('SCOPE_ROLE_TUTOR') or hasAuthority('SCOPE_ROLE_ADMIN')")
    public void delete(String courseId){
        String userId=SecurityContextHolder.getContext().getAuthentication().getName();
        var course=courseRepository.findById(courseId).
                orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));
        if(!userId.equals(course.getUser().getId())){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        courseRepository.deleteById(courseId);
    }
    @PreAuthorize("hasAuthority('SCOPE_ROLE_TUTOR') ")
    public List<CourseResponse> getAllByUserId(String userId){
        return  courseRepository.findByUserId(userId)
                .stream().map(courseMapper::toCourseResponse).toList();
    }
    @PreAuthorize("hasAuthority('SCOPE_ROLE_TUTOR') or hasAuthority('SCOPE_ROLE_ADMIN')")
    public void update(UpdateCourseRequest request){
        String userId=SecurityContextHolder.getContext().getAuthentication().getName();
        var course=courseRepository.findById(request.getId()).
                orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));
        if(!userId.equals(course.getUser().getId())){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        if(request.getPrice()!=course.getPrice()){
            if(!enrollmentRepository.findByCourseId(request.getId()).isEmpty()){
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }
        course.setMaxStudent(request.getMaxStudent());
        course.setPrice(request.getPrice());
        courseRepository.save(course);
    }
    @PreAuthorize("hasAuthority('SCOPE_ROLE_TUTOR') or hasAuthority('SCOPE_ROLE_ADMIN')")
    public List<CourseResponse> getALlByStatus(String status){
        return courseRepository.findByReviewStatus(ReviewStatus.valueOf(status)).stream()
                .map(courseMapper::toCourseResponse).toList();
    }
    public List<ScheduleResponse> getListSchedule(String courseId){
        return scheduleRepository.findByCourseId(courseId).stream()
                .map(schedule -> ScheduleResponse.builder()
                        .id(schedule.getId())
                        .meetingLink(schedule.getMeetingLink())
                        .startTime(schedule.getStartTime())
                        .endTime(schedule.getEndTime())
                        .dayOfWeek(schedule.getStartTime().getDayOfWeek())
                        .courseId(schedule.getId())
                        .build()).toList();
    }
    //for Student
    public List<CourseResponse> getAll(){
        var roles=SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        log.info("roles: {}",roles);
        if(roles.stream().anyMatch(grantedAuthority -> grantedAuthority.
                getAuthority().equals("SCOPE_ROLE_ADMIN"))){
            return courseRepository.findAll().stream()
                    .map(courseMapper::toCourseResponse).toList();
        }
        return courseRepository.findAllAvailable(ReviewStatus.APPROVED).stream()
                .map(courseMapper::toCourseResponse).toList();
    }
    public List<CourseResponse> myCourse(){
        String userId=SecurityContextHolder.getContext().getAuthentication().getName();
        return courseRepository.findByUserId(userId).stream()
                .map(courseMapper::toCourseResponse).toList();
    }
    public int quantityEnrollment(String courseId){
        courseRepository.findById(courseId).orElseThrow(
                ()->new AppException(ErrorCode.COURSE_NOT_EXISTED));
        return enrollmentRepository.quantityStudentOfCourse(courseId);
    }
}
