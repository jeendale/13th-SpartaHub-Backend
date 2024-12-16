package com.sparta.order.infrastructure.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {
  private String username;
  private String nickname;
  private String slackId;
  private String role;
}
