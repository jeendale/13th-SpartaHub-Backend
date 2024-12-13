package com.sparta.shipment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShipmentExceptionMessage {
    NOT_FOUND_DELETE("해당하는 ShipmentId로 유효한 값이 없습니다."),
    NOT_MY_INFO("해당 ShipmentId는 본인의 담당이 아닙니다.");

    private final String message;
}
