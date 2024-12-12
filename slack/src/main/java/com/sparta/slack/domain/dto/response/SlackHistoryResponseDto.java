package com.sparta.slack.domain.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SlackHistoryResponseDto {
    private UUID slackHistoryId;
    private String username;
    private String recivedSlackId;
    private String message;
    private LocalDateTime sentAt;

    @Builder
    @QueryProjection
    public SlackHistoryResponseDto(UUID slackHistoryId, String username, String recivedSlackId, String message, LocalDateTime sentAt) {
        this.slackHistoryId = slackHistoryId;
        this.username = username;
        this.recivedSlackId = recivedSlackId;
        this.message = message;
        this.sentAt = sentAt;
    }
}
