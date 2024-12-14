package com.sparta.Hub.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HubExceptionMessage {
  HUB_NOT_EXIST("존재하지 않는 허브 입니다."),
  NOT_ALLOWED_API("접근 권한이 없습니다."),
  NOT_HUB_MANAGER("HUB MANAGER 권한이 없습니다.");

  private final String message;
}
