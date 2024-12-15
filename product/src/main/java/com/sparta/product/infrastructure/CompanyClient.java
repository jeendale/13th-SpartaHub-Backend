package com.sparta.product.infrastructure;

import com.sparta.product.domain.service.CompanyClientService;
import com.sparta.product.infrastructure.dto.CompanyResponseDto;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "company-service")
public interface CompanyClient extends CompanyClientService {

    @GetMapping(value = "/api/v1/companies/{companyId}", headers = {"X-User-Username=CompanyService", "X-User-Role=MASTER"})
    ResponseEntity<CompanyResponseDto> getCompany(@PathVariable("companyId") UUID companyId);
}
