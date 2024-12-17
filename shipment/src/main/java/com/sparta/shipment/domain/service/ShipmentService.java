package com.sparta.shipment.domain.service;

import com.sparta.shipment.domain.dto.request.CreateShipmentRequestDto;
import com.sparta.shipment.domain.dto.request.CreateShipmentRouteRequestDto;
import com.sparta.shipment.domain.dto.request.UpdateShipmentRequestDto;
import com.sparta.shipment.domain.dto.response.GetShipmentResponseDto;
import com.sparta.shipment.domain.dto.response.ShipmentResponseDto;
import com.sparta.shipment.exception.FeignClientExceptionMessage;
import com.sparta.shipment.exception.ServiceNotAvailableException;
import com.sparta.shipment.exception.ShipmentCommonExceptionMessage;
import com.sparta.shipment.exception.ShipmentExceptionMessage;
import com.sparta.shipment.exception.ShipmentManagerExceptionMessage;
import com.sparta.shipment.infrastructure.dto.GetHubInfoRes;
import com.sparta.shipment.infrastructure.dto.GetHubRouteInfoRes;
import com.sparta.shipment.model.entity.ManagerTypeEnum;
import com.sparta.shipment.model.entity.Shipment;
import com.sparta.shipment.model.entity.ShipmentManager;
import com.sparta.shipment.model.entity.ShipmentStatusEnum;
import com.sparta.shipment.model.repository.ShipmentManagerRepository;
import com.sparta.shipment.model.repository.ShipmentRepository;
import feign.FeignException.BadRequest;
import feign.FeignException.ServiceUnavailable;
import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentManagerRepository shipmentManagerRepository;
    private final HubClientService hubClientService;
    private final ShipmentRouteService shipmentRouteService;

    @Transactional
    @CircuitBreaker(name = "shipment-service", fallbackMethod = "fallbackForDto")
    public ShipmentResponseDto createShipment(CreateShipmentRequestDto request, String requestRole) {

        validateCreateRole(requestRole);

        ShipmentManager shipmentManager = findActiveByShipmentManagerId(request.getEndHubId());

        UUID shipmentId = UUID.randomUUID();
        while (shipmentRepository.existsById(shipmentId)) {
            shipmentId = UUID.randomUUID();
        }
        Shipment shipment = Shipment.create(shipmentId, request.getOrderId(),
                shipmentManager,
                request.getStartHubId(),
                request.getEndHubId(),
                "PENDING_HUB_MOVE",
                request.getShippingAddress(),
                request.getReceiverName(),
                request.getReceiverSlackId());

        shipmentRepository.save(shipment);

        Page<GetHubRouteInfoRes> infoRes = hubClientService.getHubRoutesByHubIds(
                request.getStartHubId(), request.getEndHubId(), Pageable.unpaged());

        List<GetHubRouteInfoRes> info = infoRes.getContent();

        // 리스트가 비어있지 않으면 첫 번째 값을 가져오기
        GetHubRouteInfoRes getHubRouteInfoRes = info.stream()
                .findFirst() // 비어있는 경우 Optional.empty()를 반환
                .orElseThrow(
                        () -> new NoSuchElementException(FeignClientExceptionMessage.HUB_ROUTE_NOT_FOUND.getMessage()));

        CreateShipmentRouteRequestDto requestDto = new CreateShipmentRouteRequestDto(shipmentId, 1,
                getHubRouteInfoRes.getStartHubId(), getHubRouteInfoRes.getEndHubId(), getHubRouteInfoRes.getDistance(),
                getHubRouteInfoRes.getDeliveryTime(), null, null,
                "PENDING_HUB_MOVE");
        shipmentRouteService.createShipmentRoute(requestDto, requestRole);

        return ShipmentResponseDto.of(shipment);

    }

    @Transactional
    @CircuitBreaker(name = "shipment-service", fallbackMethod = "fallbackForDto")
    public ShipmentResponseDto updateShipment(UUID shipmentId,
                                              UpdateShipmentRequestDto request,
                                              String requestUsername, String requestRole) {
        validateRURole(requestRole);

        Shipment shipment = findActiveByShipmentId(shipmentId);

        validateHub(shipment.getStartHubId(), requestUsername, requestRole);

        if (requestRole.equals("SHIPMENT_MANAGER")) {
            if (!shipment.getShipmentManager().getUsername().equals(requestUsername)) {
                throw new IllegalArgumentException(ShipmentExceptionMessage.NOT_MY_INFO.getMessage());
            }

        }

        if (request.getShipmentStatus() != null) {
            shipment.updateShipmentStatus(ShipmentStatusEnum.valueOf(request.getShipmentStatus()));
        }
        if (request.getReceiverSlackId() != null) {
            shipment.updateReceiverSlackId(request.getReceiverSlackId());
        }

        if (request.getShipmentStatus() != null && request.getShipmentStatus().equals("COMPLETED")) {
            shipment.getShipmentManager().changeShippingStatus(false);
            shipment.getShipmentRoute().updateShipmentStatus(ShipmentStatusEnum.COMPLETED);
        }

        shipmentRepository.save(shipment);

        return ShipmentResponseDto.of(shipment);
    }

    @Transactional
    @CircuitBreaker(name = "shipment-service", fallbackMethod = "fallbackForDto")
    public ShipmentResponseDto deleteShipment(UUID shipmentId, String requestUsername,
                                              String requestRole) {
        validateDeleteRole(requestRole);

        Shipment shipment = findActiveByShipmentId(shipmentId);
        validateHub(shipment.getStartHubId(), requestUsername, requestRole);

        shipment.updateDeleted(requestUsername);

        return ShipmentResponseDto.of(shipment);
    }

    @Transactional
    @CircuitBreaker(name = "shipment-service", fallbackMethod = "fallbackForGetDto")
    public GetShipmentResponseDto getShipmentById(UUID shipmentId, String requestUsername,
                                                  String requestRole) {

        validateRURole(requestRole);

        Shipment shipment = findActiveByShipmentId(shipmentId);

        if (requestRole.equals("SHIPMENT_MANAGER")) {
            if (!shipment.getShipmentManager().getUsername().equals(requestUsername)) {
                throw new IllegalArgumentException(ShipmentExceptionMessage.NOT_MY_INFO.getMessage());
            }

        }
        return GetShipmentResponseDto.of(shipment);
    }

    @Transactional
    @CircuitBreaker(name = "shipment-service", fallbackMethod = "fallbackForPageGetDto")
    public Page<GetShipmentResponseDto> getShipments(String shipmentStatus, String receiverName,
                                                     String shippingAddress, UUID hubId, UUID shipmentManagerId,
                                                     Pageable pageable, String requestUsername,
                                                     String requestRole) {
        validateRURole(requestRole);
        //TODO : 담당허브만 조회되도록 구현

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

        return shipmentRepository.searchShipments(shipmentStatus, receiverName, shippingAddress,
                hubId, shipmentManagerId, pageable);
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

    private Shipment findActiveByShipmentId(UUID shipmentId) {

        return shipmentRepository.findByShipmentIdAndDeletedFalse(
                        shipmentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        ShipmentExceptionMessage.NOT_FOUND_ACTIVE.getMessage()));
    }

    private ShipmentManager findActiveByShipmentManagerId(UUID inHubId) {

        return shipmentManagerRepository.findFirstByInHubIdAndIsShippingFalseAndManagerTypeAndDeletedFalseOrderByShipmentSeqAsc(
                        inHubId, ManagerTypeEnum.COMP_SHIPMENT)
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

    public ShipmentResponseDto fallbackForDto(Throwable throwable) {
        fallback(throwable);
        return new ShipmentResponseDto();
    }

    public GetShipmentResponseDto fallbackForGetDto(Throwable throwable) {
        fallback(throwable);
        return new GetShipmentResponseDto();
    }

    public Page<GetShipmentResponseDto> fallbackForPageGetDto(Throwable throwable) {
        fallback(throwable);
        return new PageImpl<>(new ArrayList<>(), Pageable.unpaged(), 0);
    }


}
