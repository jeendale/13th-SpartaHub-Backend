package com.sparta.product.domain.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductResponseDto {
    private UUID productId;
    private UUID hubId;
    private UUID companyId;
    private String productName;

    @Builder
    @QueryProjection
    public ProductResponseDto(UUID productId, UUID hubId, UUID companyId, String productName) {
        this.productId = productId;
        this.hubId = hubId;
        this.companyId = companyId;
        this.productName = productName;
    }
}
