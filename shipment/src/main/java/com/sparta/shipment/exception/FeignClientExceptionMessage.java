package com.sparta.shipment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeignClientExceptionMessage {
    USER_NOT_FOUND("해당 유저를 찾을 수 없습니다."),
    HUB_NOT_FOUND("존재하지 않는 허브 입니다."),
    NOT_VALID_ROLE_HUB("해당 허브의 담당자가 아닙니다."),
    SERVICE_NOT_AVAILABLE("외부 서비스에 연결할 수 없습니다. 잠시 후 다시 시도해주세요."),
    HUB_ROUTE_NOT_FOUND("해당하는 허브 경로를 찾을 수 없습니다."),
    NOT_VALID_ROLE_USER("해당 user의 role은 SHIPMENT_MANAGER가 아닙니다.");


    private final String message;
}
