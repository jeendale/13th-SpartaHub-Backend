package com.sparta.order.infrastructure.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductIdResponseDto {
  private UUID productId;
}

