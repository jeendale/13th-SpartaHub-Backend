package com.sparta.shipment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShipmentManagerExceptionMessage {
    NOT_ALLOWED_ROLE("배송 담당자 권한이 아닙니다."),
    NOT_ALLOWED_NULL("은(는) 필수 입력 값입니다.");

    private final String message;
}
