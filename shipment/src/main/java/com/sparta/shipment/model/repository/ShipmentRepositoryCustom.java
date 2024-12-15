package com.sparta.shipment.model.repository;

import com.sparta.shipment.domain.dto.response.GetShipmentResponseDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShipmentRepositoryCustom {

    Page<GetShipmentResponseDto> searchShipments(String shipmentStatus, String receiverName,
                                                 String shippingAddress, UUID hubId, UUID shipmentManagerId,
                                                 Pageable pageable);
}
