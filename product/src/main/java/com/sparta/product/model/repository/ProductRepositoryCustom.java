package com.sparta.product.model.repository;

import com.sparta.product.domain.dto.response.ProductResponseDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<ProductResponseDto> searchProducts(String productName, UUID hubId, UUID companyId, Pageable pageable);
}
