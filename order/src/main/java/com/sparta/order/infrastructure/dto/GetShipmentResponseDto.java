package com.sparta.order.infrastructure.dto;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class GetShipmentResponseDto {
  private UUID shipmentId;
  private UUID orderId;
  private UUID shipmentManagerId;
  private String shipmentStatus;
  private UUID startHubId;
  private UUID endHubId;
  private String shippingAddress;
  private String receiverName;
  private String receiverSlackId;

  public GetShipmentResponseDto(
      UUID shipmentId,
      UUID orderId,
      UUID shipmentManagerId,
      String shipmentStatus,
      UUID startHubId,
      UUID endHubId,
      String shippingAddress,
      String receiverName,
      String receiverSlackId) {
    this.shipmentId = shipmentId;
    this.orderId=orderId;
    this.shipmentManagerId=shipmentManagerId;
    this.shipmentStatus=shipmentStatus;
    this.startHubId=startHubId;
    this.endHubId=endHubId;
    this.shippingAddress=shippingAddress;
    this.receiverName=receiverName;
    this.receiverSlackId=receiverSlackId;
  }
}