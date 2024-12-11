package com.sparta.ai.infrastructure.dto.request;

import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class GeminiClientRequestDto {

    private final List<Content> contents;

    @Getter
    @RequiredArgsConstructor
    @Builder
    public static class Content {
        private final List<Part> parts;
    }

    @Getter
    @RequiredArgsConstructor
    @Builder
    public static class Part {
        private final String text;
    }

    public static GeminiClientRequestDto create(String prompt, String maxLengthPromptMessage) {
        // Part 생성
        Part part = Part.builder()
                .text(prompt + maxLengthPromptMessage)
                .build();

        // Content 생성
        Content content = Content.builder()
                .parts(Collections.singletonList(part))
                .build();

        // RequestBody 생성
        return GeminiClientRequestDto.builder()
                .contents(Collections.singletonList(content))
                .build();
    }
}
