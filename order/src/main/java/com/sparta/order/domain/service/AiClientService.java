package com.sparta.order.domain.service;

import com.sparta.order.infrastructure.dto.AiMessageCreateResponseDto;
import com.sparta.order.infrastructure.dto.AiMessageRequestDto;
import org.springframework.http.ResponseEntity;

  public interface AiClientService {

    ResponseEntity<AiMessageCreateResponseDto> createAiMessage(
        AiMessageRequestDto requestDto
    );

  }
