package com.sparta.product.domain.service;

import com.sparta.product.infrastructure.dto.CompanyResponseDto;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface CompanyClientService {
    ResponseEntity<CompanyResponseDto> getCompany(UUID companyId);
}
