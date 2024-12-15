package com.sparta.shipment.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "p_shipment")
public class Shipment extends Audit {


    @Id
    @Column(name = "shipment_id", updatable = false, nullable = false)
    private UUID shipmentId;

    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy loading to improve performance
    @JoinColumn(name = "shipment_manager_id", referencedColumnName = "shipment_manager_id", nullable = false, foreignKey = @ForeignKey(name = "fk_shipment_shipment_manager"))
    private ShipmentManager shipmentManager;

    @Column(name = "start_hub_id", nullable = false)
    private UUID startHubId;

    @Column(name = "end_hub_id", nullable = false)
    private UUID endHubId;

    /**
     * 배송 상태 (ENUM) 'PENDING_HUB_MOVE', 'HUB_MOVING', 'DESTINATION_HUB_ARRIVED', 'SHIPPING', 'COMPLETED'
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "shipment_status", nullable = false)
    private ShipmentStatusEnum shipmentStatus;

    /**
     * 배송 도착 주소 255자로 제한된 도착 주소
     */
    @Column(name = "shipping_address", nullable = false)
    private String shippingAddress;

    /**
     * 받는 사람 이름 이름은 UUID로 관리되지 않으므로 VARCHAR(255)로 지정합니다.
     */
    @Column(name = "receiver_name", nullable = false)
    private String receiverName;

    /**
     * 받는 사람 슬랙 아이디 Slack 알림 전송을 위해 사용합니다.
     */
    @Column(name = "receiver_slack_id", nullable = false)
    private String receiverSlackId;

    @OneToOne(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private ShipmentRoute shipmentRoute; // Shipment와 ShipmentRoute는 1:1 관계

    public static Shipment create(UUID shipmentId, UUID orderId, ShipmentManager shipmentManager, UUID startHubId,
                                  UUID endHubId,
                                  String shipmentStatus, String shippingAddress,
                                  String receiverName, String receiverSlackId) {
        return Shipment.builder()
                .shipmentId(shipmentId)
                .orderId(orderId)
                .shipmentManager(shipmentManager)
                .startHubId(startHubId)
                .endHubId(endHubId)
                .shipmentStatus(ShipmentStatusEnum.fromString(shipmentStatus))
                .shippingAddress(shippingAddress)
                .receiverName(receiverName)
                .receiverSlackId(receiverSlackId)
                .build();
    }

    public void updateShipmentStatus(ShipmentStatusEnum status) {
        this.shipmentStatus = status;
    }

    public void updateReceiverSlackId(String receiverSlackId) {
        this.receiverSlackId = receiverSlackId;
    }


}
