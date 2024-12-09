package com.sparta.shipment.domain.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentResponseDto {
    private UUID shipmentManagerId;
    private String username;
    private String managerType;
    private UUID inHubId;
    private String slackId;
}
