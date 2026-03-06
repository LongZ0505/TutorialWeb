package com.tutor.project.mapper;

import com.tutor.project.dto.request.UpdateUserRequest;
import com.tutor.project.dto.request.UserCreationRequest;
import com.tutor.project.dto.response.UserResponse;
import com.tutor.project.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Slf4j
public class UserMapper {
    public User toUser(UserCreationRequest request){
        return User.builder()
                .email(request.getEmail())
                .avatar(request.getAvatar())
                .phone(request.getPhone())
                .fullName(request.getFullName())
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
    }
    public UserResponse toUserResponse(User request){
        return UserResponse.builder()
                .email(request.getEmail())
                .avatar(request.getAvatar())
                .phone(request.getPhone())
                .fullName(request.getFullName())
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
    }
    public void updateUser(UpdateUserRequest request, User user){
        user.setAvatar(request.getAvatar());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
    }
}
