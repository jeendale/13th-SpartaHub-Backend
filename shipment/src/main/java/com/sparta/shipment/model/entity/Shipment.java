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

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

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
    @Column(name = "receiver _name", nullable = false)
    private String receiverName;

    /**
     * 받는 사람 슬랙 아이디 Slack 알림 전송을 위해 사용합니다.
     */
    @Column(name = "receiver_slack_id", nullable = false)
    private String receiverSlackId;


}
