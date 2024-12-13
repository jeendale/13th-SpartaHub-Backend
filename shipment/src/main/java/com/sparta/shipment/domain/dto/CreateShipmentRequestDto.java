package com.sparta.shipment.domain.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateShipmentRequestDto {

    private UUID orderId;
    private String shipmentStatus;
    private UUID startHubId;
    private UUID endHubId;
}
