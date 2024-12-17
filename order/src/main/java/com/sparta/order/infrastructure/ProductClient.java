package com.sparta.order.infrastructure;


import com.sparta.order.domain.service.ProductClientService;
import com.sparta.order.infrastructure.dto.ProductIdResponseDto;
import com.sparta.order.infrastructure.dto.ProductResponseDto;
import com.sparta.order.infrastructure.dto.UpdateProductRequestDto;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="product-service")
public interface ProductClient extends ProductClientService {
  @GetMapping(value ="/api/v1/products/{productId}", headers = {"X-User-Username=\"\"", "X-User-Role=MASTER"})
  ResponseEntity<ProductResponseDto> getProduct(@PathVariable UUID productId);

  @PutMapping(value ="/api/v1/products/{productId}", headers = {"X-User-Username=\"\"", "X-User-Role=MASTER"})
  ResponseEntity<ProductIdResponseDto> updateProduct(@PathVariable UUID productId,
   @RequestBody UpdateProductRequestDto requestDto);
}
