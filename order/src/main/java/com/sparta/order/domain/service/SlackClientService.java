package com.sparta.order.domain.service;

import com.sparta.order.infrastructure.dto.SlackHistoryIdResponseDto;
import com.sparta.order.infrastructure.dto.SlackRequestDto;
import org.springframework.http.ResponseEntity;

public interface SlackClientService {

  ResponseEntity<SlackHistoryIdResponseDto>  createSlackMessage(
      SlackRequestDto slackRequestDto
  );
}
