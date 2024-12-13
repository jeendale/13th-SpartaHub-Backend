package com.sparta.Hub.domain.dto.request;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateHubRouteReq {
  private UUID endHubId;
}
