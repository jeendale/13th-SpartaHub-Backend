package com.sparta.product.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductExceptionMessage {
    NOT_OWN_HUB("요청한 허브의 담당자가 아닙니다."),
    NOT_OWN_COMPANY("요청한 업체의 담당자가 아닙니다."),
    NOT_ALLOWED_API("접근 권한이 없습니다.");

    private final String message;
}
