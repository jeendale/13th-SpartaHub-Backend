package com.sparta.Hub.domain.dto.response;

import java.io.Serializable;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class DeleteHubRes implements Serializable {
  private UUID hubId;

  public DeleteHubRes(UUID hubId) {
    this.hubId = hubId;
  }
}
