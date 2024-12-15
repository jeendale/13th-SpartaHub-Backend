package com.sparta.shipment.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentUpdateRequestDto {

    private String shipmentStatus;
    private String receiverSlackId;
}
