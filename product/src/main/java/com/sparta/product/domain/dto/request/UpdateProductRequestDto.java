package com.sparta.product.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateProductRequestDto {
    private String productName;
}
