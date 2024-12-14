package com.sparta.Hub.domain.dto.response;


import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HubIdRes {
  private UUID hubId;
}
