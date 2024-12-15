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
  private String startHubName;
  private String endHubName;
  private BigDecimal distance;
  private BigDecimal deliveryTime;

  public GetHubRouteInfoRes(UUID hubRouteId,String startHubName,String endHubName, BigDecimal distance, BigDecimal deliveryTime) {
    this.hubRouteId = hubRouteId;
    this.startHubName = startHubName;
    this.endHubName = endHubName;
    this.distance = distance;
    this.deliveryTime = deliveryTime;
  }

}
