package com.sparta.product.domain.service;

import com.sparta.product.infrastructure.dto.GetHubInfoRes;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface HubClientService {
    ResponseEntity<GetHubInfoRes> getHub(UUID hubId);
}
