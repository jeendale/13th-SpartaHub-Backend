package com.sparta.company.excpetion;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RestApiException {
    private String errorMessage;
}
