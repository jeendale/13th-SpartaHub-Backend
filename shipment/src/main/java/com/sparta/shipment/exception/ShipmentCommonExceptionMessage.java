package com.sparta.shipment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShipmentCommonExceptionMessage {
    NOT_ALLOWED_API("접근 권한이 없습니다."),
    NOT_ALLOWED_NULL("은(는) 필수 입력 값입니다.");

    private final String message;
}
