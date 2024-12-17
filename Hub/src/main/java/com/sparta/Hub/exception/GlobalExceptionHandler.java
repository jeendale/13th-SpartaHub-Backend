package com.sparta.Hub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {"com.sparta.Hub.domain.controller"})
public class GlobalExceptionHandler {
  @ExceptionHandler({IllegalArgumentException.class})
  public ResponseEntity<RestApiException> illegalArgumentExceptionHandler(IllegalArgumentException ex) {
    RestApiException restApiException = new RestApiException(ex.getMessage());
    return new ResponseEntity<>(
        restApiException,
        HttpStatus.BAD_REQUEST
    );
  }
  @ExceptionHandler({ServiceNotAvailableException.class})
  public ResponseEntity<RestApiException> userServiceNotAvailableException(final ServiceNotAvailableException ex) {


    RestApiException restApiException = RestApiException.builder()
        .message(ex.getMessage())
        .build();

    return new ResponseEntity<>(
        restApiException,
        HttpStatus.SERVICE_UNAVAILABLE
    );
  }
}
