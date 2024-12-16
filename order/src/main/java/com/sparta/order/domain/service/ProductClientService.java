package com.sparta.order.domain.service;

import com.sparta.order.infrastructure.dto.ProductIdResponseDto;
import com.sparta.order.infrastructure.dto.ProductResponseDto;
import com.sparta.order.infrastructure.dto.UpdateProductRequestDto;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface ProductClientService {
  ResponseEntity<ProductResponseDto> getProduct(UUID productId);
  ResponseEntity<ProductIdResponseDto> updateProduct(UUID productId,
      UpdateProductRequestDto requestDto);
}
