package com.sparta.order.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateOredrReq {
  private int quantity;
  private String request;
}
