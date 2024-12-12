package com.sparta.user.domain.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtTokenResponseDto {
    private String accessToken;
}
