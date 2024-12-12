package com.sparta.user.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RestApiException {
    private String errorMessage;
}
