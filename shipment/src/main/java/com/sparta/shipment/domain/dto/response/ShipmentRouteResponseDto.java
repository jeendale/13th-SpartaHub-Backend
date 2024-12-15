package com.sparta.shipment.domain.dto.response;

import com.sparta.shipment.model.entity.ShipmentRoute;
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
public class ShipmentRouteResponseDto {

    private UUID shipmentRouteId;

    public static ShipmentRouteResponseDto of(ShipmentRoute shipmentRoute) {
        return ShipmentRouteResponseDto.builder()
                .shipmentRouteId(shipmentRoute.getShipmentRouteId())
                .build();
    }
}
