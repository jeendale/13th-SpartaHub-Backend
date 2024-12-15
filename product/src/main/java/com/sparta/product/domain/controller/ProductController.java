package com.sparta.product.domain.controller;

import com.sparta.product.domain.dto.request.ProductRequestDto;
import com.sparta.product.domain.dto.request.UpdateProductRequestDto;
import com.sparta.product.domain.dto.response.ProductIdResponseDto;
import com.sparta.product.domain.dto.response.ProductResponseDto;
import com.sparta.product.domain.service.ProductService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    ResponseEntity<ProductIdResponseDto> createProduct(
            @RequestBody ProductRequestDto requestDto,
            @RequestHeader("X-User-Username") String requestUsername,
            @RequestHeader("X-User-Role") String requestRole) {

        ProductIdResponseDto responseDto = productService.createProduct(requestDto, requestUsername, requestRole);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{productId}")
    ResponseEntity<ProductResponseDto> getProduct(
            @PathVariable UUID productId,
            @RequestHeader("X-User-Username") String requestUsername,
            @RequestHeader("X-User-Role") String requestRole) {

        ProductResponseDto responseDto = productService.getProduct(productId, requestUsername, requestRole);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping
    ResponseEntity<PagedModel<ProductResponseDto>> getProducts(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) UUID hubId,
            @RequestParam(required = false) UUID companyId,
            @RequestHeader("X-User-Username") String requestUsername,
            @RequestHeader("X-User-Role") String requestRole,
            Pageable pageable) {

        Page<ProductResponseDto> responseDtos = productService.getProducts(
                productName, hubId, companyId, requestUsername, requestRole, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(new PagedModel<>(responseDtos));
    }

    @PutMapping("/{productId}")
    ResponseEntity<ProductIdResponseDto> updateProduct(
            @PathVariable UUID productId,
            @RequestBody UpdateProductRequestDto requestDto,
            @RequestHeader("X-User-Username") String requestUsername,
            @RequestHeader("X-User-Role") String requestRole) {

        ProductIdResponseDto responseDto = productService.updateProduct(
                productId, requestDto, requestUsername, requestRole);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/{productId}")
    ResponseEntity<ProductIdResponseDto> deleteProduct(
            @PathVariable UUID productId,
            @RequestHeader("X-User-Username") String requestUsername,
            @RequestHeader("X-User-Role") String requestRole) {

        ProductIdResponseDto responseDto = productService.deleteProduct(productId, requestUsername, requestRole);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
