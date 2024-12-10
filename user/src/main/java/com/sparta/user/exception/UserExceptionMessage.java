package com.sparta.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserExceptionMessage {
    DUPLICATED_USERNAME("username이 이미 존재합니다."),
    DUPLICATED_NICKNAME("nickname이 이미 존재합니다."),
    DUPLICATED_SLACKID("slackId가 이미 존재합니다."),
    NOT_ALLOWED_ROLE("MASTER 권한으로 가입할 수 없습니다.");

    private final String message;
}
