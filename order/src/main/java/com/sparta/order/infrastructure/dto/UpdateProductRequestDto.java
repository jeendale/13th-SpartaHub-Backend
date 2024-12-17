package com.sparta.order.infrastructure.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateProductRequestDto {
  private String productName;
  private Integer count;

  public UpdateProductRequestDto(String productName, Integer count) {
    this.productName = productName;
    this.count = count;
  }
}
