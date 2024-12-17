package com.sparta.order.infrastructure.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiMessageRequestDto {
  private String prompt;
}
