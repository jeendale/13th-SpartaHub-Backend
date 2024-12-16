package com.sparta.order.infrastructure.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateProductRequestDto {
  private String productName;
}
