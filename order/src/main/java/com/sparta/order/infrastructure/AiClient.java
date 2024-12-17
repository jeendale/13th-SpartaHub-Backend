package com.sparta.order.infrastructure;

import com.sparta.order.domain.service.AiClientService;
import com.sparta.order.infrastructure.dto.AiMessageCreateResponseDto;
import com.sparta.order.infrastructure.dto.AiMessageRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ai-service")
public interface AiClient extends AiClientService {
  @PostMapping(value = "api/v1/ai-messages", headers = {"X-User-Username=\"\"", "X-User-Role=MASTER"})
  ResponseEntity<AiMessageCreateResponseDto> createAiMessage(@RequestBody AiMessageRequestDto requestDto);
}
