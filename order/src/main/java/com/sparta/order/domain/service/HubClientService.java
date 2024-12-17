package com.sparta.order.domain.service;

import com.sparta.order.infrastructure.dto.GetHubInfoRes;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface HubClientService {
  ResponseEntity<GetHubInfoRes> getHub(
      UUID hubId
  );
}
