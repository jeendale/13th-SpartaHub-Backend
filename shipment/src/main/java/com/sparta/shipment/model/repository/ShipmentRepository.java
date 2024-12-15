package com.sparta.shipment.model.repository;

import com.sparta.shipment.model.entity.Shipment;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, UUID>, ShipmentRepositoryCustom {


    Optional<Shipment> findByShipmentIdAndDeletedFalse(UUID shipmentId);
}
