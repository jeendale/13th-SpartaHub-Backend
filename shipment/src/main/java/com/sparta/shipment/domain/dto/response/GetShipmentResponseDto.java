package com.sparta.shipment.domain.dto.response;

import com.sparta.shipment.model.entity.Shipment;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class GetShipmentResponseDto {
    private UUID shipmentId;
    private UUID orderId;
    private UUID shipmentManagerId;
    private String shipmentStatus;
    private UUID startHubId;
    private UUID endHubId;
    private String shippingAddress;
    private String receiverName;
    private String receiverSlackId;
    //private GetShipmentRouteResponseDto shipmentRoute;

    public static GetShipmentResponseDto of(Shipment shipment) {
        return GetShipmentResponseDto.builder()
                .shipmentId(shipment.getShipmentId())
                .orderId(shipment.getOrderId())
                .shipmentManagerId(shipment.getShipmentManager().getShipmentManagerId())
                .shipmentStatus(shipment.getShipmentStatus().toString())
                .startHubId(shipment.getStartHubId())
                .endHubId(shipment.getEndHubId())
                .shippingAddress(shipment.getShippingAddress())
                .receiverName(shipment.getReceiverName())
                .receiverSlackId(shipment.getReceiverSlackId())
                //.shipmentRoute(GetShipmentRouteResponseDto.of(shipment.getShipmentRoute()))
                .build();
    }

}
