package com.sparta.shipment.model.entity;

public enum ShipmentStatusEnum {

    PENDING_HUB_MOVE,  // 허브 이동 대기중
    HUB_MOVING,        // 허브 이동중
    DESTINATION_HUB_ARRIVED, // 목적지 허브 도착
    SHIPPING,          // 배송중
    COMPLETED;          // 배송완료

    public static ShipmentStatusEnum fromString(String status) {
        for (ShipmentStatusEnum shipmentStatus : ShipmentStatusEnum.values()) {
            if (shipmentStatus.name().equalsIgnoreCase(status)) {
                return shipmentStatus;
            }
        }
        throw new IllegalArgumentException("Invalid ShipmentStatus: " + status);
    }
}
