package com.sparta.Hub.domain.dto.response;


import java.io.Serializable;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HubIdRes implements Serializable {
  private UUID hubId;
}
