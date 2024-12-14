package com.sparta.ai.model.repository;

import com.sparta.ai.domain.dto.response.AiMessageResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AiMessageRepositoryCustom {
    Page<AiMessageResponseDto> searchAiMessages(String username, Pageable pageable);
}
