package com.sparta.product.infrastructure;

import com.sparta.product.domain.service.HubClientService;
import com.sparta.product.infrastructure.dto.GetHubInfoRes;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hub-service")
public interface HubClient extends HubClientService {

    @GetMapping(value = "/api/v1/hubs/{hubId}", headers = {"X-User-Username=CompanyService", "X-User-Role=MASTER"})
    ResponseEntity<GetHubInfoRes> getHub(@PathVariable UUID hubId);
}
