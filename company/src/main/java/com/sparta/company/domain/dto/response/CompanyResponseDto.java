package com.sparta.company.domain.dto.response;

import com.sparta.company.model.entity.CompanyType;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompanyResponseDto {
    private UUID companyId;
    private UUID hubId;
    private String username;
    private String companyName;
    private String companyAddress;
    private CompanyType companyType;
}
