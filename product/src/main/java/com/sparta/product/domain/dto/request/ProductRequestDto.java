package com.sparta.product.domain.dto.request;

import com.sparta.product.model.entity.Product;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductRequestDto {
    private UUID hubId;
    private UUID companyId;
    private String productName;
    @Min(0)
    private Integer count;

    public Product toEntity() {
        return Product.builder()
                .hubId(hubId)
                .companyId(companyId)
                .productName(productName)
                .count(count)
                .build();
    }
}
