package com.sparta.shipment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShipmentManagerExceptionMessage {
    NOT_FOUND_ACTIVE("해당하는 ShipmentManagerId로 유효한 값이 없습니다."),
    NOT_MY_INFO("해당 ShipmentManagerId는 본인의 담당이 아닙니다."),
    NOT_COMP_SHIPMENT("업체 배송 담당자가 아닙니다."),
    NOT_HUB_SHIPMENT("허브 배송 담당자가 아닙니다."),
    NOT_IN_HUB("배송 담당자의 담당 허브가 아닙니다.");

    private final String message;
}
