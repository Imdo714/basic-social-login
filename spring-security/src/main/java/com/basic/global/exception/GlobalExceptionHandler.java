package com.basic.global.exception;

import com.basic.api.ApiResponse;
import com.basic.global.exception.handelException.jwtException.CustomJwtException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomJwtException.class)
    public ApiResponse<Object> jwtExpiredException(CustomJwtException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return ApiResponse.of(HttpStatus.UNAUTHORIZED, e.getMessage(), null);
    }


}
