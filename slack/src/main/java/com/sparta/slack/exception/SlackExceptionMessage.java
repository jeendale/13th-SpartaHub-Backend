package com.sparta.slack.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SlackExceptionMessage {
    SLACK_HISTORY_NOT_FOUND("존재하지 않는 슬랙 메시지 기록입니다."),
    NOT_ALLOWED_API("접근 권한이 없습니다.");

    private final String message;
}
