package com.sparta.shipment.domain.controller;

import com.sparta.shipment.domain.dto.ShipmentManagerSearchDto;
import com.sparta.shipment.domain.dto.request.CreateShipmentManagerRequestDto;
import com.sparta.shipment.domain.dto.request.UpdateShipmentManagerRequestDto;
import com.sparta.shipment.domain.dto.response.GetShipmentManagerResponseDto;
import com.sparta.shipment.domain.dto.response.ShipmentManagerResponseDto;
import com.sparta.shipment.domain.service.ShipmentManagerService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
            @Valid @RequestBody CreateShipmentManagerRequestDto request,
            @RequestHeader("X-User-Username") String requestUsername,
            @RequestHeader("X-User-Role") String requestRole) {
        ShipmentManagerResponseDto response = shipmentManagerService.createShipmentManager(request, requestUsername,
                requestRole);
        return ResponseEntity.status(HttpStatus.CREATED).body((response));
    }

    // 배송담당자 삭제 API
    @DeleteMapping("/{shipmentManagerId}")
    public ResponseEntity<ShipmentManagerResponseDto> deleteShipmentManager(
            @PathVariable UUID shipmentManagerId,
            @RequestHeader("X-User-Username") String requestUsername,
            @RequestHeader("X-User-Role") String requestRole) {
        ShipmentManagerResponseDto response = shipmentManagerService.deleteShipmentManager(shipmentManagerId,
                requestUsername, requestRole);
        return ResponseEntity.status(HttpStatus.OK).body((response));
    }

    @PatchMapping("/{shipmentManagerId}")
    public ResponseEntity<ShipmentManagerResponseDto> updateShipmentManager(@PathVariable UUID shipmentManagerId,
                                                                            @RequestBody UpdateShipmentManagerRequestDto request,
                                                                            @RequestHeader("X-User-Username") String requestUsername,
                                                                            @RequestHeader("X-User-Role") String requestRole) {
        ShipmentManagerResponseDto response = shipmentManagerService.updateShipmentManager(shipmentManagerId, request,
                requestUsername, requestRole);
        return ResponseEntity.status(HttpStatus.OK).body((response));
    }

    @GetMapping("/{shipmentManagerId}")
    public ResponseEntity<GetShipmentManagerResponseDto> getShipmentManagerById(@PathVariable UUID shipmentManagerId,
                                                                                @RequestHeader("X-User-Username") String requestUsername,
                                                                                @RequestHeader("X-User-Role") String requestRole) {
        GetShipmentManagerResponseDto response = shipmentManagerService.getShipmentManagerById(shipmentManagerId,
                requestUsername, requestRole);
        return ResponseEntity.status(HttpStatus.OK).body((response));
    }

    @GetMapping
    public ResponseEntity<PagedModel<GetShipmentManagerResponseDto>> getShipmentManagers(
            ShipmentManagerSearchDto searchDto,
            Pageable pageable,
            @RequestHeader("X-User-Username") String requestUsername,
            @RequestHeader("X-User-Role") String requestRole) {
        Page<GetShipmentManagerResponseDto> response = shipmentManagerService.getShipmentManagers(searchDto,
                pageable, requestUsername, requestRole);

        return ResponseEntity.status(HttpStatus.OK).body((new PagedModel<>(response)));
    }
}
