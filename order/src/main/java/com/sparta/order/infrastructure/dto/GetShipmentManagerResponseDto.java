package com.sparta.order.infrastructure.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class GetShipmentManagerResponseDto {
  private UUID shipmentManagerId;
  private String username;
  private String managerType;
  private UUID inHubId;
  private int shipmentSeq;
  public GetShipmentManagerResponseDto(UUID shipmentManagerId, String username, String managerType,UUID inHubId, int shipmentSeq) {
    this.shipmentManagerId = shipmentManagerId;
    this.username = username;
    this.managerType = managerType;
    this.inHubId = inHubId;
    this.shipmentSeq = shipmentSeq;
  }
}
