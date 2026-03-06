package com.tutor.project.controller;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;
import com.tutor.project.dto.request.*;
import com.tutor.project.dto.response.ApiResponse;
import com.tutor.project.dto.response.IntrospectResponse;
import com.tutor.project.dto.response.LoginResponse;
import com.tutor.project.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    AuthenticationService authenticationService;
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) throws KeyLengthException {

        return ApiResponse.<LoginResponse>builder()
                .result(authenticationService.login(request))
                .build();
    }
    @PostMapping("/logout")
    public void logout(@RequestBody LogoutRequest request){
        authenticationService.logout(request);
    }
    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> logout(@RequestBody IntrospectRequest request){
        return ApiResponse.<IntrospectResponse>builder()
                .result(authenticationService.introspect(request))
                .build();
    }
    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refresh(@RequestBody RefreshTokenRequest request) throws ParseException, JOSEException {
        return ApiResponse.<LoginResponse>builder()
                .result(authenticationService.refreshToken(request))
                .build();
    }
//    @PostMapping("/role")
//    public ApiResponse<String> addRoles(@RequestBody RoleCreationRequest request){
//        return ApiResponse.<String>builder()
//                .result(authenticationService.addRole(request))
//                .build();
//
//    }
}
