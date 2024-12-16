package com.sparta.order.infrastructure.dto;


import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateShipmentManagerRequestDto {

  private String username;

  private UUID inHubId;

  private String managerType;

}