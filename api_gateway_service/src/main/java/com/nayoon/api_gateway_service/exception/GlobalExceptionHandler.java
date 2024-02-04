package com.nayoon.api_gateway_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorResponse response = ErrorResponse.builder()
                .errorCode(e.getErrorCode())
                .message(e.getMessage())
                .build();

        log.warn("{}", e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse response = ErrorResponse.builder()
                .errorCode(ErrorCode.INTERNAL_SERVER_ERROR)
                .message(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                .build();

        log.error("Exception is occurred.", e);
        return ResponseEntity.status(response.getErrorCode().getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder errorMessage = new StringBuilder();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMessage.append(fieldError.getDefaultMessage()).append("; ");
        }

        ErrorResponse response = ErrorResponse.builder()
                .errorCode(ErrorCode.INVALID_REQUEST)
                .message(errorMessage.toString())
                .build();

        log.error("MethodArgumentNotValidException is occurred.", e);
        return ResponseEntity.status(response.getErrorCode().getStatus()).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        ErrorResponse response = ErrorResponse.builder()
                .errorCode(ErrorCode.INVALID_REQUEST)
                .message(ErrorCode.INVALID_REQUEST.getMessage())
                .build();

        log.error("DataIntegrityViolationException is occurred.", e);
        return ResponseEntity.status(response.getErrorCode().getStatus()).body(response);
    }

}