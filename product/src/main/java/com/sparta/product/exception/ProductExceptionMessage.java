package com.sparta.product.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductExceptionMessage {
    PRODUCT_NOT_FOUND("존재하지 않는 상품입니다."),
    DELETED_PRODUCT("삭제된 상품입니다."),
    NOT_OWN_HUB("요청한 상품의 허브 담당자가 아닙니다."),
    NOT_OWN_COMPANY("요청한 상품의 업체 담당자가 아닙니다."),
    REQUIRE_HUB_ID("HUB_MANAGER는 hubId 값을 반드시 입력해야 합니다."),
    NOT_COMPANY_HUB("해당 업체는 요청한 허브에 속해있지 않습니다."),
    NOT_ALLOWED_API("접근 권한이 없습니다.");

    private final String message;
}
