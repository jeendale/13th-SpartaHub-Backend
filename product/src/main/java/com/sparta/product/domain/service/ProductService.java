package com.sparta.product.domain.service;

import com.sparta.product.domain.dto.request.ProductRequestDto;
import com.sparta.product.domain.dto.request.UpdateProductRequestDto;
import com.sparta.product.domain.dto.response.ProductIdResponseDto;
import com.sparta.product.domain.dto.response.ProductResponseDto;
import com.sparta.product.exception.FeignClientExceptionMessage;
import com.sparta.product.exception.ProductExceptionMessage;
import com.sparta.product.exception.ServiceNotAvailableException;
import com.sparta.product.infrastructure.dto.CompanyResponseDto;
import com.sparta.product.infrastructure.dto.GetHubInfoRes;
import com.sparta.product.model.entity.Product;
import com.sparta.product.model.repository.ProductRepository;
import feign.FeignException.BadRequest;
import feign.FeignException.ServiceUnavailable;
import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CompanyClientService companyClientService;
    private final HubClientService hubClientService;

    @Transactional
    @CircuitBreaker(name = "company-service", fallbackMethod = "fallback")
    public ProductIdResponseDto createProduct(ProductRequestDto requestDto, String requestUsername,
                                              String requestRole) {
        validateRequestRole(requestRole);

        GetHubInfoRes getHubInfoRes = getHubInfoRes(requestDto.getHubId());

        if (requestRole.equals("HUB_MANAGER")) {
            validateOwnHub(getHubInfoRes.getUsername(), requestUsername);
        }

        CompanyResponseDto companyResponseDto = getCompanyResponseDto(requestDto.getCompanyId());

        if (requestRole.equals("COMPANY_MANAGER")) {
            validateOwnCompany(companyResponseDto.getUsername(), requestUsername);
        }

        validateCompanyHubId(companyResponseDto.getHubId(), getHubInfoRes.getHubId());

        Product product = requestDto.toEntity();
        productRepository.save(product);

        return ProductIdResponseDto.builder()
                .productId(product.getProductId())
                .build();
    }

    @CircuitBreaker(name = "company-service", fallbackMethod = "fallback")
    public ProductResponseDto getProduct(UUID productId, String requestUsername, String requestRole) {
        Product product = productRepository.findById(productId)
                .orElseThrow(
                        () -> new IllegalArgumentException(ProductExceptionMessage.PRODUCT_NOT_FOUND.getMessage()));

        validateDeletedProduct(product);

        if (requestRole.equals("HUB_MANAGER")) {
            GetHubInfoRes getHubInfoRes = getHubInfoRes(product.getHubId());
            validateOwnHub(getHubInfoRes.getUsername(), requestUsername);
        }

        return ProductResponseDto.builder()
                .productId(product.getProductId())
                .hubId(product.getHubId())
                .companyId(product.getCompanyId())
                .productName(product.getProductName())
                .build();
    }

    @CircuitBreaker(name = "company-service", fallbackMethod = "fallback")
    public Page<ProductResponseDto> getProducts(String productName, UUID hubId, UUID companyId,
                                                String requestUsername, String requestRole, Pageable pageable) {

        if (requestRole.equals("HUB_MANAGER")) {
            if (hubId == null) {
                throw new IllegalArgumentException(ProductExceptionMessage.REQUIRE_HUB_ID.getMessage());
            }
            GetHubInfoRes getHubInfoRes = getHubInfoRes(hubId);

            validateOwnHub(getHubInfoRes.getUsername(), requestUsername);

            if (companyId == null) {
                return productRepository.searchProducts(productName, hubId, companyId, pageable);
            }

            CompanyResponseDto companyResponseDto = getCompanyResponseDto(companyId);

            validateCompanyHubId(companyResponseDto.getHubId(), getHubInfoRes.getHubId());

            return productRepository.searchProducts(productName, hubId, companyId, pageable);
        }

        return productRepository.searchProducts(productName, hubId, companyId, pageable);
    }

    @Transactional
    @CircuitBreaker(name = "company-service", fallbackMethod = "fallback")
    public ProductIdResponseDto updateProduct(UUID productId, UpdateProductRequestDto requestDto,
                                              String requestUsername, String requestRole) {
        validateRequestRole(requestRole);

        Product product = productRepository.findById(productId)
                .orElseThrow(
                        () -> new IllegalArgumentException(ProductExceptionMessage.PRODUCT_NOT_FOUND.getMessage()));

        validateDeletedProduct(product);

        GetHubInfoRes getHubInfoRes = getHubInfoRes(product.getHubId());

        if (requestRole.equals("HUB_MANAGER")) {
            validateOwnHub(getHubInfoRes.getUsername(), requestUsername);
        }

        CompanyResponseDto companyResponseDto = getCompanyResponseDto(product.getCompanyId());

        if (requestRole.equals("COMPANY_MANAGER")) {
            validateOwnCompany(companyResponseDto.getUsername(), requestUsername);
        }

        updateProduct(product, requestDto);

        return ProductIdResponseDto.builder()
                .productId(product.getProductId())
                .build();
    }

    @Transactional
    @CircuitBreaker(name = "company-service", fallbackMethod = "fallback")
    public ProductIdResponseDto deleteProduct(UUID productId, String requestUsername, String requestRole) {
        validateDeleteRequestRole(requestRole);

        Product product = productRepository.findById(productId)
                .orElseThrow(
                        () -> new IllegalArgumentException(ProductExceptionMessage.PRODUCT_NOT_FOUND.getMessage()));

        validateDeletedProduct(product);

        GetHubInfoRes getHubInfoRes = getHubInfoRes(product.getHubId());

        if (requestRole.equals("HUB_MANAGER")) {
            validateOwnHub(getHubInfoRes.getUsername(), requestUsername);
        }

        product.updateDeleted(requestUsername);

        return ProductIdResponseDto.builder()
                .productId(product.getProductId())
                .build();
    }

    private GetHubInfoRes getHubInfoRes(UUID requestHubId) {
        return hubClientService.getHub(requestHubId).getBody();
    }

    private CompanyResponseDto getCompanyResponseDto(UUID requestCompanyId) {
        return companyClientService.getCompany(requestCompanyId).getBody();
    }

    private void validateCompanyHubId(UUID companyHubID, UUID requestHubId) {
        if (!companyHubID.equals(requestHubId)) {
            throw new IllegalArgumentException(ProductExceptionMessage.NOT_COMPANY_HUB.getMessage());
        }
    }

    private void validateRequestRole(String requestRole) {
        if (!requestRole.equals("HUB_MANAGER") && !requestRole.equals("COMPANY_MANAGER") && !requestRole.equals(
                "MASTER")) {
            throw new IllegalArgumentException(ProductExceptionMessage.NOT_ALLOWED_API.getMessage());
        }
    }

    private void validateDeleteRequestRole(String requestRole) {
        if (!requestRole.equals("HUB_MANAGER") && !requestRole.equals("MASTER")) {
            throw new IllegalArgumentException(ProductExceptionMessage.NOT_ALLOWED_API.getMessage());
        }
    }

    private void validateDeletedProduct(Product product) {
        if (product.isDeleted()) {
            throw new IllegalArgumentException(ProductExceptionMessage.DELETED_PRODUCT.getMessage());
        }
    }

    private void validateOwnHub(String hubUsername, String requestUsername) {
        if (!hubUsername.equals(requestUsername)) {
            throw new IllegalArgumentException(ProductExceptionMessage.NOT_OWN_HUB.getMessage());
        }
    }

    private void validateOwnCompany(String companyUsername, String requestUsername) {
        if (!companyUsername.equals(requestUsername)) {
            throw new IllegalArgumentException(ProductExceptionMessage.NOT_OWN_COMPANY.getMessage());
        }
    }

    private void updateProduct(Product product, UpdateProductRequestDto requestDto) {
        if (requestDto.getProductName() != null) {
            product.updateProductName(requestDto.getProductName());
        }

        if (requestDto.getCount() != null) {
            product.updateCount(requestDto.getCount());
        }
    }

    public ProductIdResponseDto fallback(Throwable throwable) {
        if (throwable instanceof BadRequest) {
            log.warn("400 Bad Request 발생: {}", throwable.getMessage());
            if (throwable.getMessage().contains(FeignClientExceptionMessage.HUB_NOT_FOUND.getMessage())) {
                throw new IllegalArgumentException(FeignClientExceptionMessage.HUB_NOT_FOUND.getMessage());
            }

            if (throwable.getMessage().contains(FeignClientExceptionMessage.COMPANY_NOT_FOUND.getMessage())) {
                throw new IllegalArgumentException(FeignClientExceptionMessage.COMPANY_NOT_FOUND.getMessage());
            }
        }

        if (throwable instanceof RetryableException) {
            log.warn("RetryableException 발생");
            throw new ServiceNotAvailableException(FeignClientExceptionMessage.SERVICE_NOT_AVAILABLE.getMessage());
        }

        if (throwable instanceof ServiceUnavailable) {
            log.warn("ServiceUnavailableException 발생");
            throw new ServiceNotAvailableException(FeignClientExceptionMessage.SERVICE_NOT_AVAILABLE.getMessage());
        }

        if (throwable instanceof IllegalArgumentException) {
            log.warn("IllegalArgumentException 발생");
            throw new IllegalArgumentException(throwable.getMessage());
        }

        log.warn("기타 예외 발생: {}", String.valueOf(throwable));
        throw new ServiceNotAvailableException(FeignClientExceptionMessage.SERVICE_NOT_AVAILABLE.getMessage());
    }
}
