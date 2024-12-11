package com.sparta.shipment.domain.controller;

import com.sparta.shipment.domain.dto.CreateShipmentManagerRequestDto;
import com.sparta.shipment.domain.dto.ShipmentManagerResponseDto;
import com.sparta.shipment.domain.service.ShipmentManagerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shipment-managers")
@RequiredArgsConstructor
public class ShipmentManagerController {

    private final ShipmentManagerService shipmentManagerService;

    // 배송담당자 추가 API
    @PostMapping
    public ResponseEntity<ShipmentManagerResponseDto> createShipmentManager(
            @Valid @RequestBody CreateShipmentManagerRequestDto request) {
        ShipmentManagerResponseDto response = shipmentManagerService.createShipmentManager(request);
        return ResponseEntity.status(HttpStatus.CREATED).body((response));
    }


}
