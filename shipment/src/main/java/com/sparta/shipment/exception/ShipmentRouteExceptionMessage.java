package com.sparta.shipment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShipmentRouteExceptionMessage {
    NOT_FOUND_ACTIVE("해당하는 ShipmentRouteId로 유효한 값이 없습니다."),
    NOT_MY_INFO("해당 ShipmentRouteId는 본인 담당의 Id가 아닙니다.");

    private final String message;
}
