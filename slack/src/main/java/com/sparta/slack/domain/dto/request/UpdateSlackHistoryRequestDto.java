package com.sparta.slack.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateSlackHistoryRequestDto {
    private String message;
}
