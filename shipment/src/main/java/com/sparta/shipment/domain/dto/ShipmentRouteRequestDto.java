package com.sparta.shipment.domain.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentRouteRequestDto {

    private UUID shipmentId;
    private UUID shipmentManagerId;
    private UUID startHubId;
    private UUID endHubId;
    private BigDecimal expectedDistance;
    private BigDecimal expectedTime;
    private BigDecimal realDistance;
    private BigDecimal realTime;
    private String shipmentStatus;
}
