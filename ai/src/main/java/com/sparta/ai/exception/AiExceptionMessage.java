package com.sparta.ai.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AiExceptionMessage {
    AI_MESSAGE_NOT_FOUND("존재하지 않는 AI 메시지 기록입니다."),
    NOT_ALLOWED_API("접근 권한이 없습니다.");

    private final String message;
}
