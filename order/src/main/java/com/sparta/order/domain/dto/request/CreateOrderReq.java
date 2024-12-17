package com.sparta.order.domain.dto.request;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class CreateOrderReq {
  private UUID requestCompanyId;
  private UUID receiveCompanyId;
  private UUID productId;
  private Integer quantity;
  private String request;

  public CreateOrderReq(UUID requestCompanyId, UUID receiveCompanyId, UUID productId,Integer quantity, String request) {
    this.requestCompanyId = requestCompanyId;
    this.receiveCompanyId = receiveCompanyId;
    this.productId = productId;
    this.quantity = quantity;
    this.request = request;
  }
}
