package com.sparta.product.domain.dto.request;

import com.sparta.product.model.entity.Product;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductRequestDto {
    private UUID productId;
    private UUID hubId;
    private UUID companyId;
    private String productName;

    public Product toEntity() {
        return Product.builder()
                .productId(productId)
                .hubId(hubId)
                .companyId(companyId)
                .productName(productName)
                .build();
    }
}
