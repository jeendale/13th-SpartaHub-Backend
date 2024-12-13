package com.sparta.slack.domain.service;

import com.sparta.slack.infrastructure.dto.SlackClientRequestDto;

public interface SlackClientService {
    void sendMessage(SlackClientRequestDto requestDto);
}
