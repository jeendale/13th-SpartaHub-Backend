package com.sparta.company.domain.dto.request;

import com.sparta.company.model.entity.Company;
import com.sparta.company.model.entity.CompanyType;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompanyRequestDto {
    private UUID hubId;
    private String username;
    private String companyName;
    private String companyAddress;
    private CompanyType companyType;

    public Company toEntity() {
        return Company.builder()
                .hubId(hubId)
                .username(username)
                .companyName(companyName)
                .companyAddress(companyAddress)
                .companyType(companyType)
                .build();
    }
}
