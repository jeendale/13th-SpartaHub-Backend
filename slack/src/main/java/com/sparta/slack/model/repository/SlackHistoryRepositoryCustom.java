package com.sparta.slack.model.repository;

import com.sparta.slack.domain.dto.response.SlackHistoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SlackHistoryRepositoryCustom {
    Page<SlackHistoryResponseDto> searchSlackHistories(String recievedSlackId, Pageable pageable);
}

