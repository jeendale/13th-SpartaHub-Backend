package com.sparta.shipment.domain.service;

import com.sparta.shipment.domain.dto.CreateShipmentManagerRequestDto;
import com.sparta.shipment.domain.dto.ShipmentManagerResponseDto;
import com.sparta.shipment.model.entity.ManagerTypeEnum;
import com.sparta.shipment.model.entity.ShipmentManager;
import com.sparta.shipment.model.repository.ShipmentManagerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShipmentManagerService {

    @PersistenceContext
    private final EntityManager entityManager;

    private final ShipmentManagerRepository shipmentManagerRepository;

    @Transactional
    public ShipmentManagerResponseDto createShipmentManager(CreateShipmentManagerRequestDto request) {

        ManagerTypeEnum.fromString(request.getManagerType());
        // TODO: user와 연결해서 username값 있는지, 해당 username 권한이 SHIPMENT_MANAGER 인지 확인
        // TODO: HUB와 연결해서 hubId 있는지 확인, 허브 담당자 권한일 시 내 담당 허브인지 확인

        UUID shipmentManagerId = UUID.randomUUID();
        while (shipmentManagerRepository.existsById(shipmentManagerId)) {
            shipmentManagerId = UUID.randomUUID();
        }

        int shipmentSeq = getNextSequence(request.getManagerType());

        try {
            ShipmentManager shipmentManager = ShipmentManager.create(shipmentManagerId, request.getUsername(),
                    request.getInHubId(), request.getManagerType(), shipmentSeq);

            shipmentManagerRepository.save(shipmentManager);

            return ShipmentManagerResponseDto.of(shipmentManager);
        } catch (Exception e) {
            rollbackSequence(request.getManagerType(), shipmentSeq);
            throw e;  // 예외를 다시 던져 트랜잭션 롤백
        }

    }

    private int getNextSequence(String managerType) {

        String sequenceName = managerType.equals("HUB_SHIPMENT") ? "shipment_seq_hub" : "shipment_seq_comp";
        // PostgreSQL에서 시퀀스를 가져오는 SQL
        String sql = "SELECT NEXTVAL(:sequenceName)";
        return ((Number) entityManager.createNativeQuery(sql)
                .setParameter("sequenceName", sequenceName)
                .getSingleResult()).intValue();
    }

    // 시퀀스 롤백
    private void rollbackSequence(String managerType, int shipmentSeq) {
        String sequenceName = managerType.equals("HUB_SHIPMENT") ? "shipment_seq_hub" : "shipment_seq_comp";
        String sql = "SELECT setval(:sequenceName, :initialSeq)";
        entityManager.createNativeQuery(sql)
                .setParameter("sequenceName", sequenceName)
                .setParameter("initialSeq", shipmentSeq)
                .executeUpdate();
    }

}
