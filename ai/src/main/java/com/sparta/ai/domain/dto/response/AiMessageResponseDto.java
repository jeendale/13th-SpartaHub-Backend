package com.sparta.ai.domain.dto.response;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiMessageResponseDto {
    private UUID aiMessageId;
    private String username;
    private String prompt;
    private String content;
}
