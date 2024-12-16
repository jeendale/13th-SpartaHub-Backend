package com.sparta.order.domain.dto.response;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class OrderIdRes {
  private UUID orderId;

  public OrderIdRes(UUID orderId) {
    this.orderId = orderId;
  }
}
