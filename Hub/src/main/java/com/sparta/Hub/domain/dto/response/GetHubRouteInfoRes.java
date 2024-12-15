package com.sparta.Hub.domain.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class GetHubRouteInfoRes implements Serializable {
  private UUID hubRouteId;
  private UUID startHubId;
  private UUID endHubId;
  private String startHubName;
  private String endHubName;
  private BigDecimal distance;
  private BigDecimal deliveryTime;

  public GetHubRouteInfoRes(UUID hubRouteId,UUID startHubId,UUID endHubId,String startHubName,String endHubName, BigDecimal distance, BigDecimal deliveryTime) {
    this.hubRouteId = hubRouteId;
    this.startHubId = startHubId;
    this.endHubId = endHubId;
    this.startHubName = startHubName;
    this.endHubName = endHubName;
    this.distance = distance;
    this.deliveryTime = deliveryTime;
  }

}
