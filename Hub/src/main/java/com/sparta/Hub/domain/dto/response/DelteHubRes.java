package com.sparta.Hub.domain.dto.response;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class DelteHubRes {
  private UUID hubId;

  public DelteHubRes(UUID hubId) {
    this.hubId = hubId;
  }
}
