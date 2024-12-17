package com.sparta.order.infrastructure.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShipmentResponseDto {
  private UUID shipmentId;
}
