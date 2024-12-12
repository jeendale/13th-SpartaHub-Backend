package com.sparta.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthExceptionMessage {
    WRONG_USERNAME_OR_PASSWORD("username 또는 password 정보가 잘못되었습니다.");

    private final String message;
}
