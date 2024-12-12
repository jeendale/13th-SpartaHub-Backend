package com.sparta.ai.domain.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AiMessageResponseDto {
    private UUID aiMessageId;
    private String username;
    private String prompt;
    private String content;

    @Builder
    @QueryProjection
    public AiMessageResponseDto(UUID aiMessageId, String username, String prompt, String content) {
        this.aiMessageId = aiMessageId;
        this.username = username;
        this.prompt = prompt;
        this.content = content;
    }
}
