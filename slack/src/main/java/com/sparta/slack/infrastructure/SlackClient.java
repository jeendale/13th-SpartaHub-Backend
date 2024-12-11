package com.sparta.slack.infrastructure;

import com.sparta.slack.domain.service.SlackClientService;
import com.sparta.slack.infrastructure.dto.SlackClientRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "slackClient", url = "${service.slack.request-url}")
public interface SlackClient extends SlackClientService {

    @PostMapping
    void sendMessage(@RequestBody SlackClientRequestDto requestDto);
}
