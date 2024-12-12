package com.sparta.Hub.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HubExceptionMessage {
  HUB_NOT_EXIST("존재하지 않는 Hub 입니다.");
  private final String message;
}
