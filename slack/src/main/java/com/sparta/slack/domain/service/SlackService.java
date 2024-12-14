package com.sparta.slack.domain.service;

import com.sparta.slack.domain.dto.request.SlackRequestDto;
import com.sparta.slack.domain.dto.request.UpdateSlackHistoryRequestDto;
import com.sparta.slack.domain.dto.response.SlackHistoryIdResponseDto;
import com.sparta.slack.domain.dto.response.SlackHistoryResponseDto;
import com.sparta.slack.exception.SlackExceptionMessage;
import com.sparta.slack.infrastructure.dto.SlackClientRequestDto;
import com.sparta.slack.model.entity.SlackHistory;
import com.sparta.slack.model.repository.SlackRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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

    public SlackHistoryResponseDto getSlackHistory(UUID slackHistoryId, String requestRole) {
        validateRequestRole(requestRole);

        SlackHistory slackHistory = slackRepository.findById(slackHistoryId)
                .orElseThrow(
                        () -> new IllegalArgumentException(SlackExceptionMessage.SLACK_HISTORY_NOT_FOUND.getMessage())
                );

        validateDeleted(slackHistory);

        return SlackHistoryResponseDto.builder()
                .slackHistoryId(slackHistory.getSlackHistoryId())
                .username(slackHistory.getUsername())
                .recivedSlackId(slackHistory.getRecievedSlackId())
                .message(slackHistory.getMessage())
                .sentAt(slackHistory.getSentAt())
                .build();
    }

    public Page<SlackHistoryResponseDto> getSlackHistories(String recievedSlackId, Pageable pageable, String requestRole) {
        validateRequestRole(requestRole);

        return slackRepository.searchSlackHistories(recievedSlackId, pageable);
    }

    @Transactional
    public SlackHistoryIdResponseDto updateSlackHistory(UUID slackHistoryId, UpdateSlackHistoryRequestDto requestDto, String requestRole) {
        validateRequestRole(requestRole);

        SlackHistory slackHistory = slackRepository.findById(slackHistoryId)
                .orElseThrow(
                        () -> new IllegalArgumentException(SlackExceptionMessage.SLACK_HISTORY_NOT_FOUND.getMessage())
                );

        validateDeleted(slackHistory);

        slackHistory.updateMessage(requestDto.getMessage());

        return SlackHistoryIdResponseDto.builder()
                .slackHistoryId(slackHistory.getSlackHistoryId())
                .build();
    }

    @Transactional
    public SlackHistoryIdResponseDto deleteSlackHistory(UUID slackHistoryId, String requestUsername, String requestRole) {
        validateRequestRole(requestRole);

        SlackHistory slackHistory = slackRepository.findById(slackHistoryId)
                .orElseThrow(
                        () -> new IllegalArgumentException(SlackExceptionMessage.SLACK_HISTORY_NOT_FOUND.getMessage())
                );

        validateDeleted(slackHistory);

        slackHistory.updateDeleted(requestUsername);

        return SlackHistoryIdResponseDto.builder()
                .slackHistoryId(slackHistory.getSlackHistoryId())
                .build();
    }

    private void validateRequestRole(String requestRole) {
        if (!requestRole.equals("MASTER")) {
            throw new IllegalArgumentException(SlackExceptionMessage.NOT_ALLOWED_API.getMessage());
        }
    }

    private void validateDeleted(SlackHistory slackHistory) {
        if (slackHistory.isDeleted()) {
            throw new IllegalArgumentException(SlackExceptionMessage.DELETED_SLACK_HISTORY.getMessage());
        }
    }
}
