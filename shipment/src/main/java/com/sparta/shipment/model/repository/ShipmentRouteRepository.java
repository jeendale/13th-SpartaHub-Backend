package com.sparta.shipment.model.repository;

import com.sparta.shipment.model.entity.ShipmentRoute;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRouteRepository extends JpaRepository<ShipmentRoute, UUID> {

    Optional<ShipmentRoute> findByShipmentRouteIdAndDeletedFalse(UUID shipmentRouteId);
}
