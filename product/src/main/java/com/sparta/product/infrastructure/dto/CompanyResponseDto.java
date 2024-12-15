package com.sparta.product.infrastructure.dto;

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
    private String companyType;
}
