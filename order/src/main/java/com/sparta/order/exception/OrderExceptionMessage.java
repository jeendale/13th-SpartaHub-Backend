package com.sparta.order.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderExceptionMessage {
  ORDER_NOT_FOUND("주문에 대한 정보가 없습니다."),
  NOT_YOUR_HUB("담당 허브가 아닙니다."),
  NOT_YOUR_COMANY("담당 업체가 아닙니다."),
  NOT_YOUR_SHIPMENT("담당 배송이 아닙니다."),
  PRODUCT_QUANTITY_OVER("남아있는 수량보다 많습니다."),
  PRODUCT_QUANTITY_ERROR("0 이상의 값을 입력해주세요"),
  CHECK_USER_ROlE("권한이 없습니다.");

 private final String message;
}
