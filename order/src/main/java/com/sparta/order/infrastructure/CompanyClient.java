package com.sparta.order.infrastructure;

import com.sparta.order.domain.service.CompanyClientService;
import com.sparta.order.infrastructure.dto.CompanyResponseDto;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="company-service")
public interface CompanyClient extends CompanyClientService {

  @GetMapping(value = "/api/v1/companies/{companyId}")
  ResponseEntity<CompanyResponseDto> getCompany (@PathVariable UUID companyId);

}
