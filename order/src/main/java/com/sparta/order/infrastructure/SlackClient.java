package com.sparta.order.infrastructure;

import com.sparta.order.domain.service.SlackClientService;
import com.sparta.order.infrastructure.dto.SlackHistoryIdResponseDto;
import com.sparta.order.infrastructure.dto.SlackRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "slack-service")
public interface SlackClient extends SlackClientService {
  @PostMapping(value = "/api/v1/slack", headers = {"X-User-Username=\"\""})
  ResponseEntity<SlackHistoryIdResponseDto> createSlackMessage(@RequestBody SlackRequestDto requestDto);
}
