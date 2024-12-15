package com.sparta.product.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeignClientExceptionMessage {
    HUB_NOT_FOUND("존재하지 않는 허브 입니다."),

    COMPANY_NOT_FOUND("해당 업체가 존재하지 않습니다."),
    NOT_OWN_COMPANY("요청한 업체의 담당자가 아닙니다."),
    SERVICE_NOT_AVAILABLE("외부 서비스에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.");

    private final String message;
}
