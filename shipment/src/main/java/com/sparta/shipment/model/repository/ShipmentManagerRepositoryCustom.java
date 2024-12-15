package com.sparta.shipment.model.repository;

import com.sparta.shipment.domain.dto.response.GetShipmentManagerResponseDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShipmentManagerRepositoryCustom {

    Page<GetShipmentManagerResponseDto> searchShipmentManagers(String username, String managerType, UUID hubId,
                                                               Pageable pageable);
}
