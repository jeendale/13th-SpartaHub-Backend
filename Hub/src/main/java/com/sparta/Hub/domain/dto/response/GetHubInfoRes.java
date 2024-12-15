package com.sparta.Hub.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetHubInfoRes implements Serializable {

  private UUID hubId;
  private String hubName;
  private String address;
  private double lati;
  private double longti;
  private String username;
  @JsonProperty("centerHub")
  private boolean isCenterHub;

  @Builder
  public GetHubInfoRes(UUID hubId, String hubName, String address, double lati, double longti, boolean isCenterHub,String username) {
    this.hubId = hubId;
    this.hubName = hubName;
    this.address = address;
    this.lati = lati;
    this.longti = longti;
    this.isCenterHub = isCenterHub;
  }
}
