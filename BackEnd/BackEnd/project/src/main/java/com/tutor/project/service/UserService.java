package com.tutor.project.service;

import com.nimbusds.jose.KeyLengthException;
import com.tutor.project.constant.PaymentStatus;
import com.tutor.project.constant.ReviewStatus;
import com.tutor.project.dto.request.UpdateRoleCreationRequest;
import com.tutor.project.dto.request.UpdateUserRequest;
import com.tutor.project.dto.request.UserCreationRequest;
import com.tutor.project.dto.response.UserResponse;
import com.tutor.project.entity.UpdateRoleRequest;
import com.tutor.project.entity.User;
import com.tutor.project.exception.AppException;
import com.tutor.project.exception.ErrorCode;
import com.tutor.project.mapper.UserMapper;
import com.tutor.project.repository.RoleRepository;
import com.tutor.project.repository.UpdateRoleRequestRepository;
import com.tutor.project.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    AuthenticationService authenticationService;
    RoleRepository roleRepository;
    UpdateRoleRequestRepository updateRoleRequestRepository;
    public String registry(UserCreationRequest request) throws KeyLengthException {
        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user =userMapper.toUser(request);
        log.info(user.getUsername());
        var role=roleRepository.findByName("STUDENT").orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        user.setRoles(Set.of(role));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);
        return authenticationService.generateToken(user.getId());
    }
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public List<UserResponse> findAll(){
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse).toList();
    }
    public List<UserResponse> findByFullNameContainKeyword(String keyword){
        List<UserResponse> lstUsers= new ArrayList<>();
        if(userRepository.findFullNamContainKeyword("%"+keyword+"%").isPresent()) {
            lstUsers = userRepository.findFullNamContainKeyword("%" + keyword + "%")
                    .get().stream().map(userMapper::toUserResponse).toList();
        }
        return lstUsers;
    }
    public void delete(String userId){
        String id= SecurityContextHolder.getContext().getAuthentication().getName();
        if(!id.equals(userId)){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        userRepository.findById(userId).orElseThrow(()
                ->new AppException(ErrorCode.USER_NOT_EXISTED));
        userRepository.deleteById(userId);
    }
    public String update(String userId, UpdateUserRequest request){
        String id= SecurityContextHolder.getContext().getAuthentication().getName();
        if(!id.equals(userId)){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        var user=userRepository.findById(userId).orElseThrow(()
                ->new AppException(ErrorCode.USER_NOT_EXISTED));
        userMapper.updateUser(request,user);
        userRepository.save(user);
        return "Update successful";
    }
    public String createUpdateRoleRequest(UpdateRoleCreationRequest request){
        var userId= SecurityContextHolder.getContext().getAuthentication().getName();
        var check=updateRoleRequestRepository.findByUserId(userId);
        if(check.isPresent()){
            log.info("user id:{}",check.get().getUser().getId());
            throw new AppException(ErrorCode.UPDATE_ROLE_REQUEST_EXISTED);
        }
        User user= userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        UpdateRoleRequest u = new UpdateRoleRequest();
        u.setRequestedAt(LocalDateTime.now());
        u.setCvImage(request.getCvImage());
        u.setUser(user);
        u.setReviewStatus(ReviewStatus.PENDING);
        u.setPaymentStatus(PaymentStatus.UNPAID);
        updateRoleRequestRepository.save(u);
        return "request successful";
    }
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public void  handleUpdateRoleRequest(String requestUpdateRoleId, boolean isApproved){
        var request=updateRoleRequestRepository.findById(requestUpdateRoleId)
                .orElseThrow(()-> new AppException(ErrorCode.UPDATE_ROLE_REQUEST_NOT_EXISTED));
        if(isApproved){
            request.setReviewStatus(ReviewStatus.APPROVED);
            request.setPaymentDeadline(LocalDateTime.now().plusDays(3));
        }else{
            request.setReviewStatus(ReviewStatus.REJECTED);
        }
        updateRoleRequestRepository.save(request);
    }
    public void completedPayment(String requestUpdateRoleId){
        var request=updateRoleRequestRepository.findById(requestUpdateRoleId)
                .orElseThrow(()-> new AppException(ErrorCode.UPDATE_ROLE_REQUEST_NOT_EXISTED));
        if(!request.getReviewStatus().equals("1")){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        request.setPaymentStatus(PaymentStatus.PAID);

        User user= userRepository.findByIdWithRoles(request.getUser().getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        var role=roleRepository.findByName("TUTOR").orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        user.getRoles().add(role);
        userRepository.save(user);
        updateRoleRequestRepository.save(request);
    }
    // running every one hour
    @Scheduled(fixedRate = 3600000)
    public void expiredRequest() {
        log.info("setting expired request ");
        List<UpdateRoleRequest> lst = updateRoleRequestRepository.
                findAllExpiredRequest(LocalDateTime.now())
                .stream().toList();
        log.info("size: {}", lst.size());
        lst.forEach(request -> request.setPaymentStatus(PaymentStatus.EXPIRED));
        updateRoleRequestRepository.saveAll(lst);
    }
}
