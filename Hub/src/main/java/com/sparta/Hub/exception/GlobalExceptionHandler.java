package com.sparta.Hub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler({IllegalArgumentException.class})
  public ResponseEntity<RestApiException> illegalArgumentExceptionHandler(IllegalArgumentException ex) {
    RestApiException restApiException = new RestApiException(ex.getMessage());
    return new ResponseEntity<>(
        restApiException,
        HttpStatus.BAD_REQUEST
    );
  }

}
