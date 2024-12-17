package com.sparta.shipment.domain.service;

import com.sparta.shipment.domain.dto.request.CreateShipmentManagerRequestDto;
import com.sparta.shipment.domain.dto.request.UpdateShipmentManagerRequestDto;
import com.sparta.shipment.domain.dto.response.GetShipmentManagerResponseDto;
import com.sparta.shipment.domain.dto.response.ShipmentManagerResponseDto;
import com.sparta.shipment.exception.FeignClientExceptionMessage;
import com.sparta.shipment.exception.ServiceNotAvailableException;
import com.sparta.shipment.exception.ShipmentCommonExceptionMessage;
import com.sparta.shipment.exception.ShipmentManagerExceptionMessage;
import com.sparta.shipment.infrastructure.dto.GetHubInfoRes;
import com.sparta.shipment.infrastructure.dto.UserResponseDto;
import com.sparta.shipment.model.entity.ShipmentManager;
import com.sparta.shipment.model.repository.ShipmentManagerRepository;
import feign.FeignException.BadRequest;
import feign.FeignException.ServiceUnavailable;
import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShipmentManagerService {

    @PersistenceContext
    private final EntityManager entityManager;

    private final ShipmentManagerRepository shipmentManagerRepository;
    private final UserClientService userClientService;
    private final HubClientService hubClientService;

    @Transactional
    @CircuitBreaker(name = "shipment-manager-service", fallbackMethod = "fallbackForDto")
    public ShipmentManagerResponseDto createShipmentManager(CreateShipmentManagerRequestDto request,
                                                            String requestUsername,
                                                            String requestRole) {

        validateRole(requestRole);

        // TODO: 인원도 10명까지만 가능한가?

        validateHub(request.getInHubId(), requestUsername, requestRole);

        validateUserRoleIsShipmentManager(request.getUsername());

        UUID shipmentManagerId = UUID.randomUUID();
        while (shipmentManagerRepository.existsById(shipmentManagerId)) {
            shipmentManagerId = UUID.randomUUID();
        }

        int shipmentSeq = getNextSequence(request.getManagerType());

        ShipmentManager shipmentManager = ShipmentManager.create(shipmentManagerId, request.getUsername(),
                request.getInHubId(), request.getManagerType(), false, shipmentSeq);

        shipmentManagerRepository.save(shipmentManager);

        return ShipmentManagerResponseDto.of(shipmentManager);

    }

    @Transactional
    @CircuitBreaker(name = "shipment-manager-service", fallbackMethod = "fallbackForDto")
    public ShipmentManagerResponseDto deleteShipmentManager(UUID shipmentManagerId, String requestUsername,
                                                            String requestRole) {
        validateRole(requestRole);

        ShipmentManager shipmentManager = findActiveByShipmentManagerId(shipmentManagerId);

        validateHub(shipmentManager.getInHubId(), requestUsername, requestRole);

        shipmentManager.updateDeleted(requestUsername);

        return ShipmentManagerResponseDto.of(shipmentManager);
    }

    @Transactional
    @CircuitBreaker(name = "shipment-manager-service", fallbackMethod = "fallbackForDto")
    public ShipmentManagerResponseDto updateShipmentManager(UUID shipmentManagerId,
                                                            UpdateShipmentManagerRequestDto request,
                                                            String requestUsername, String requestRole) {
        validateRole(requestRole);

        ShipmentManager shipmentManager = findActiveByShipmentManagerId(shipmentManagerId);

        validateHub(shipmentManager.getInHubId(), requestUsername, requestRole);

        ShipmentManager updatedShipmentManager = ShipmentManager.create(shipmentManagerId,
                shipmentManager.getUsername(),
                request.getInHubId() != null ? request.getInHubId() : shipmentManager.getInHubId(),
                request.getManagerType() != null ? request.getManagerType()
                        : shipmentManager.getManagerType().toString(),
                request.getIsShipping() != null ? request.getIsShipping() : shipmentManager.getIsShipping(),
                shipmentManager.getShipmentSeq());

        shipmentManagerRepository.save(updatedShipmentManager);

        return ShipmentManagerResponseDto.of(updatedShipmentManager);

    }

    @Transactional
    @CircuitBreaker(name = "shipment-manager-service", fallbackMethod = "fallbackForGetDto")
    public GetShipmentManagerResponseDto getShipmentManagerById(UUID shipmentManagerId, String requestUsername,
                                                                String requestRole) {

        validateGetByIdRole(requestRole);

        ShipmentManager shipmentManager = findActiveByShipmentManagerId(shipmentManagerId);
        validateHub(shipmentManager.getInHubId(), requestUsername, requestRole);

        if (requestRole.equals("SHIPMENT_MANAGER")) {
            if (!shipmentManager.getUsername().equals(requestUsername)) {
                throw new IllegalArgumentException(ShipmentManagerExceptionMessage.NOT_MY_INFO.getMessage());
            }

        }
        return GetShipmentManagerResponseDto.of(shipmentManager);
    }

    @Transactional
    @CircuitBreaker(name = "shipment-manager-service", fallbackMethod = "fallbackForPageGetDto")
    public Page<GetShipmentManagerResponseDto> getShipmentManagers(String username, String managerType, UUID hubId,
                                                                   Pageable pageable, String requestUsername,
                                                                   String requestRole) {
        validateRole(requestRole);

        if (requestRole.equals("HUB_MANAGER")) {
            if (hubId == null) {
                throw new IllegalArgumentException(ShipmentManagerExceptionMessage.REQUIRE_HUB_ID.getMessage());
            }
            validateHub(hubId, requestUsername, requestRole);
        }

        return shipmentManagerRepository.searchShipmentManagers(username, managerType, hubId, pageable);
    }

    private int getNextSequence(String managerType) {
        String sequenceName = managerType.equals("HUB_SHIPMENT") ? "shipment_seq_hub" : "shipment_seq_comp";
        // PostgreSQL에서 시퀀스를 가져오는 SQL
        String sql = "SELECT NEXTVAL('" + sequenceName + "')";
        return ((Number) entityManager.createNativeQuery(sql)
                .getSingleResult()).intValue();
    }

    // 요청 헤더의 role이 MASTER인지 검증하는 메서드
    private void validateRole(String requestRole) {

        if (!requestRole.equals("MASTER") && !requestRole.equals("HUB_MANAGER")) {
            throw new IllegalArgumentException(ShipmentCommonExceptionMessage.NOT_ALLOWED_API.getMessage());
        }

    }

    private void validateGetByIdRole(String requestRole) {

        if (!requestRole.equals("MASTER") && !requestRole.equals("HUB_MANAGER") && !requestRole.equals(
                "SHIPMENT_MANAGER")) {
            throw new IllegalArgumentException(ShipmentCommonExceptionMessage.NOT_ALLOWED_API.getMessage());
        }

    }

    private ShipmentManager findActiveByShipmentManagerId(UUID shipmentManagerId) {

        return shipmentManagerRepository.findByShipmentManagerIdAndDeletedFalse(
                        shipmentManagerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        ShipmentManagerExceptionMessage.NOT_FOUND_ACTIVE.getMessage()));
    }

    private void validateUserRoleIsShipmentManager(String username) {
        log.info("User request for username: {}", username);
        UserResponseDto userResponseDto = Optional.ofNullable(userClientService.getUser(username))
                .orElseThrow(
                        () -> new IllegalArgumentException(FeignClientExceptionMessage.USER_NOT_FOUND.getMessage()));

        if (!userResponseDto.getRole().equals("SHIPMENT_MANAGER")) {
            throw new IllegalArgumentException(FeignClientExceptionMessage.NOT_VALID_ROLE_USER.getMessage());
        }
    }

    private void validateHub(UUID hubId, String requestUsername, String requestRole) {
        log.info("Hub request for hubId: {}", hubId);

        GetHubInfoRes getHubInfoRes = Optional.ofNullable(hubClientService.getHub(hubId)).orElseThrow(
                () -> new IllegalArgumentException(FeignClientExceptionMessage.HUB_NOT_FOUND.getMessage()));

        if (requestRole.equals("HUB_MANAGER")) {
            if (!getHubInfoRes.getUsername().equals(requestUsername)) {
                throw new IllegalArgumentException(FeignClientExceptionMessage.NOT_VALID_ROLE_HUB.getMessage());
            }
        }

    }

    private void fallback(Throwable throwable) {
        if (throwable instanceof BadRequest) {
            log.warn("User 400 Bad Request 발생: {}", throwable.getMessage());
            if (throwable.getMessage().contains(FeignClientExceptionMessage.HUB_NOT_FOUND.getMessage())) {
                throw new IllegalArgumentException(FeignClientExceptionMessage.HUB_NOT_FOUND.getMessage());
            }

            if (throwable.getMessage().contains(FeignClientExceptionMessage.USER_NOT_FOUND.getMessage())) {
                throw new IllegalArgumentException(FeignClientExceptionMessage.USER_NOT_FOUND.getMessage());
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

    public ShipmentManagerResponseDto fallbackForDto(Throwable throwable) {
        fallback(throwable);
        return new ShipmentManagerResponseDto();
    }

    public GetShipmentManagerResponseDto fallbackForGetDto(Throwable throwable) {
        fallback(throwable);
        return new GetShipmentManagerResponseDto();
    }

    public Page<GetShipmentManagerResponseDto> fallbackForPageGetDto(Throwable throwable) {
        fallback(throwable);
        return new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0);
    }

}
