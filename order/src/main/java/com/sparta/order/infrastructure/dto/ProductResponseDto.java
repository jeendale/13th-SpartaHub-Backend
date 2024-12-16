package com.sparta.order.infrastructure.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponseDto {
  private UUID productId;
  private UUID hubId;
  private UUID companyId;
  private String productName;
}
