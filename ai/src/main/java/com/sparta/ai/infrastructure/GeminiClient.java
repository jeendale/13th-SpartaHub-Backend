package com.sparta.ai.infrastructure;

import com.sparta.ai.domain.service.GeminiClientService;
import com.sparta.ai.infrastructure.dto.request.GeminiClientRequestDto;
import com.sparta.ai.infrastructure.dto.response.GeminiClientResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "slackClient", url = "${service.gemini.request-url}")
public interface GeminiClient extends GeminiClientService {

    @PostMapping
    GeminiClientResponseDto sendPrompt(@RequestParam String key, @RequestBody GeminiClientRequestDto requestDto);
}
