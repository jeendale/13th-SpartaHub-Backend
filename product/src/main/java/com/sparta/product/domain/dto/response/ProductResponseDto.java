package com.sparta.product.domain.dto.response;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponseDto {
    private UUID productId;
    private UUID hubId;
    private UUID companyId;
    private String productName;
}
