package com.sparta.shipment.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_shipment_route",
        uniqueConstraints = @UniqueConstraint(columnNames = {"shipment_id", "route_seq"}))
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@Getter
public class ShipmentRoute extends Audit {

    /**
     * PK - 배송 경로 아이디 (UUID)
     */
    @Id
    @Column(name = "shipment_route_id", updatable = false, nullable = false)
    private UUID shipmentRouteId;

    /**
     * FK - 배송 아이디 (UUID)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment; // Shipment 엔티티와 연결

    /**
     * FK - 배송 관리자 아이디 (UUID)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_manager_id", nullable = false)
    private ShipmentManager shipmentManager; // ShipmentManager 엔티티와 연결

    /**
     * 경로 순서 (INT)
     */
    @Column(name = "route_seq", nullable = false)
    private int routeSeq;

    /**
     * FK - 배송 출발 허브 아이디 (UUID)
     */
    @Column(name = "start_hub_id", nullable = false)
    private UUID startHubId;

    /**
     * FK - 배송 도착 허브 아이디 (UUID)
     */
    @Column(name = "end_hub_id", nullable = false)
    private UUID endHubId;

    /**
     * 예상 남은 거리 (DECIMAL(5,2))
     */
    @Column(name = "expected_distance", nullable = false, precision = 5, scale = 2)
    private BigDecimal expectedDistance;

    /**
     * 예상 남은 시간 (TIME)
     */
    @Column(name = "expected_time", nullable = false, precision = 4, scale = 2)
    private BigDecimal expectedTime;

    /**
     * 실제 이동한 거리 (DECIMAL(5,2))
     */
    @Column(name = "real_distance", precision = 5, scale = 2)
    private BigDecimal realDistance;

    /**
     * 실제 소요된 시간 (DECIMAL(4,2))
     */
    @Column(name = "real_time", precision = 4, scale = 2)
    private BigDecimal realTime;

    /**
     * 배송 상태 (ENUM)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "shipment_status", nullable = false)
    private ShipmentStatusEnum shipmentStatus;

    public static ShipmentRoute create(UUID shipmentRouteId, Shipment shipment, ShipmentManager shipmentManager,
                                       int routeSeq,
                                       UUID startHubId, UUID endHubId, BigDecimal expectedDistance,
                                       BigDecimal expectedTime, BigDecimal realDistance,
                                       BigDecimal realTime, String shipmentStatus) {
        return ShipmentRoute.builder()
                .shipmentRouteId(shipmentRouteId)
                .shipment(shipment)
                .shipmentManager(shipmentManager)
                .routeSeq(routeSeq)
                .startHubId(startHubId)
                .endHubId(endHubId)
                .expectedDistance(expectedDistance)
                .expectedTime(expectedTime)
                .realDistance(realDistance)
                .realTime(realTime)
                .shipmentStatus(ShipmentStatusEnum.fromString(shipmentStatus))
                .build();
    }
}
