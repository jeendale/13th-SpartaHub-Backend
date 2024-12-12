package com.sparta.shipment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShipmentManagerExceptionMessage {
    NOT_FOUND_DELETE("해당하는 ShipmentManagerId로 삭제 가능한 값이 없습니다."),
    NOT_MY_INFO("해당 ShipmentManagerId는 본인의 Id가 아닙니다.");

    private final String message;
}
