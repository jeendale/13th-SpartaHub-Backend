package com.sparta.order.model.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompanyType {
  SUPPLIER_COMPANY(Type.SUPPLIER_COMPANY),
  RECIPIENT_COMPANY(Type.RECIPIENT_COMPANY);

  private final String type;

  public static class Type {
    public static final String SUPPLIER_COMPANY = "TYPE_SUPPLIER_COMPANY";
    public static final String RECIPIENT_COMPANY = "TYPE_RECIPIENT_COMPANY";
  }

  @JsonCreator
  public static CompanyType fromString(String type) {
    return CompanyType.valueOf(type.toUpperCase()); // 대소문자 구분을 하지 않음
  }

  @JsonValue
  public String toValue() {
    return this.name(); // name()은 enum의 이름을 반환
  }
}
