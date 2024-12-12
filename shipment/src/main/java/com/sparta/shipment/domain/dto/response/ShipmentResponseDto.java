package com.sparta.shipment.domain.dto.response;

import com.sparta.shipment.model.entity.Shipment;
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
public class ShipmentResponseDto {
    private UUID shipmentId;

    public static ShipmentResponseDto of(Shipment shipment) {
        return ShipmentResponseDto.builder()
                .shipmentId(shipment.getShipmentId())
                .build();
    }
}
