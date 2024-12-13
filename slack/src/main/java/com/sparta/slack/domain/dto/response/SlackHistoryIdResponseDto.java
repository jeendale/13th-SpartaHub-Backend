package com.sparta.slack.domain.dto.response;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SlackHistoryIdResponseDto {
    private UUID slackHistoryId;
}
