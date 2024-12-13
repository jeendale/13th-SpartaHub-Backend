package com.sparta.company.domain.service;

import com.sparta.company.domain.dto.request.CompanyRequestDto;
import com.sparta.company.domain.dto.response.CompanyIdResponseDto;
import com.sparta.company.excpetion.UserServiceNotAvailableException;
import com.sparta.company.infrastructure.dto.UserResponseDto;
import com.sparta.company.model.entity.Company;
import com.sparta.company.model.repository.CompanyRepository;
import feign.FeignException.BadRequest;
import feign.FeignException.ServiceUnavailable;
import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.Objects;
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

    @Transactional
    @Retry(name = "userServiceRetry")
    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackUser")
    // Hub 구현 시 HUB_MANAGER 가 생성 시 요청 HubId가 담당 허브의 HubId 인지 검증 필요
    // Hub 구현 시 요청 HubId가 실제로 존재하는 HubId 인지 검증 필요
    public CompanyIdResponseDto createCompany(
            CompanyRequestDto requestDto,
            String requestUsername,
            String requestRole) {

        UserResponseDto userResponseDto = getUserResponseDto(requestDto.getUsername());

        validateUserRoleIsCompanyManager(Objects.requireNonNull(userResponseDto));

        Company company = requestDto.toEntity();
        companyRepository.save(company);

        return CompanyIdResponseDto.builder()
                .companyId(company.getCompanyId())
                .build();
    }

    public UserResponseDto getUserResponseDto(String username) {
        log.info("User request for username: {}", username);
        return userClientService.getUser(username).getBody();
    }

    private void validateUserRoleIsCompanyManager(UserResponseDto userResponseDto) {
        if (!userResponseDto.getRole().equals("COMPANY_MANAGER")) {
            throw new IllegalArgumentException("해당 사용자는 업체 관리자가 아닙니다.");
        }
    }

    public CompanyIdResponseDto fallbackUser(Throwable throwable) {
        if (throwable instanceof BadRequest badRequestException) {
            log.warn("400 Bad Request 발생: {}", throwable.toString());
            throw new IllegalArgumentException("해당 사용자가 존재하지 않습니다.");
        }

        if (throwable instanceof RetryableException retryableException) {
            log.warn("RetryableException 발생");
            throw new UserServiceNotAvailableException("User 서비스에 연결할 수 없습니다.");
        }

        if (throwable instanceof ServiceUnavailable serviceUnavailableException) {
            log.warn("ServiceUnavailableException 발생");
            throw new UserServiceNotAvailableException("User 서비스에 연결할 수 없습니다.");
        }

        log.warn("기타 예외 발생: {}", String.valueOf(throwable));
        throw new RuntimeException(throwable.getMessage());
    }
}
