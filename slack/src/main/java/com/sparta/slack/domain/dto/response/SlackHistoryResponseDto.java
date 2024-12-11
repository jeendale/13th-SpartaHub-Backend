package com.sparta.slack.domain.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SlackHistoryResponseDto {
    private UUID slackHistoryId;
    private String username;
    private String recivedSlackId;
    private String message;
    private LocalDateTime sentAt;
}
