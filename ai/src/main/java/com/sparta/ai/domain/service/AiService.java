package com.sparta.ai.domain.service;

import com.sparta.ai.domain.dto.request.AiMessageRequestDto;
import com.sparta.ai.domain.dto.response.AiMessageIdResponseDto;
import com.sparta.ai.exception.AiExceptionMessage;
import com.sparta.ai.infrastructure.dto.request.GeminiClientRequestDto;
import com.sparta.ai.infrastructure.dto.response.GeminiClientResponseDto;
import com.sparta.ai.model.entity.AiMessage;
import com.sparta.ai.model.repository.AiMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiService {
    private static final String MAX_LENGTH_PROMPT_MESSAGE = ", 답변을 최대한 간결하게 50자 이하로";

    private final AiMessageRepository aiMessageRepository;
    private final GeminiClientService geminiClientService;

    @Value("${service.gemini.api-key}")
    private String apiKey;

    @Transactional
    public AiMessageIdResponseDto createAiMessage(AiMessageRequestDto requestDto, String requestUsername, String requestRole) {
        validateRequestRole(requestRole);

        GeminiClientResponseDto responseDto = geminiClientService.sendPrompt(apiKey,
                GeminiClientRequestDto.create(requestDto.getPrompt(), MAX_LENGTH_PROMPT_MESSAGE));

        AiMessage aiMessage = AiMessage.builder()
                .username(requestUsername)
                .prompt(requestDto.getPrompt())
                .content(responseDto.getCandidates()
                        .get(0)
                        .getContent()
                        .getParts()
                        .get(0)
                        .getText())
                .build();

        aiMessageRepository.save(aiMessage);

        return AiMessageIdResponseDto.builder()
                .aiMessageId(aiMessage.getAiMessageId())
                .content(aiMessage.getContent())
                .build();
    }

    private void validateRequestRole(String requestRole) {
        if (!requestRole.equals("MASTER")) {
            throw new IllegalArgumentException(AiExceptionMessage.NOT_ALLOWED_API.getMessage());
        }
    }
}
