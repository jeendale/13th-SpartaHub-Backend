package com.sparta.shipment.domain.service;

import com.sparta.shipment.domain.dto.request.CreateShipmentRouteRequestDto;
import com.sparta.shipment.domain.dto.request.UpdateShipmentRouteRequestDto;
import com.sparta.shipment.domain.dto.response.ShipmentRouteResponseDto;
import com.sparta.shipment.exception.ShipmentCommonExceptionMessage;
import com.sparta.shipment.exception.ShipmentExceptionMessage;
import com.sparta.shipment.exception.ShipmentManagerExceptionMessage;
import com.sparta.shipment.exception.ShipmentRouteExceptionMessage;
import com.sparta.shipment.model.entity.Shipment;
import com.sparta.shipment.model.entity.ShipmentManager;
import com.sparta.shipment.model.entity.ShipmentRoute;
import com.sparta.shipment.model.repository.ShipmentManagerRepository;
import com.sparta.shipment.model.repository.ShipmentRepository;
import com.sparta.shipment.model.repository.ShipmentRouteRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShipmentRouteService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentManagerRepository shipmentManagerRepository;
    private final ShipmentRouteRepository shipmentRouteRepository;

    @Transactional
    public ShipmentRouteResponseDto createShipmentRoute(CreateShipmentRouteRequestDto request, String requestUsername,
                                                        String requestRole) {

        validateCreateRole(requestRole);

        UUID shipmentRouteId = UUID.randomUUID();
        while (shipmentRouteRepository.existsById(shipmentRouteId)) {
            shipmentRouteId = UUID.randomUUID();
        }

        Shipment shipment = findActiveByShipmentId(request.getShipmentId());
        ShipmentManager shipmentManager = findActiveByShipmentManagerId(request.getShipmentManagerId());

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

        shipmentRouteRepository.save(shipmentRoute);

        return ShipmentRouteResponseDto.of(shipmentRoute);

    }

    @Transactional
    public ShipmentRouteResponseDto updateShipmentRoute(UUID shipmentRouteId,
                                                        UpdateShipmentRouteRequestDto request,
                                                        String requestUsername, String requestRole) {
        validateRURole(requestRole);

        ShipmentRoute shipmentRoute = findActiveByShipmentRouteId(shipmentRouteId);

        ShipmentRoute updatedShipmentRoute = ShipmentRoute.create(
                shipmentRoute.getShipmentRouteId(),
                shipmentRoute.getShipment(),
                shipmentRoute.getShipmentManager(),
                shipmentRoute.getRouteSeq(),
                shipmentRoute.getStartHubId(),
                shipmentRoute.getEndHubId(),
                shipmentRoute.getExpectedDistance(),
                shipmentRoute.getExpectedTime(),
                request.getRealDistance() != null ? request.getRealDistance() : shipmentRoute.getRealDistance(),
                request.getRealTime() != null ? request.getRealTime() : shipmentRoute.getRealTime(),
                request.getShipmentStatus() != null ? request.getShipmentStatus()
                        : shipmentRoute.getShipmentStatus().toString()

        );

        shipmentRouteRepository.save(updatedShipmentRoute);

        return ShipmentRouteResponseDto.of(updatedShipmentRoute);
    }

    @Transactional
    public ShipmentRouteResponseDto deleteShipmentRoute(UUID shipmentRouteId, String requestUsername,
                                                        String requestRole) {
        validateDeleteRole(requestRole);

        ShipmentRoute shipmentRoute = findActiveByShipmentRouteId(shipmentRouteId);

        shipmentRoute.updateDeleted(requestUsername);

        return ShipmentRouteResponseDto.of(shipmentRoute);
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

    private ShipmentManager findActiveByShipmentManagerId(UUID shipmentManagerId) {

        return shipmentManagerRepository.findByShipmentManagerIdAndDeletedFalse(shipmentManagerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        ShipmentManagerExceptionMessage.NOT_FOUND_ACTIVE.getMessage()));

    }
}
