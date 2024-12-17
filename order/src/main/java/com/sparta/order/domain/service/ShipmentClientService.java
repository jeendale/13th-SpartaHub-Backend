package com.sparta.order.domain.service;

import com.sparta.order.infrastructure.dto.CreateShipmentManagerRequestDto;
import com.sparta.order.infrastructure.dto.CreateShipmentRequestDto;
import com.sparta.order.infrastructure.dto.CreateShipmentRouteRequestDto;
import com.sparta.order.infrastructure.dto.GetShipmentManagerResponseDto;
import com.sparta.order.infrastructure.dto.GetShipmentResponseDto;
import com.sparta.order.infrastructure.dto.ShipmentManagerResponseDto;
import com.sparta.order.infrastructure.dto.ShipmentResponseDto;
import com.sparta.order.infrastructure.dto.ShipmentRouteResponseDto;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface ShipmentClientService {
  ResponseEntity<ShipmentResponseDto> createShipment(CreateShipmentRequestDto request);
  ResponseEntity<ShipmentManagerResponseDto> createShipmentManager(CreateShipmentManagerRequestDto request);
  ResponseEntity<ShipmentRouteResponseDto> createShipmentRoute(CreateShipmentRouteRequestDto request);
  ResponseEntity<GetShipmentResponseDto> getShipmentById(UUID shipmentId);
  ResponseEntity<GetShipmentManagerResponseDto> getShipmentManagerById(UUID shipmentManagerId);
}
