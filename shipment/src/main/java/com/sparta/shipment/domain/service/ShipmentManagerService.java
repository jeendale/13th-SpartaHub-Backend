package com.sparta.shipment.domain.service;

import com.sparta.shipment.domain.dto.CreateShipmentManagerRequestDto;
import com.sparta.shipment.domain.dto.ShipmentManagerResponseDto;
import com.sparta.shipment.domain.dto.UpdateShipmentManagerRequestDto;
import com.sparta.shipment.exception.ShipmentManagerExceptionMessage;
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
    public ShipmentManagerResponseDto createShipmentManager(CreateShipmentManagerRequestDto request,
                                                            String requestUsername,
                                                            String requestRole) {

        validateRequestRole(requestRole, "CREATE");

        // TODO: user와 연결해서 username값 있는지, 해당 username 권한이 SHIPMENT_MANAGER 인지 확인
        // TODO: HUB와 연결해서 hubId 있는지 확인

        UUID shipmentManagerId = UUID.randomUUID();
        while (shipmentManagerRepository.existsById(shipmentManagerId)) {
            shipmentManagerId = UUID.randomUUID();
        }

        int shipmentSeq = getNextSequence(request.getManagerType());

        try {
            ShipmentManager shipmentManager = ShipmentManager.create(shipmentManagerId, request.getUsername(),
                    request.getInHubId(), request.getManagerType(), shipmentSeq);

            shipmentManager.updateCreatedByAndLastModifiedBy(requestUsername);
            shipmentManagerRepository.save(shipmentManager);

            return ShipmentManagerResponseDto.of(shipmentManager);
        } catch (Exception e) {
            rollbackSequence(request.getManagerType(), shipmentSeq);
            throw e;  // 예외를 다시 던져 트랜잭션 롤백
        }

    }

    @Transactional
    public ShipmentManagerResponseDto deleteShipmentManager(UUID shipmentManagerId, String requestUsername,
                                                            String requestRole) {
        validateRequestRole(requestRole, "DELETE");

        ShipmentManager shipmentManager = findActiveByShipmentManagerId(shipmentManagerId);

        shipmentManager.updateDeleted(requestUsername);

        return ShipmentManagerResponseDto.of(shipmentManager);
    }

    @Transactional
    public ShipmentManagerResponseDto updateShipmentManager(UUID shipmentManagerId,
                                                            UpdateShipmentManagerRequestDto request,
                                                            String requestUsername, String requestRole) {
        validateRequestRole(requestRole, "UPDATE");

        ShipmentManager shipmentManager = findActiveByShipmentManagerId(shipmentManagerId);

        ShipmentManager updatedShipmentManager = ShipmentManager.create(shipmentManagerId,
                shipmentManager.getUsername(),
                request.getInHubId() != null ? request.getInHubId() : shipmentManager.getInHubId(),
                request.getManagerType() != null ? request.getManagerType()
                        : shipmentManager.getManagerType().toString(),
                shipmentManager.getShipmentSeq());

        shipmentManagerRepository.save(updatedShipmentManager);

        return ShipmentManagerResponseDto.of(updatedShipmentManager);

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

    // 요청 헤더의 role이 MASTER인지 검증하는 메서드
    private void validateRequestRole(String requestRole, String actionType) {

        //TODO: 허브 담당자 권한일 시 내 담당 허브인지 확인, 배송담당자 일땐 본인 정보만 확인 가능
        if (actionType.equals("CREATE") || actionType.equals("UPDATE") || actionType.equals("DELETE")) {
            if (!requestRole.equals("MASTER") && !requestRole.equals("HUB_MANAGER")) {
                throw new IllegalArgumentException(ShipmentManagerExceptionMessage.NOT_ALLOWED_API.getMessage());
            }
        } else if (actionType.equals("READ")) {
            // 'READ' 작업은 'MASTER', 'HUB_MANAGER', 'SHIPMENT_MANAGER' 모두 허용
            if (!requestRole.equals("MASTER") && !requestRole.equals("HUB_MANAGER") && !requestRole.equals(
                    "SHIPMENT_MANAGER")) {
                throw new IllegalArgumentException(ShipmentManagerExceptionMessage.NOT_ALLOWED_API.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Unknown action type: " + actionType);
        }

    }

    private ShipmentManager findActiveByShipmentManagerId(UUID shipmentManagerId) {

        return shipmentManagerRepository.findActiveByShipmentManagerId(
                        shipmentManagerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        ShipmentManagerExceptionMessage.NOT_FOUND_DELETE.getMessage()));
    }


}
