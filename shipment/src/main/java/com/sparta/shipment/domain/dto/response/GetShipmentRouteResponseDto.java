package com.sparta.shipment.domain.dto.response;

import com.sparta.shipment.model.entity.ShipmentRoute;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class GetShipmentRouteResponseDto {

    private UUID shipmentRouteId;
    private UUID shipmentId;
    private UUID shipmentManagerId;
    private int routeSeq;
    private UUID startHubId;
    private UUID endHubId;
    private BigDecimal expectedDistance;
    private BigDecimal expectedTime;
    private BigDecimal realDistance;
    private BigDecimal realTime;
    private String shipmentStatus;

    public static GetShipmentRouteResponseDto of(ShipmentRoute shipmentRoute) {
        return GetShipmentRouteResponseDto.builder()
                .shipmentRouteId(shipmentRoute.getShipmentRouteId())
                .shipmentId(shipmentRoute.getShipment().getShipmentId())
                .shipmentManagerId(shipmentRoute.getShipmentManager().getShipmentManagerId())
                .routeSeq(shipmentRoute.getRouteSeq())
                .startHubId(shipmentRoute.getStartHubId())
                .endHubId(shipmentRoute.getEndHubId())
                .expectedDistance(shipmentRoute.getExpectedDistance())
                .expectedTime(shipmentRoute.getExpectedTime())
                .realDistance(shipmentRoute.getRealDistance())
                .realTime(shipmentRoute.getRealTime())
                .shipmentStatus(shipmentRoute.getShipmentStatus().toString())
                .build();
    }
}
