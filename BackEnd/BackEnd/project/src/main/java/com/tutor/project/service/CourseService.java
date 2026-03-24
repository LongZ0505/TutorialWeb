package com.tutor.project.service;

import com.tutor.project.constant.ReviewStatus;
import com.tutor.project.constant.StatusBatch;
import com.tutor.project.dto.request.CourseBatchCreationRequest;
import com.tutor.project.dto.request.CourseCreationRequest;
import com.tutor.project.dto.request.UpdateCourseBatchRequest;
import com.tutor.project.dto.response.CourseBatchResponse;
import com.tutor.project.dto.response.CourseResponse;
import com.tutor.project.dto.response.ScheduleResponse;
import com.tutor.project.entity.Course;
import com.tutor.project.entity.CourseBatch;
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
    CourseBatchRepository courseBatchRepository;
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
    // course batch
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public void handleCreationCourseBatch(String courseBatchId, boolean isApproved){
        var request=courseBatchRepository.findById(courseBatchId)
                .orElseThrow(()-> new AppException(ErrorCode.COURSE_BATCH_NOT_EXISTED));
        if(isApproved){
            request.setReviewStatus(ReviewStatus.APPROVED);
        }else{
            request.setReviewStatus(ReviewStatus.REJECTED);
        }
        courseBatchRepository.save(request);
    }
    @PreAuthorize("hasAuthority('SCOPE_ROLE_TUTOR')")
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
                .description(request.getDescription())
                .imgURL(request.getImgURL())
                .subject(subject)
                .user(user)
                .reviewStatus(ReviewStatus.PENDING)
                .build();
        courseRepository.save(course);
        return "created Course successful";
    }
    @PreAuthorize("hasAuthority('SCOPE_ROLE_TUTOR')")
    public String createCourseBatch(CourseBatchCreationRequest request){
        var userId= SecurityContextHolder.getContext().getAuthentication().getName();
        var course = courseRepository.findById(request.getCourseId())
                .orElseThrow(()->new AppException(ErrorCode.COURSE_NOT_EXISTED));
        if(!userId.equals(course.getUser().getId())){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        CourseBatch courseBatch= CourseBatch.builder()
                .course(course)
                .price(request.getPrice())
                .maxStudent(request.getMaxStudent())
                .sessions(request.getSessions())
                .statusBatch(StatusBatch.PENDING)
                .reviewStatus(ReviewStatus.PENDING)
                .build();
        courseBatchRepository.save(courseBatch);
        return "created Course Batch successful";
    }
    @PreAuthorize("hasAuthority('SCOPE_ROLE_TUTOR') or hasAuthority('SCOPE_ROLE_ADMIN')")
    public void delete(String courseId){
        String userId=SecurityContextHolder.getContext().getAuthentication().getName();
        var course=courseRepository.findById(courseId).
                orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));
        if(!userId.equals(course.getUser().getId())){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        var lstCourseBatch=courseBatchRepository.findByCourseId(courseId);
        if(!lstCourseBatch.isEmpty()){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        courseRepository.deleteById(courseId);
    }
    @PreAuthorize("hasAuthority('SCOPE_ROLE_TUTOR') or hasAuthority('SCOPE_ROLE_ADMIN')")
    public void cancelCourseBatch(String courseBatchId){
        courseBatchRepository.findById(courseBatchId).
                orElseThrow(() -> new AppException(ErrorCode.COURSE_BATCH_NOT_EXISTED));
        if (!enrollmentRepository.findByCourseBatchIdFetch(courseBatchId).isEmpty()){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        courseBatchRepository.deleteById(courseBatchId);
    }
    @PreAuthorize("hasAuthority('SCOPE_ROLE_TUTOR') ")
    public List<CourseResponse> getAllByUserId(String userId){
        return  courseRepository.findByUserIdFetchCourseBatch(userId)
                .stream().map(courseMapper::toCourseResponse).toList();
    }
    @PreAuthorize("hasAuthority('SCOPE_ROLE_TUTOR') or hasAuthority('SCOPE_ROLE_ADMIN')")
    public void updateCourseBatch(UpdateCourseBatchRequest request){
        var courseBatch= courseBatchRepository.findById(request.getId())
                .orElseThrow(()->new AppException(ErrorCode.COURSE_BATCH_NOT_EXISTED));
        if(request.getPrice()!=courseBatch.getPrice()){
            if(!enrollmentRepository.findByCourseBatchIdAndPaid(request.getId()).isEmpty()){
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }
        courseBatch.setMaxStudent(request.getMaxStudent());
        courseBatch.setPrice(request.getPrice());
        courseBatchRepository.save(courseBatch);
    }
    @PreAuthorize("hasAuthority('SCOPE_ROLE_TUTOR') or hasAuthority('SCOPE_ROLE_ADMIN')")
    public List<CourseResponse> getALlByStatus(String status){
        return courseRepository.findByReviewStatus(ReviewStatus.valueOf(status)).stream()
                .map(courseMapper::toCourseResponse).toList();
    }
    public List<ScheduleResponse> getListSchedule(String courseBatchId){
        return scheduleRepository.findByCourseBatchId(courseBatchId).stream()
                .map(schedule -> ScheduleResponse.builder()
                        .id(schedule.getId())
                        .meetingLink(schedule.getMeetingLink())
                        .startTime(schedule.getStartTime())
                        .endTime(schedule.getEndTime())
                        .dayOfWeek(schedule.getStartTime().getDayOfWeek())
                        .courseBatchId(schedule.getId())
                        .build()).toList();
    }
    //for Student
    public List<CourseResponse> getAll(){
        var roles=SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        log.info("roles: {}",roles);
        if(roles.stream().anyMatch(grantedAuthority -> grantedAuthority.
                getAuthority().equals("SCOPE_ROLE_ADMIN"))){
            return courseRepository.findAllFetchCourseBatch().stream()
                    .map(courseMapper::toCourseResponse).toList();
        }
        return courseRepository.findByReviewStatus(ReviewStatus.APPROVED).stream()
                .map(course ->{
                    course.setCourseBatches(course.getCourseBatches().stream().
                            filter(courseBatch -> !courseBatch.getStatusBatch()
                                    .equals(StatusBatch.COMPLETED)).toList());
                    return courseMapper.toCourseResponse(course);
                }).toList();
    }
    public List<CourseResponse> myCourse(){
        String userId=SecurityContextHolder.getContext().getAuthentication().getName();
         return courseRepository.findByUserIdFetchCourseBatch(userId).stream()
                .map(courseMapper::toCourseResponse).toList();
    }
    public List<CourseBatchResponse> detailCourse(String courseId){
        var lstBatches=courseBatchRepository.findByCourseIdAndFetchSchedule(courseId);
        return lstBatches.stream().map(courseMapper::toCourseBatchResponse).toList();
    }
    public int quantityEnrollment(String courseBatchId){
        courseBatchRepository.findById(courseBatchId).orElseThrow(
                ()->new AppException(ErrorCode.COURSE_BATCH_NOT_EXISTED));
        return enrollmentRepository.quantityStudentOfCourseBatch(courseBatchId);
    }
    @PreAuthorize("hasAuthority('SCOPE_ROLE_TUTOR')")
    public void completedCourseBatch(String courseBatchId){
        String userId= SecurityContextHolder.getContext().getAuthentication().getName();
        var courseBatch=courseBatchRepository.findById(courseBatchId)
                .orElseThrow(()->new AppException(ErrorCode.COURSE_BATCH_NOT_EXISTED));
        var course=courseRepository.findById(courseBatch.getCourse().getId())
                .orElseThrow(()->new AppException(ErrorCode.COURSE_NOT_EXISTED));
        if(!userId.equals(course.getUser().getId()))
            throw new AppException(ErrorCode.UNAUTHORIZED);
        int completed=scheduleRepository.countCompletedScheduleOfCourseBatch(courseBatchId);
        log.info("completed days: {}",completed);
        if(courseBatch.getSessions()>completed)
            throw new AppException(ErrorCode.UNAUTHORIZED);
        courseBatch.setStatusBatch(StatusBatch.COMPLETED);
        courseBatchRepository.save(courseBatch);
    }
}
