package com.sparta.order.domain.service;

import com.sparta.order.infrastructure.dto.CompanyResponseDto;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface CompanyClientService {
  ResponseEntity<CompanyResponseDto> getCompany( UUID companyId);
}
