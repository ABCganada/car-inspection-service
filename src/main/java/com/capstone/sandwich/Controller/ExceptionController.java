package com.capstone.sandwich.Controller;

import com.capstone.sandwich.Domain.Exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<?> ExceptionResponse(ApiException e) {
        log.error("ApiException = {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), e.getCode());
    }

}
