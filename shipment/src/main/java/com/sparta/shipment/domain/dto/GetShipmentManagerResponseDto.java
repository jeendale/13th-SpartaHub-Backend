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
public class GetShipmentManagerResponseDto {

    private UUID shipmentManagerId;
    private String username;
    private String managerType;
    private UUID inHubId;
    private int shipmentSeq;
    //TODO: user 연동해서 slackID가져오기?
    //private String slackId;

    public static GetShipmentManagerResponseDto of(ShipmentManager shipmentManager) {
        return GetShipmentManagerResponseDto.builder()
                .shipmentManagerId(shipmentManager.getShipmentManagerId())
                .username(shipmentManager.getUsername())
                .managerType(shipmentManager.getManagerType().toString())
                .inHubId(shipmentManager.getInHubId())
                .shipmentSeq(shipmentManager.getShipmentSeq())
                .build();
    }
}
