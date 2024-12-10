package com.sparta.slack.domain.controller;

import com.sparta.slack.domain.dto.request.SlackRequestDto;
import com.sparta.slack.domain.dto.response.SlackHistoryIdResponseDto;
import com.sparta.slack.domain.service.SlackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/slack")
@RequiredArgsConstructor
public class SlackController {

    private final SlackService slackService;

    @PostMapping
    public ResponseEntity<SlackHistoryIdResponseDto> createSlackMessage(
            @RequestBody SlackRequestDto requestDto,
            @RequestHeader("X-User-Username") String requestUsername) {

        SlackHistoryIdResponseDto responseDto = slackService.createSlackMessage(requestUsername, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
