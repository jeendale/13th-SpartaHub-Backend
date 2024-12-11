package com.sparta.ai.domain.dto.response;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiMessageIdResponseDto {
    private UUID aiMessageId;
    private String content;
}
