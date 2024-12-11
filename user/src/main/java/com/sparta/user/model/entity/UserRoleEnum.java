package com.sparta.user.model.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRoleEnum {
    HUB_MANAGER(Authority.HUB_MANAGER),  // 손님 권한
    SHIPMENT_MANAGER(Authority.SHIPMENT_MANAGER),  // 점주 권한
    COMPANY_MANAGER(Authority.COMPANY_MANAGER), //매니저 권한
    MASTER(Authority.MASTER); //관리자 권한

    private final String authority;

    public static class Authority {
        public static final String HUB_MANAGER = "ROLE_HUB_MANAGER";
        public static final String SHIPMENT_MANAGER = "ROLE_SHIPMENT_MANAGER";
        public static final String COMPANY_MANAGER = "ROLE_COMPANY_MANAGER";
        public static final String MASTER = "ROLE_MASTER";
    }

    @JsonCreator
    public static UserRoleEnum fromString(String role) {
        return UserRoleEnum.valueOf(role.toUpperCase()); // 대소문자 구분을 하지 않음
    }

    @JsonValue
    public String toValue() {
        return this.name(); // name()은 enum의 이름을 반환
    }
}
