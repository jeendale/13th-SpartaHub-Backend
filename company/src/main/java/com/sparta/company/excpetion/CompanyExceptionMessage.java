package com.sparta.company.excpetion;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompanyExceptionMessage {
    NOT_COMPANY_MANAGER("해당 사용자는 업체 관리자가 아닙니다."),
    COMPANY_NOT_FOUND("해당 업체가 존재하지 않습니다."),
    NOT_OWN_COMPANY("요청한 업체는 자신의 업체가 아닙니다.");

    private final String message;
}
