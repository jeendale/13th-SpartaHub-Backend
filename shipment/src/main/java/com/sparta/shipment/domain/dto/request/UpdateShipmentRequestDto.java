package com.sparta.shipment.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateShipmentRequestDto {

    private String shipmentStatus;
    private String receiverSlackId;
}
