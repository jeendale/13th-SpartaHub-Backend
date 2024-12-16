package com.sparta.product.domain.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateProductRequestDto {
    private String productName;
    @Min(0)
    private Integer count;
}
