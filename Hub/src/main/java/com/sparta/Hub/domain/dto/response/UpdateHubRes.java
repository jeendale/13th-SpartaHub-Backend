package com.sparta.Hub.domain.dto.response;


import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class UpdateHubRes {
  private UUID hubId;

  public UpdateHubRes(UUID hubId) {
    this.hubId = hubId;
  }
}
