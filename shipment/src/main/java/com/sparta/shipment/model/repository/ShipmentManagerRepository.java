package com.sparta.shipment.model.repository;

import com.sparta.shipment.model.entity.ShipmentManager;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShipmentManagerRepository extends JpaRepository<ShipmentManager, UUID> {

    @Query("SELECT sm FROM ShipmentManager sm " +
            "WHERE sm.shipmentManagerId = :shipmentManagerId " +
            "AND (sm.deleted IS NULL OR sm.deleted = false)")
    Optional<ShipmentManager> findActiveByShipmentManagerId(@Param("shipmentManagerId") UUID shipmentManagerId);
}
