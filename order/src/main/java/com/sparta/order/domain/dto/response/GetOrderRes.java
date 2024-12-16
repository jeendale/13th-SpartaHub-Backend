package com.sparta.order.domain.dto.response;

import jakarta.persistence.Column;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class GetOrderRes {

  private UUID orderId;
  private LocalDateTime orderDate;
  private int quantity;
  private String request;
  private String username;
  private UUID requestCompanyId;
  private UUID receiveCompanyId;
  private UUID productId;
  private UUID shipmentId;

  public GetOrderRes(UUID orderId,
      LocalDateTime orderDate,
      int quantity,
      String request,
      String username,
      UUID requestCompanyId,
      UUID receiveCompanyId,
      UUID productId,
      UUID shipmentId){
    this.orderId = orderId;
    this.orderDate = orderDate;
    this.quantity = quantity;
    this.request = request;
    this.username = username;
    this.requestCompanyId = requestCompanyId;
    this.receiveCompanyId = receiveCompanyId;
    this.productId = productId;
    this.shipmentId = shipmentId;
  }
}
