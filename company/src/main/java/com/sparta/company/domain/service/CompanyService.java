package com.sparta.company.domain.service;

import com.sparta.company.domain.dto.request.CompanyRequestDto;
import com.sparta.company.domain.dto.response.CompanyIdResponseDto;
import com.sparta.company.domain.dto.response.CompanyResponseDto;
import com.sparta.company.excpetion.CompanyExceptionMessage;
import com.sparta.company.excpetion.FeignClientExceptionMessage;
import com.sparta.company.excpetion.UserServiceNotAvailableException;
import com.sparta.company.infrastructure.dto.GetHubInfoRes;
import com.sparta.company.infrastructure.dto.UserResponseDto;
import com.sparta.company.model.entity.Company;
import com.sparta.company.model.repository.CompanyRepository;
import feign.FeignException.BadRequest;
import feign.FeignException.ServiceUnavailable;
import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserClientService userClientService;
    private final HubClientService hubClientService;

    @Transactional
    @Retry(name = "userServiceRetry")
    @CircuitBreaker(name = "userAndHubService", fallbackMethod = "fallback")
    // Hub 구현 시 HUB_MANAGER 가 생성 시 요청 HubId가 담당 허브의 HubId 인지 검증 필요
    public CompanyIdResponseDto createCompany(
            CompanyRequestDto requestDto,
            String requestUsername,
            String requestRole) {

        UserResponseDto userResponseDto = getUserResponseDto(requestDto.getUsername());
        validateUserRoleIsCompanyManager(Objects.requireNonNull(userResponseDto));

        GetHubInfoRes getHubInfoRes = getHubInfoResponse(requestDto.getHubId());
        // 아래 HUB_MANAGER 가 생성 시 요청 HubId가 담당 허브의 HubId 인지 검증하는 메서드 추가

        Company company = requestDto.toEntity();
        companyRepository.save(company);

        return CompanyIdResponseDto.builder()
                .companyId(company.getCompanyId())
                .build();
    }

    public CompanyResponseDto getCompany(UUID companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException(CompanyExceptionMessage.COMPANY_NOT_FOUND.getMessage()));

        return CompanyResponseDto.builder()
                .companyId(company.getCompanyId())
                .hubId(company.getHubId())
                .username(company.getUsername())
                .companyName(company.getCompanyName())
                .companyAddress(company.getCompanyAddress())
                .companyType(company.getCompanyType())
                .build();
    }

    private GetHubInfoRes getHubInfoResponse(UUID hubId) {
        return hubClientService.getHub(hubId).getBody();
    }

    private UserResponseDto getUserResponseDto(String username) {
        log.info("User request for username: {}", username);
        return userClientService.getUser(username).getBody();
    }

    private void validateUserRoleIsCompanyManager(UserResponseDto userResponseDto) {
        if (!userResponseDto.getRole().equals("COMPANY_MANAGER")) {
            throw new IllegalArgumentException(CompanyExceptionMessage.NOT_COMPANY_MANAGER.getMessage());
        }
    }

    public CompanyIdResponseDto fallback(Throwable throwable) {
        if (throwable instanceof BadRequest badRequestException) {
            log.warn("User 400 Bad Request 발생: {}", throwable.getMessage());
            if (throwable.getMessage().contains(FeignClientExceptionMessage.HUB_NOT_FOUND.getMessage())) {
                throw new IllegalArgumentException(FeignClientExceptionMessage.HUB_NOT_FOUND.getMessage());
            }

            if (throwable.getMessage().contains(FeignClientExceptionMessage.USER_NOT_FOUND.getMessage())) {
                throw new IllegalArgumentException(FeignClientExceptionMessage.USER_NOT_FOUND.getMessage());
            }
        }

        if (throwable instanceof RetryableException retryableException) {
            log.warn("RetryableException 발생");
            throw new UserServiceNotAvailableException(FeignClientExceptionMessage.SERVICE_NOT_AVAILABLE.getMessage());
        }

        if (throwable instanceof ServiceUnavailable serviceUnavailableException) {
            log.warn("ServiceUnavailableException 발생");
            throw new UserServiceNotAvailableException(FeignClientExceptionMessage.SERVICE_NOT_AVAILABLE.getMessage());
        }

        log.warn("기타 예외 발생: {}", String.valueOf(throwable));
        return null;
    }
}
