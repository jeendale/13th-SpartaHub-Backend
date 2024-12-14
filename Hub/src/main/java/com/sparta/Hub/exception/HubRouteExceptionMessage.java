package com.sparta.Hub.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HubRouteExceptionMessage {
  HUB_ROUTE_NOT_EXIST("허브 이동 정보가 존재하지 않습니다."),
  HUB_ROUTE_EQUEAL("허브의 주소가 같습니다.");

  private final String message;
}
