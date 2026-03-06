package com.tutor.project.controller;

import com.nimbusds.jose.KeyLengthException;
import com.tutor.project.dto.request.UpdateRoleCreationRequest;
import com.tutor.project.dto.request.UpdateUserRequest;
import com.tutor.project.dto.request.UserCreationRequest;
import com.tutor.project.dto.response.ApiResponse;
import com.tutor.project.dto.response.UserResponse;
import com.tutor.project.entity.UpdateRoleRequest;
import com.tutor.project.service.UserService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class UserController {
    UserService userService;
    @PostMapping("/registry")
    public ApiResponse<String> registration(@RequestBody  UserCreationRequest request) throws KeyLengthException {
        return ApiResponse.<String>builder()
                .result(userService.registry(request))
                .build();
    }
    @GetMapping("/users")
    public ApiResponse<List<UserResponse>> getAllUsers(){
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.findAll())
                .build();
    }
    @DeleteMapping("/{userId}")
    public ApiResponse<Void> deleteUser(@PathVariable("userId") String userId){
        userService.delete(userId);
        return ApiResponse.<Void>builder()
                .message("deleted user "+userId)
                .build();
    }
    @PutMapping("/{userId}")
    public ApiResponse<String> updateUser
            (@PathVariable("userId") String userId,UpdateUserRequest request){
        return ApiResponse.<String>builder()
                .result(userService.update(userId,request))
                .build();
    }
    @GetMapping("/{keyword}")
    public ApiResponse<List<UserResponse>> findByKeyword
            (@PathVariable("keyword") String keyword){
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.findByFullNameContainKeyword(keyword))
                .build();
    }
    @PostMapping("/role/update")
    public ApiResponse<String> createUpdateRoleRequest(@RequestBody UpdateRoleCreationRequest request){
        return ApiResponse.<String>builder()
                .result(userService.createUpdateRoleRequest(request))
                .build();
    }
    @PutMapping("/role/{requestRoleId}")
    public ApiResponse<?> handleUpdateRoleRequest
            (@PathVariable("requestRoleId") String requestUpdateRoleId,
             @RequestParam("isApproved") boolean isApproved){
        userService.handleUpdateRoleRequest(requestUpdateRoleId,isApproved);
        return ApiResponse.builder()
                .message("handled request update role")
                .build();
    }
    @PutMapping("/role/payment/{requestRoleId}")
    public ApiResponse<?> completedPayment
            (@PathVariable("requestRoleId") String requestRoleId){
        userService.completedPayment(requestRoleId);
        return ApiResponse.builder()
                .message("done payment")
                .build();
    }
}
