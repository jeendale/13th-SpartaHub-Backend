package com.sparta.shipment.domain.dto;

import com.sparta.shipment.model.entity.ShipmentManager;
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
public class ShipmentManagerResponseDto {
    private UUID shipmentManagerId;

    // Order -> OrderResponse 변경 Static 메서드
    public static ShipmentManagerResponseDto of(final ShipmentManager shipmentManager) {
        return ShipmentManagerResponseDto.builder()
                .shipmentManagerId(shipmentManager.getShipmentManagerId())
                .build();
    }
}
