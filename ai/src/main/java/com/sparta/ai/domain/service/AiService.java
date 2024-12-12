package com.sparta.ai.domain.service;

import com.sparta.ai.domain.dto.request.AiMessageRequestDto;
import com.sparta.ai.domain.dto.response.AiMessageCreateResponseDto;
import com.sparta.ai.domain.dto.response.AiMessageIdResponseDto;
import com.sparta.ai.domain.dto.response.AiMessageResponseDto;
import com.sparta.ai.exception.AiExceptionMessage;
import com.sparta.ai.infrastructure.dto.request.GeminiClientRequestDto;
import com.sparta.ai.infrastructure.dto.response.GeminiClientResponseDto;
import com.sparta.ai.model.entity.AiMessage;
import com.sparta.ai.model.repository.AiMessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiService {
    private static final String FORMATTING_MESSAGE = ", 위 내용을 기반으로 몇월 며칠 오전/오후 몇시까지 형식으로 최종 발송 시한을 도출해줘";
    private static final String MAX_LENGTH_PROMPT_MESSAGE = ", 답변을 최대한 간결하게 50자 이하로";

    private final AiMessageRepository aiMessageRepository;
    private final GeminiClientService geminiClientService;

    @Value("${service.gemini.api-key}")
    private String apiKey;

    @Transactional
    public AiMessageCreateResponseDto createAiMessage(AiMessageRequestDto requestDto, String requestUsername,
                                                      String requestRole) {
        validateRequestRole(requestRole);

        GeminiClientResponseDto responseDto = geminiClientService.sendPrompt(apiKey,
                GeminiClientRequestDto.create(requestDto.getPrompt() + FORMATTING_MESSAGE, MAX_LENGTH_PROMPT_MESSAGE));

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

        return AiMessageCreateResponseDto.builder()
                .aiMessageId(aiMessage.getAiMessageId())
                .content(aiMessage.getContent())
                .build();
    }

    public AiMessageResponseDto getAiMessage(UUID aiMessageId, String requestRole) {
        validateRequestRole(requestRole);

        AiMessage aiMessage = aiMessageRepository.findById(aiMessageId)
                .orElseThrow(() -> new IllegalArgumentException(AiExceptionMessage.AI_MESSAGE_NOT_FOUND.getMessage()));

        validateDeleted(aiMessage);

        return AiMessageResponseDto.builder()
                .aiMessageId(aiMessage.getAiMessageId())
                .username(aiMessage.getUsername())
                .prompt(aiMessage.getPrompt())
                .content(aiMessage.getContent())
                .build();
    }

    public Page<AiMessageResponseDto> searchAiMessages(String username, String requestRole, Pageable pageable) {
        validateRequestRole(requestRole);

        return aiMessageRepository.searchAiMessages(username, pageable);
    }

    @Transactional
    public AiMessageIdResponseDto deleteAiMessage(UUID aiMessageId, String requestUsername, String requestRole) {
        validateRequestRole(requestRole);

        AiMessage aiMessage = aiMessageRepository.findById(aiMessageId)
                .orElseThrow(() -> new IllegalArgumentException(AiExceptionMessage.AI_MESSAGE_NOT_FOUND.getMessage()));

        validateDeleted(aiMessage);

        aiMessage.updateDeleted(requestUsername);

        return AiMessageIdResponseDto.builder()
                .aiMessageId(aiMessageId)
                .build();
    }

    private void validateRequestRole(String requestRole) {
        if (!requestRole.equals("MASTER")) {
            throw new IllegalArgumentException(AiExceptionMessage.NOT_ALLOWED_API.getMessage());
        }
    }

    private void validateDeleted(AiMessage aiMessage) {
        if (aiMessage.isDeleted()) {
            throw new IllegalArgumentException(AiExceptionMessage.DELETED_AI_MESSAGE.getMessage());
        }
    }
}
