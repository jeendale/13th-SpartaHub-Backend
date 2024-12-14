package com.sparta.company.domain.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sparta.company.model.entity.CompanyType;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CompanyResponseDto {
    private UUID companyId;
    private UUID hubId;
    private String username;
    private String companyName;
    private String companyAddress;
    private CompanyType companyType;

    @Builder
    @QueryProjection
    public CompanyResponseDto(UUID companyId, UUID hubId, String username, String companyName, String companyAddress,
                              CompanyType companyType) {
        this.companyId = companyId;
        this.hubId = hubId;
        this.username = username;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyType = companyType;
    }
}
