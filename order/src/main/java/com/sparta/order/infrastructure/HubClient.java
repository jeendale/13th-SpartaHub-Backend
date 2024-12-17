package com.sparta.order.infrastructure;

import com.sparta.order.domain.service.HubClientService;
import com.sparta.order.infrastructure.dto.GetHubInfoRes;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="hub-service")
public interface HubClient extends HubClientService {
  @GetMapping(value = "/api/v1/hubs/{hubId}")
  ResponseEntity<GetHubInfoRes> getHub(@PathVariable UUID hubId);
}
