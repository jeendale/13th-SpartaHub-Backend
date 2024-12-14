package com.sparta.company.excpetion;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeignClientExceptionMessage {
    USER_NOT_FOUND("해당 유저를 찾을 수 없습니다."),
    HUB_NOT_FOUND("존재하지 않는 허브 입니다."),
    SERVICE_NOT_AVAILABLE("외부 서비스에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.");

    private final String message;
}
