package com.sparta.ai.domain.service;

import com.sparta.ai.infrastructure.dto.request.GeminiClientRequestDto;
import com.sparta.ai.infrastructure.dto.response.GeminiClientResponseDto;

public interface GeminiClientService {
    GeminiClientResponseDto sendPrompt(String key, GeminiClientRequestDto requestDto);
}
