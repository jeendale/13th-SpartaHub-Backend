package com.sparta.order.infrastructure;

import com.sparta.order.domain.service.ShipmentClientService;
import com.sparta.order.infrastructure.dto.CreateShipmentManagerRequestDto;
import com.sparta.order.infrastructure.dto.CreateShipmentRequestDto;
import com.sparta.order.infrastructure.dto.CreateShipmentRouteRequestDto;
import com.sparta.order.infrastructure.dto.GetShipmentManagerResponseDto;
import com.sparta.order.infrastructure.dto.GetShipmentResponseDto;
import com.sparta.order.infrastructure.dto.ShipmentManagerResponseDto;
import com.sparta.order.infrastructure.dto.ShipmentResponseDto;
import com.sparta.order.infrastructure.dto.ShipmentRouteResponseDto;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="shipment-service")
public interface ShipmentClient extends ShipmentClientService {

  @PostMapping(value = "/api/v1/shipments" , headers = {"X-User-Username=\"\"", "X-User-Role=MASTER"})
  ResponseEntity<ShipmentResponseDto> createShipment(@RequestBody CreateShipmentRequestDto request);

  @PostMapping(value = "/api/v1/shipment-managers",headers = {"X-User-Username=\"\"", "X-User-Role=MASTER"} )
  ResponseEntity<ShipmentManagerResponseDto> createShipmentManager(@RequestBody CreateShipmentManagerRequestDto request);

  @PostMapping(value = "/api/v1/shipment-routes",headers = {"X-User-Username=\"\"", "X-User-Role=MASTER"} )
  ResponseEntity<ShipmentRouteResponseDto> createShipmentRoute(@RequestBody CreateShipmentRouteRequestDto request);

  @GetMapping(value = "/api/v1/shipments/{shipmentId}" , headers = {"X-User-Username=\"\"", "X-User-Role=MASTER"})
  ResponseEntity<GetShipmentResponseDto> getShipmentById(@PathVariable UUID shipmentId);

  @GetMapping(value = "/api/v1/shipment-managers/{shipmentManagerId}",headers ={"X-User-Username=\"\"", "X-User-Role=MASTER"} )
  ResponseEntity<GetShipmentManagerResponseDto> getShipmentManagerById(@PathVariable UUID shipmentManagerId);
}
