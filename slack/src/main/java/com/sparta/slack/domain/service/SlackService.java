package com.sparta.slack.domain.service;

import com.sparta.slack.domain.dto.request.SlackRequestDto;
import com.sparta.slack.domain.dto.response.SlackHistoryIdResponseDto;
import com.sparta.slack.infrastructure.dto.SlackClientRequestDto;
import com.sparta.slack.model.entity.SlackHistory;
import com.sparta.slack.model.repository.SlackRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SlackService {

    private final SlackRepository slackRepository;
    private final SlackClientService slackClientService;

    @Transactional
    public SlackHistoryIdResponseDto createSlackMessage(String requestUsername, SlackRequestDto requestDto) {
        slackClientService.sendMessage(SlackClientRequestDto.builder()
                .channel(requestDto.getRecivedSlackId())
                .text(requestDto.getMessage())
                .build());

        SlackHistory slackHistory = SlackHistory.builder()
                .username(requestUsername)
                .recievedSlackId(requestDto.getRecivedSlackId())
                .message(requestDto.getMessage())
                .sentAt(LocalDateTime.now())
                .build();

        slackRepository.save(slackHistory);

        return SlackHistoryIdResponseDto.builder()
                .slackHistoryId(slackHistory.getSlackHistoryId())
                .build();
    }
}
