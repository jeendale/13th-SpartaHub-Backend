package com.sparta.order.infrastructure.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateShipmentRequestDto {


  private UUID orderId;


  private UUID startHubId;


  private UUID endHubId;


  private String shippingAddress;

  private String receiverName;

  private String receiverSlackId;

}
