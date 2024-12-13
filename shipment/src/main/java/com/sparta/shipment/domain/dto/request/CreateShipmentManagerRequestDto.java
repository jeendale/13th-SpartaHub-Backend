package com.sparta.shipment.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateShipmentManagerRequestDto {

    @NotNull
    private String username;

    @NotNull
    private UUID inHubId;

    @NotNull
    private String managerType;

}
