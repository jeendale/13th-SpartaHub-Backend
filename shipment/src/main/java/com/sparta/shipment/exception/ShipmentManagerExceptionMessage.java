package com.sparta.shipment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShipmentManagerExceptionMessage {
    NOT_ALLOWED_ROLE("배송 담당자 권한이 아닙니다."),
    NOT_ALLOWED_API("접근 권한이 없습니다."),
    NOT_FOUND_DELETE("해당하는 ShipmentManagerId에 삭제 가능한 값이 없습니다."),
    NOT_MY_INFO("해당 ShipmentManagerId는 본인의 Id가 아닙니다."),
    NOT_ALLOWED_NULL("은(는) 필수 입력 값입니다.");

    private final String message;
}
