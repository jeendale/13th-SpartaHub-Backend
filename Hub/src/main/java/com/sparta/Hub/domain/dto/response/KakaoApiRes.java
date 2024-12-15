package com.sparta.Hub.domain.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드를 받는 생성자
@Builder
public class KakaoApiRes {
  private BigDecimal deliveryTime;
  private BigDecimal distance;
}
