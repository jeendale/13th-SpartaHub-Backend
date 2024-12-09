package com.sparta.shipment.domain.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentRouteResponseDto {

    private UUID shipmentRouteId;
    private UUID shipmentManagerId;
    private int routeSeq;
    private UUID startHubId;
    private UUID endHubId;
    private double expectedDistance;
    private double expectedTime;
    private double realDistance;
    private double realTime;
    private String shipmentStatus;
}
