package com.sparta.shipment.model.repository;

import com.sparta.shipment.model.entity.Shipment;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShipmentRepository extends JpaRepository<Shipment, UUID>, ShipmentRepositoryCustom {

    @EntityGraph(attributePaths = "shipmentRoute")
    @Query("SELECT s FROM Shipment s LEFT JOIN s.shipmentRoute sr WHERE s.shipmentId = :shipmentId AND s.deleted = false AND sr.deleted = false")
    Optional<Shipment> findByShipmentIdAndDeletedFalse(UUID shipmentId);
}
