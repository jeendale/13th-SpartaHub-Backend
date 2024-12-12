package com.sparta.Hub.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HubExceptionMessage {
  HUB_NOT_EXIST("존재하지 않는 허브 입니다."),
  HUB_NAME_EQUEAL("허브 이름이 동일합니다."),
  HUB_ADRESS_EQUEAL("허브 주소가 동일합니다.");

  private final String message;
}
