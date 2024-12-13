package com.sparta.shipment.model.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.sparta.shipment.exception.ShipmentManagerExceptionMessage;

public enum ManagerTypeEnum {

    HUB_SHIPMENT,    //허브배송담당자
    COMP_SHIPMENT;    //업체배송담당자

    @JsonCreator
    public static ManagerTypeEnum fromString(String status) {
        for (ManagerTypeEnum managerType : ManagerTypeEnum.values()) {
            if (managerType.name().equalsIgnoreCase(status)) {
                return managerType;
            }
        }
        throw new IllegalArgumentException(ShipmentManagerExceptionMessage.NOT_ALLOWED_ROLE.getMessage());
    }
}
