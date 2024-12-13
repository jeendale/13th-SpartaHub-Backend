package com.sparta.shipment.domain.service;

import com.sparta.shipment.domain.dto.request.CreateShipmentRequestDto;
import com.sparta.shipment.domain.dto.response.ShipmentResponseDto;
import com.sparta.shipment.exception.ShipmentCommonExceptionMessage;
import com.sparta.shipment.exception.ShipmentManagerExceptionMessage;
import com.sparta.shipment.model.entity.Shipment;
import com.sparta.shipment.model.repository.ShipmentRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    @Transactional
    public ShipmentResponseDto createShipment(CreateShipmentRequestDto request,
                                              String requestRole) {

        validateCreateRole(requestRole);

        UUID shipmentId = UUID.randomUUID();
        while (shipmentRepository.existsById(shipmentId)) {
            shipmentId = UUID.randomUUID();
        }

        Shipment shipment = Shipment.create(shipmentId, request.getOrderId(),
                request.getShipmentManagerId(),
                request.getStartHubId(),
                request.getEndHubId(),
                request.getShipmentStatus(),
                request.getShippingAddress(),
                request.getReceivername(),
                request.getReceiverSlackId());

        shipmentRepository.save(shipment);

        return ShipmentResponseDto.of(shipment);

    }

    @Transactional
    public ShipmentResponseDto deleteShipment(UUID shipmentId, String requestUsername,
                                              String requestRole) {
        validateDeleteRole(requestRole);

        Shipment shipment = findActiveByShipmentId(shipmentId);

        shipment.updateDeleted(requestUsername);

        return ShipmentResponseDto.of(shipment);
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


    private Shipment findActiveByShipmentId(UUID shipmentId) {

        return shipmentRepository.findByShipmentIdAndDeletedFalse(
                        shipmentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        ShipmentManagerExceptionMessage.NOT_FOUND_DELETE.getMessage()));
    }
}
