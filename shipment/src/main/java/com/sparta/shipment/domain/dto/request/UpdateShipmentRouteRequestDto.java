package com.sparta.shipment.domain.dto.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateShipmentRouteRequestDto {

    private BigDecimal realDistance;
    private BigDecimal realTime;
    private String shipmentStatus;
}
