package com.sparta.shipment.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice(basePackages = {"com.sparta.shipment.domain.controller"})
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestApiException> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("handleValidationExceptions 발생: {}", ex.getMessage());

        FieldError error = (FieldError) ex.getBindingResult().getAllErrors().get(0);
        String fieldName = error.getField();

        RestApiException restApiException = RestApiException.builder()
                .errorMessage(fieldName + ShipmentCommonExceptionMessage.NOT_ALLOWED_NULL.getMessage())
                .build();

        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RestApiException> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        // 예외의 원인 (내부 예외)을 가져옵니다.
        Throwable cause = ex.getCause();

        // JsonMappingException인 경우
        if (cause instanceof JsonMappingException) {
            JsonMappingException jsonMappingException = (JsonMappingException) cause;

            // 예외 메시지에서 유효한 enum 값 목록을 추출하기 위한 정규 표현식
            String message = jsonMappingException.getMessage();
            String enumValues = extractEnumValues(message);

            // 결과 메시지 반환
            String errorMessage = ShipmentCommonExceptionMessage.NOT_ALLOWED_STATUS.getMessage() + enumValues + "]";

            RestApiException restApiException = RestApiException.builder()
                    .errorMessage(errorMessage)
                    .build();
            return new ResponseEntity<>(restApiException, HttpStatus.BAD_REQUEST);
        }

        RestApiException restApiException = RestApiException.builder()
                .errorMessage(ShipmentCommonExceptionMessage.NOT_ALLOWED_STATUS.getMessage())
                .build();

        // 기본적인 오류 메시지 처리
        return new ResponseEntity<>(restApiException,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<RestApiException> handlePSQLException(SQLException ex) {
        RestApiException restApiException = RestApiException.builder()
                .errorMessage(ex.getMessage())
                .build();

        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }

    // 정규 표현식을 사용하여 enum 값들 추출
    private String extractEnumValues(String message) {
        // 대괄호 안의 값들 추출: [HUB_MOVING, DESTINATION_HUB_ARRIVED, ...]
        Pattern pattern = Pattern.compile("\\[([^]]+)\\]");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            // 대괄호 안의 값을 ,로 구분하여 반환
            String enumList = matcher.group(1);
            List<String> enumValues = Arrays.asList(enumList.split(", "));
            return String.join(", ",
                    enumValues);  // 반환 값: "HUB_MOVING, DESTINATION_HUB_ARRIVED, COMPLETED, SHIPPING, PENDING_HUB_MOVE"
        }
        return "";
    }
}
