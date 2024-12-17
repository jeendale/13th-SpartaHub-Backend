package com.sparta.order.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateOredrReq {
  private Integer quantity;
  private String request;
}
