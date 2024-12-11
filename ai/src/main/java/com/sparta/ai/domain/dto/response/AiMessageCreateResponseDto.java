package com.sparta.ai.domain.dto.response;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiMessageCreateResponseDto {
    private UUID aiMessageId;
    private String content;
}
