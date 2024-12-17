package com.sparta.shipment.domain.service;

import com.sparta.shipment.domain.dto.request.CreateShipmentRouteRequestDto;
import com.sparta.shipment.domain.dto.request.UpdateShipmentRouteRequestDto;
import com.sparta.shipment.domain.dto.response.GetShipmentRouteResponseDto;
import com.sparta.shipment.domain.dto.response.ShipmentRouteResponseDto;
import com.sparta.shipment.exception.FeignClientExceptionMessage;
import com.sparta.shipment.exception.ServiceNotAvailableException;
import com.sparta.shipment.exception.ShipmentCommonExceptionMessage;
import com.sparta.shipment.exception.ShipmentExceptionMessage;
import com.sparta.shipment.exception.ShipmentManagerExceptionMessage;
import com.sparta.shipment.exception.ShipmentRouteExceptionMessage;
import com.sparta.shipment.infrastructure.dto.GetHubInfoRes;
import com.sparta.shipment.model.entity.ManagerTypeEnum;
import com.sparta.shipment.model.entity.Shipment;
import com.sparta.shipment.model.entity.ShipmentManager;
import com.sparta.shipment.model.entity.ShipmentRoute;
import com.sparta.shipment.model.entity.ShipmentStatusEnum;
import com.sparta.shipment.model.repository.ShipmentManagerRepository;
import com.sparta.shipment.model.repository.ShipmentRepository;
import com.sparta.shipment.model.repository.ShipmentRouteRepository;
import feign.FeignException.BadRequest;
import feign.FeignException.ServiceUnavailable;
import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
public class ShipmentRouteService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentManagerRepository shipmentManagerRepository;
    private final ShipmentRouteRepository shipmentRouteRepository;

    private final HubClientService hubClientService;

    @Transactional
    @CircuitBreaker(name = "shipment-route-service", fallbackMethod = "fallbackForDto")
    public ShipmentRouteResponseDto createShipmentRoute(CreateShipmentRouteRequestDto request, String requestRole) {

        validateCreateRole(requestRole);

        ShipmentManager shipmentManager = findActiveByShipmentManagerId(request.getStartHubId());
        shipmentManager.changeShippingStatus(true);

        UUID shipmentRouteId = UUID.randomUUID();
        while (shipmentRouteRepository.existsById(shipmentRouteId)) {
            shipmentRouteId = UUID.randomUUID();
        }

        Shipment shipment = findActiveByShipmentId(request.getShipmentId());

        ShipmentRoute shipmentRoute = ShipmentRoute.create(
                shipmentRouteId,
                shipment,
                shipmentManager,
                request.getRouteSeq(),
                request.getStartHubId(),
                request.getEndHubId(),
                request.getExpectedDistance(),
                request.getExpectedTime(),
                request.getRealDistance(),
                request.getRealTime(),
                request.getShipmentStatus()
        );

        shipmentManager.changeShippingStatus(true);

        shipmentRouteRepository.save(shipmentRoute);

        return ShipmentRouteResponseDto.of(shipmentRoute);

    }

    @Transactional
    @CircuitBreaker(name = "shipment-route-service", fallbackMethod = "fallbackForDto")
    public ShipmentRouteResponseDto updateShipmentRoute(UUID shipmentRouteId,
                                                        UpdateShipmentRouteRequestDto request,
                                                        String requestUsername, String requestRole) {
        validateRURole(requestRole);

        ShipmentRoute shipmentRoute = findActiveByShipmentRouteId(shipmentRouteId);
        validateHub(shipmentRoute.getStartHubId(), requestUsername, requestRole);

        if (requestRole.equals("SHIPMENT_MANAGER")) {
            if (!shipmentRoute.getShipmentManager().getUsername().equals(requestUsername)) {
                throw new IllegalArgumentException(ShipmentRouteExceptionMessage.NOT_MY_INFO.getMessage());
            }
        }

        if (request.getShipmentStatus() != null) {
            shipmentRoute.updateShipmentStatus(ShipmentStatusEnum.valueOf(request.getShipmentStatus()));
        }

        if (request.getShipmentStatus() != null && request.getShipmentStatus().equals("DESTINATION_HUB_ARRIVED")) {

            if (request.getRealDistance() == null || request.getRealTime() == null) {
                throw new IllegalArgumentException(ShipmentRouteExceptionMessage.REQUIRED_BOTH_INFO.getMessage());
            }
            shipmentRoute.updateRealInfo(request.getRealDistance(), request.getRealTime());

            shipmentRoute.getShipmentManager().changeInHub(shipmentRoute.getEndHubId());
            shipmentRoute.getShipmentManager().changeShippingStatus(false);
            shipmentRoute.getShipment().updateShipmentStatus(ShipmentStatusEnum.SHIPPING);
            shipmentRoute.getShipment().getShipmentManager().changeShippingStatus(true);

        }

        shipmentRouteRepository.save(shipmentRoute);

        return ShipmentRouteResponseDto.of(shipmentRoute);
    }

    @Transactional
    @CircuitBreaker(name = "shipment-route-service", fallbackMethod = "fallbackForDto")
    public ShipmentRouteResponseDto deleteShipmentRoute(UUID shipmentRouteId, String requestUsername,
                                                        String requestRole) {
        validateDeleteRole(requestRole);

        ShipmentRoute shipmentRoute = findActiveByShipmentRouteId(shipmentRouteId);

        validateHub(shipmentRoute.getStartHubId(), requestUsername, requestRole);

        shipmentRoute.updateDeleted(requestUsername);

        return ShipmentRouteResponseDto.of(shipmentRoute);
    }

    @Transactional
    @CircuitBreaker(name = "shipment-route-service", fallbackMethod = "fallbackForGetDto")
    public GetShipmentRouteResponseDto getShipmentRouteById(UUID shipmentRouteId, String requestUsername,
                                                            String requestRole) {

        validateRURole(requestRole);

        ShipmentRoute shipmentRoute = findActiveByShipmentRouteId(shipmentRouteId);

        validateHub(shipmentRoute.getStartHubId(), requestUsername, requestRole);

        if (requestRole.equals("SHIPMENT_MANAGER")) {
            if (!shipmentRoute.getShipmentManager().getUsername().equals(requestUsername)) {
                throw new IllegalArgumentException(ShipmentRouteExceptionMessage.NOT_MY_INFO.getMessage());
            }

        }
        return GetShipmentRouteResponseDto.of(shipmentRoute);
    }

    @Transactional
    @CircuitBreaker(name = "shipment-route-service", fallbackMethod = "fallbackForPageGetDto")
    public Page<GetShipmentRouteResponseDto> getShipmentRoutes(String shipmentStatus, UUID hubId,
                                                               UUID shipmentManagerId,
                                                               Pageable pageable, String requestUsername,
                                                               String requestRole) {
        validateRURole(requestRole);
        //TODO : 배송담당자와 허브관리자 권한 체크

        if (requestRole.equals("SHIPMENT_MANAGER")) {
            if (shipmentManagerId == null) {
                throw new IllegalArgumentException(ShipmentManagerExceptionMessage.REQUIRE_MANAGER_ID.getMessage());
            }
            ShipmentManager shipmentManager = shipmentManagerRepository
                    .findByShipmentManagerIdAndDeletedFalse(shipmentManagerId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            ShipmentManagerExceptionMessage.NOT_FOUND_ACTIVE.getMessage()));

            if (!shipmentManager.getUsername().equals(requestUsername)) {
                throw new IllegalArgumentException(ShipmentManagerExceptionMessage.NOT_MY_INFO.getMessage());
            }
        }

        if (requestRole.equals("HUB_MANAGER")) {
            if (hubId == null) {
                throw new IllegalArgumentException(ShipmentManagerExceptionMessage.REQUIRE_HUB_ID.getMessage());
            }
            validateHub(hubId, requestUsername, requestRole);
        }
        return shipmentRouteRepository.searchShipmentRoutes(shipmentStatus, hubId, shipmentManagerId, pageable);

    }

    // create 요청이 가능한 권한인지 검증하는 메서드
    private void validateCreateRole(String requestRole) {

        if (!requestRole.equals("MASTER")) {
            throw new IllegalArgumentException(ShipmentCommonExceptionMessage.NOT_ALLOWED_API.getMessage());
        }

    }

    // delete 요청이 가능한 권한인지 검증하는 메서드
    private void validateDeleteRole(String requestRole) {

        if (!requestRole.equals("MASTER") && !requestRole.equals("HUB_MANAGER")) {
            throw new IllegalArgumentException(ShipmentCommonExceptionMessage.NOT_ALLOWED_API.getMessage());
        }

    }

    // update와 read 요청이 가능한 권한인지 검증하는 메서드
    private void validateRURole(String requestRole) {

        if (!requestRole.equals("MASTER") && !requestRole.equals("HUB_MANAGER") && !requestRole.equals(
                "SHIPMENT_MANAGER")) {
            throw new IllegalArgumentException(ShipmentCommonExceptionMessage.NOT_ALLOWED_API.getMessage());
        }

    }

    private ShipmentRoute findActiveByShipmentRouteId(UUID shipmentRouteId) {

        return shipmentRouteRepository.findByShipmentRouteIdAndDeletedFalse(
                        shipmentRouteId)
                .orElseThrow(() -> new IllegalArgumentException(
                        ShipmentRouteExceptionMessage.NOT_FOUND_ACTIVE.getMessage()));
    }

    private Shipment findActiveByShipmentId(UUID shipmentId) {

        return shipmentRepository.findByShipmentIdAndDeletedFalse(
                        shipmentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        ShipmentExceptionMessage.NOT_FOUND_ACTIVE.getMessage()));
    }

    private ShipmentManager findActiveByShipmentManagerId(UUID inHubId) {

        return shipmentManagerRepository.findFirstByInHubIdAndIsShippingFalseAndManagerTypeAndDeletedFalseOrderByShipmentSeqAsc(
                        inHubId, ManagerTypeEnum.HUB_SHIPMENT)
                .orElseThrow(() -> new IllegalArgumentException(
                        ShipmentManagerExceptionMessage.NOT_FOUND_ACTIVE.getMessage()));
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

    public ShipmentRouteResponseDto fallbackForDto(Throwable throwable) {
        fallback(throwable);
        return new ShipmentRouteResponseDto();
    }

    public GetShipmentRouteResponseDto fallbackForGetDto(Throwable throwable) {
        fallback(throwable);
        return new GetShipmentRouteResponseDto();
    }

    public Page<GetShipmentRouteResponseDto> fallbackForPageGetDto(Throwable throwable) {
        fallback(throwable);
        return new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0);
    }
}
