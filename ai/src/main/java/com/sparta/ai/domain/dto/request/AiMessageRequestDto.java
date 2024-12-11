package com.sparta.ai.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiMessageRequestDto {
    private String prompt;
}
