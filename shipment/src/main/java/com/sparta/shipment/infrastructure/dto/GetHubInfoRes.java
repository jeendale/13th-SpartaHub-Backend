package com.sparta.shipment.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetHubInfoRes {
    private UUID hubId;
    private String username;
    private String hubName;
    private String address;
    private double lati;
    private double longti;
    @JsonProperty("centerHub")
    private boolean isCenterHub;
}
