package com.sparta.shipment.domain.controller;

import com.sparta.shipment.domain.dto.request.CreateShipmentRouteRequestDto;
import com.sparta.shipment.domain.dto.request.UpdateShipmentRouteRequestDto;
import com.sparta.shipment.domain.dto.response.GetShipmentRouteResponseDto;
import com.sparta.shipment.domain.dto.response.ShipmentRouteResponseDto;
import com.sparta.shipment.domain.service.ShipmentRouteService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shipment-routes")
@RequiredArgsConstructor
public class ShipmentRouteController {

    private final ShipmentRouteService shipmentRouteService;

    // 배송 경로 추가 API
    @PostMapping
    public ResponseEntity<ShipmentRouteResponseDto> createShipmentRoute(
            @Valid @RequestBody CreateShipmentRouteRequestDto request,
            @RequestHeader("X-User-Username") String requestUsername,
            @RequestHeader("X-User-Role") String requestRole) {
        ShipmentRouteResponseDto response = shipmentRouteService.createShipmentRoute(request, requestRole);
        return ResponseEntity.status(HttpStatus.CREATED).body((response));
    }


    // 배송 경로 삭제 API
    @DeleteMapping("/{shipmentRouteId}")
    public ResponseEntity<ShipmentRouteResponseDto> deleteShipmentRoute(
            @PathVariable UUID shipmentRouteId,
            @RequestHeader("X-User-Username") String requestUsername,
            @RequestHeader("X-User-Role") String requestRole) {
        ShipmentRouteResponseDto response = shipmentRouteService.deleteShipmentRoute(shipmentRouteId,
                requestUsername, requestRole);
        return ResponseEntity.status(HttpStatus.OK).body((response));
    }

    //배송 경로 수정 API
    @PatchMapping("/{shipmentRouteId}")
    public ResponseEntity<ShipmentRouteResponseDto> updateShipmentRoute(@PathVariable UUID shipmentRouteId,
                                                                        @RequestBody UpdateShipmentRouteRequestDto request,
                                                                        @RequestHeader("X-User-Username") String requestUsername,
                                                                        @RequestHeader("X-User-Role") String requestRole) {
        ShipmentRouteResponseDto response = shipmentRouteService.updateShipmentRoute(shipmentRouteId, request,
                requestUsername, requestRole);
        return ResponseEntity.status(HttpStatus.OK).body((response));
    }


    //배송 경로 단건 조회 API
    @GetMapping("/{shipmentRouteId}")
    public ResponseEntity<GetShipmentRouteResponseDto> getShipmentRouteById(@PathVariable UUID shipmentRouteId,
                                                                            @RequestHeader("X-User-Username") String requestUsername,
                                                                            @RequestHeader("X-User-Role") String requestRole) {
        GetShipmentRouteResponseDto response = shipmentRouteService.getShipmentRouteById(shipmentRouteId,
                requestUsername, requestRole);
        return ResponseEntity.status(HttpStatus.OK).body((response));
    }

    //배송 경로 다건 조회 API
    @GetMapping
    public ResponseEntity<PagedModel<GetShipmentRouteResponseDto>> getShipmentRoutes(
            @RequestParam(required = false) String shipmentStatus,
            @RequestParam(required = false) UUID hubId,
            @RequestParam(required = false) UUID shipmentManagerId,
            Pageable pageable,
            @RequestHeader("X-User-Username") String requestUsername,
            @RequestHeader("X-User-Role") String requestRole) {

        Page<GetShipmentRouteResponseDto> response = shipmentRouteService.getShipmentRoutes(shipmentStatus, hubId,
                shipmentManagerId,
                pageable, requestUsername, requestRole);

        return ResponseEntity.status(HttpStatus.OK).body((new PagedModel<>(response)));
    }

}
