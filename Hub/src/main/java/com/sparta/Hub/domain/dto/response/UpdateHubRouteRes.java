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
public class UpdateHubRouteRes implements Serializable {
  private UUID hubRouteId;
  private LocalDateTime deliveryTime;
  private BigDecimal distance;

  public UpdateHubRouteRes(UUID hubRouteId, LocalDateTime deliveryTime, BigDecimal distance) {
    this.hubRouteId = hubRouteId;
    this.deliveryTime = deliveryTime;
    this.distance = distance;
  }

}
