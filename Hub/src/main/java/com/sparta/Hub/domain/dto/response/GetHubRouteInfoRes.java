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
  private BigDecimal distance;
  private LocalDateTime deliveryTime;

  public GetHubRouteInfoRes(UUID hubRouteId, BigDecimal distance, LocalDateTime deliveryTime) {
    this.hubRouteId = hubRouteId;
    this.distance = distance;
    this.deliveryTime = deliveryTime;
  }

}
