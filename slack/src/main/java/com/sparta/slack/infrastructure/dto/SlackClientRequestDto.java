package com.sparta.slack.infrastructure.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SlackClientRequestDto {
    private String channel;
    private String text;
}
