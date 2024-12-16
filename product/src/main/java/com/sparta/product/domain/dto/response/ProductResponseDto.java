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
    private Integer count;

    @Builder
    @QueryProjection
    public ProductResponseDto(UUID productId, UUID hubId, UUID companyId, String productName, Integer count) {
        this.productId = productId;
        this.hubId = hubId;
        this.companyId = companyId;
        this.productName = productName;
        this.count = count;
    }
}
