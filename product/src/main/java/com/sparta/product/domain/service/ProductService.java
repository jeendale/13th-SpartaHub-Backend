package com.sparta.product.domain.service;

import com.sparta.product.domain.dto.request.ProductRequestDto;
import com.sparta.product.domain.dto.response.ProductIdResponseDto;
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
import io.github.resilience4j.retry.annotation.Retry;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CompanyClientService companyClientService;
    private final HubClientService hubClientService;

    @Transactional
    @CircuitBreaker(name = "company-service", fallbackMethod = "fallback")
    public ProductIdResponseDto createProduct(ProductRequestDto requestDto, String requestUsername, String requestRole) {
        validateCreateRequestRole(requestRole);

        GetHubInfoRes getHubInfoRes = getHubInfoRes(requestDto.getHubId());

        if (requestRole.equals("HUB_MANAGER")) {
            validateOwnHub(getHubInfoRes.getUsername(), requestUsername);
        }

        CompanyResponseDto companyResponseDto = getCompanyResponseDto(requestDto.getCompanyId());

        if (requestRole.equals("COMPANY_MANAGER")) {
            validateOwnCompany(companyResponseDto.getUsername(), requestUsername);
        }

        Product product = requestDto.toEntity();
        productRepository.save(product);

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

    private void validateCreateRequestRole(String requestRole) {
        if (!requestRole.equals("HUB_MANAGER") && !requestRole.equals("COMPANY_MANAGER") && !requestRole.equals("MASTER")) {
            throw new IllegalArgumentException(ProductExceptionMessage.NOT_ALLOWED_API.getMessage());
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
