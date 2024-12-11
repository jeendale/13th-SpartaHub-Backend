package com.sparta.slack.domain.controller;

import com.sparta.slack.domain.dto.request.SlackRequestDto;
import com.sparta.slack.domain.dto.request.UpdateSlackHistoryRequestDto;
import com.sparta.slack.domain.dto.response.SlackHistoryIdResponseDto;
import com.sparta.slack.domain.dto.response.SlackHistoryResponseDto;
import com.sparta.slack.domain.service.SlackService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @GetMapping("/{slackHistoryId}")
    public ResponseEntity<SlackHistoryResponseDto> getSlackHistory(
            @PathVariable UUID slackHistoryId,
            @RequestHeader("X-User-Role") String requestRole) {

        SlackHistoryResponseDto responseDto = slackService.getSlackHistory(slackHistoryId, requestRole);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/{slackHistoryId}")
    public ResponseEntity<SlackHistoryIdResponseDto> updateSlackHistory(
            @PathVariable UUID slackHistoryId,
            @RequestBody UpdateSlackHistoryRequestDto requestDto,
            @RequestHeader("X-User-Role") String requestRole) {

        SlackHistoryIdResponseDto responseDto = slackService.updateSlackHistory(slackHistoryId, requestDto, requestRole);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
