package com.sparta.Hub.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class UpdateHubReq {
  private String hubname;
  private String adress;
  private double lati;
  private double longti;
  private boolean isCenterHub=false;
}
