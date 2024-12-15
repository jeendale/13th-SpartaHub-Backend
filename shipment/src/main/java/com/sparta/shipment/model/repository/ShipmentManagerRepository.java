package com.sparta.shipment.model.repository;

import com.sparta.shipment.model.entity.ManagerTypeEnum;
import com.sparta.shipment.model.entity.ShipmentManager;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentManagerRepository extends JpaRepository<ShipmentManager, UUID>,
        ShipmentManagerRepositoryCustom {

    Optional<ShipmentManager> findByShipmentManagerIdAndDeletedFalse(UUID shipmentManagerId);

    Optional<ShipmentManager> findFirstByInHubIdAndIsShippingFalseAndManagerTypeAndDeletedFalseOrderByShipmentSeqAsc(
            UUID inHubId,
            ManagerTypeEnum managerType);


}
