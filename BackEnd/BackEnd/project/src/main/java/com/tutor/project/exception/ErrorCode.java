package com.tutor.project.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    USER_EXISTED(1000,"User existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1001,"User not existed", HttpStatus.NOT_FOUND),
    ROLE_NOT_EXISTED(1004,"Role not existed", HttpStatus.NOT_FOUND),
    UNCATEGORIZED_EXCEPTION(8888,"Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(1002,"Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1003,"Unauthorized", HttpStatus.INTERNAL_SERVER_ERROR),
    UPDATE_ROLE_REQUEST_NOT_EXISTED(1006,"Request update role not existed", HttpStatus.NOT_FOUND),
    UPDATE_ROLE_REQUEST_EXISTED(1005,"Request update role existed", HttpStatus.BAD_REQUEST),
    ;
    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
