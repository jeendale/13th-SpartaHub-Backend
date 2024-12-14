package com.sparta.shipment.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_shipment_manager")
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@Getter
public class ShipmentManager extends Audit {

    @Id
    @Column(name = "shipment_manager_id", updatable = false, nullable = false)
    private UUID shipmentManagerId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "in_hub_id", nullable = false)
    private UUID inHubId;

    @Enumerated(EnumType.STRING)  // Enum 타입을 문자열로 저장
    @Column(name = "manager_type", nullable = false)
    private ManagerTypeEnum managerType;

    @Column(name = "is_shipping", nullable = false)
    private Boolean isShipping;

    @Column(name = "shipment_seq", nullable = false)
    private int shipmentSeq;


    public static ShipmentManager create(UUID shipmentManagerId, String username, UUID inHubId, String managerType,
                                         Boolean isShipping, int shipmentSeq) {
        return ShipmentManager.builder()
                .shipmentManagerId(shipmentManagerId)
                .username(username)
                .inHubId(inHubId)
                .managerType(ManagerTypeEnum.fromString(managerType))
                .isShipping(isShipping)
                .shipmentSeq(shipmentSeq)
                .build();
    }

    public void changeShippingStatus(Boolean isShipping) {
        this.isShipping = isShipping;
    }
}
