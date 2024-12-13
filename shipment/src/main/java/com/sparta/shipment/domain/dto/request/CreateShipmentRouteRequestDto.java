package com.sparta.shipment.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateShipmentRouteRequestDto {

    @NotNull
    private UUID shipmentId;

    @NotNull
    private UUID shipmentManagerId;

    @NotNull
    private Integer routeSeq;

    @NotNull
    private UUID startHubId;

    @NotNull
    private UUID endHubId;

    @NotNull
    private BigDecimal expectedDistance;

    @NotNull
    private BigDecimal expectedTime;

    private BigDecimal realDistance;

    private BigDecimal realTime;

    @NotNull
    private String shipmentStatus;

}
