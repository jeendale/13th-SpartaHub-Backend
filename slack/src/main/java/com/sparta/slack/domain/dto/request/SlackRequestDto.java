package com.sparta.slack.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SlackRequestDto {
    private String recivedSlackId;
    private String message;
}
