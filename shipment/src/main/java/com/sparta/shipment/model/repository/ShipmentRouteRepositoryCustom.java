package com.sparta.shipment.model.repository;

import com.sparta.shipment.domain.dto.response.GetShipmentRouteResponseDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShipmentRouteRepositoryCustom {

    Page<GetShipmentRouteResponseDto> searchShipmentRoutes(String shipmentStatus, UUID hubId, UUID shipmentManagerId,
                                                           Pageable pageable);
}
