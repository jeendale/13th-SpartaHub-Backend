package com.sparta.shipment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShipmentManagerExceptionMessage {
    NOT_FOUND_ACTIVE("해당하는 ShipmentManagerId로 유효한 값이 없습니다."),
    NOT_MY_INFO("해당 ShipmentManagerId는 본인의 담당이 아닙니다.");

    private final String message;
}
