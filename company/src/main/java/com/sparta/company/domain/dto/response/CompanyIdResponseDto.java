package com.sparta.company.domain.dto.response;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompanyIdResponseDto {
    private UUID companyId;
}
