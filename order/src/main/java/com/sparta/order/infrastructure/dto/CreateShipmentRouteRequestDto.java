package com.sparta.order.infrastructure.dto;


import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateShipmentRouteRequestDto {


  private UUID shipmentId;

  private Integer routeSeq;

  private UUID startHubId;

  private UUID endHubId;

  private BigDecimal expectedDistance;

  private BigDecimal expectedTime;

  private BigDecimal realDistance;

  private BigDecimal realTime;

  private String shipmentStatus;

}
