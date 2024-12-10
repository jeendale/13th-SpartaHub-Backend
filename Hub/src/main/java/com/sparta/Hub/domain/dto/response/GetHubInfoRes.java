package com.sparta.Hub.domain.dto.response;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetHubInfoRes {

  private UUID hubId;
  private String hubName;
  private String address;
  private double lati;
  private double longti;
  private boolean isCenterHub;

  @Builder
  public GetHubInfoRes(UUID hubId, String hubName, String address, double lati, double longti, boolean isCenterHub) {
    this.hubId = hubId;
    this.hubName = hubName;
    this.address = address;
    this.lati = lati;
    this.longti = longti;
    this.isCenterHub = isCenterHub;
  }
}
