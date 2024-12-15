package com.sparta.Hub.domain.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class CreateHubRouteRes {
  private UUID hubRouteId;
  private UUID startHubId;
  private UUID endHubId;
  private String startHubName;
  private String endHubName;
  private LocalDateTime deliveryTime;
  private BigDecimal distance;


  public CreateHubRouteRes(UUID hubRouteId,UUID startHubId,UUID endHubId,String startHubName,String endHubName, LocalDateTime deliveryTime, BigDecimal distance) {
    this.hubRouteId = hubRouteId;
    this.startHubId = startHubId;
    this.endHubId = endHubId;
    this.startHubName = startHubName;
    this.endHubName = endHubName;
    this.deliveryTime = deliveryTime;
    this.distance = distance;
  }

}
