package com.sparta.product.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = {"com.sparta.product.domain.controller"})

public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<RestApiException> illegalArgumentException(final IllegalArgumentException ex) {

        log.warn("IllegalArgumentException 발생: {}", ex.getMessage());

        RestApiException restApiException = RestApiException.builder()
                .errorMessage(ex.getMessage())
                .build();

        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({ServiceNotAvailableException.class})
    public ResponseEntity<RestApiException> ServiceNotAvailableException(final ServiceNotAvailableException ex) {

        log.warn("UserServiceNotAvailableException 발생: {}", ex.getMessage());

        RestApiException restApiException = RestApiException.builder()
                .errorMessage(ex.getMessage())
                .build();

        return new ResponseEntity<>(
                restApiException,
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestApiException> handleValidationExceptions(MethodArgumentNotValidException ex) {

        log.warn("handleValidationExceptions 발생: {}", ex.getMessage());

        StringBuilder errorMessages = new StringBuilder();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorMessages.append(error.getField())
                        .append(" : ")
                        .append(error.getDefaultMessage())
                        .append(" ")
        );

        RestApiException restApiException = RestApiException.builder()
                .errorMessage(errorMessages.toString())
                .build();

        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }
}
