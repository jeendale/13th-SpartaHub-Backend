package com.sparta.ai.domain.controller;

import com.sparta.ai.domain.dto.request.AiMessageRequestDto;
import com.sparta.ai.domain.dto.response.AiMessageIdResponseDto;
import com.sparta.ai.domain.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/ai-messages")
public class AiController {

    private final AiService aiService;

    @PostMapping
    public ResponseEntity<AiMessageIdResponseDto> createAiMessage(
            @RequestBody AiMessageRequestDto requestDto,
            @RequestHeader("X-User-Username") String requestUsername,
            @RequestHeader("X-User-Role") String requestRole) {

        AiMessageIdResponseDto responseDto = aiService.createAiMessage(requestDto, requestUsername, requestRole);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
