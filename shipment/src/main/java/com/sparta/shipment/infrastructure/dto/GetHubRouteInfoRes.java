package com.sparta.shipment.infrastructure.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetHubRouteInfoRes implements Serializable {
    private UUID hubRouteId;
    private UUID startHubId;
    private UUID endHubId;
    private BigDecimal distance;
    private BigDecimal deliveryTime;

}
