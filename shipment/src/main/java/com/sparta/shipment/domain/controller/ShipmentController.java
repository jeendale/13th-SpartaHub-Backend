package com.sparta.shipment.domain.controller;

import com.sparta.shipment.domain.dto.request.CreateShipmentRequestDto;
import com.sparta.shipment.domain.dto.request.UpdateShipmentRequestDto;
import com.sparta.shipment.domain.dto.response.GetShipmentResponseDto;
import com.sparta.shipment.domain.dto.response.ShipmentResponseDto;
import com.sparta.shipment.domain.service.ShipmentService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    // 배송 추가 API
    @PostMapping
    public ResponseEntity<ShipmentResponseDto> createShipment(
            @Valid @RequestBody CreateShipmentRequestDto request,
            @RequestHeader("X-User-Username") String requestUsername,
            @RequestHeader("X-User-Role") String requestRole) {
        ShipmentResponseDto response = shipmentService.createShipment(request, requestUsername, requestRole);
        return ResponseEntity.status(HttpStatus.CREATED).body((response));
    }

    // 배송 삭제 API
    @DeleteMapping("/{shipmentId}")
    public ResponseEntity<ShipmentResponseDto> deleteShipment(
            @PathVariable UUID shipmentId,
            @RequestHeader("X-User-Username") String requestUsername,
            @RequestHeader("X-User-Role") String requestRole) {
        ShipmentResponseDto response = shipmentService.deleteShipment(shipmentId,
                requestUsername, requestRole);
        return ResponseEntity.status(HttpStatus.OK).body((response));
    }

    //배송 수정 API
    @PatchMapping("/{shipmentId}")
    public ResponseEntity<ShipmentResponseDto> updateShipment(@PathVariable UUID shipmentId,
                                                              @RequestBody UpdateShipmentRequestDto request,
                                                              @RequestHeader("X-User-Username") String requestUsername,
                                                              @RequestHeader("X-User-Role") String requestRole) {
        ShipmentResponseDto response = shipmentService.updateShipment(shipmentId, request,
                requestUsername, requestRole);
        return ResponseEntity.status(HttpStatus.OK).body((response));
    }


    //배송 단건 조회 API
    @GetMapping("/{shipmentId}")
    public ResponseEntity<GetShipmentResponseDto> getShipmentById(@PathVariable UUID shipmentId,
                                                                  @RequestHeader("X-User-Username") String requestUsername,
                                                                  @RequestHeader("X-User-Role") String requestRole) {
        GetShipmentResponseDto response = shipmentService.getShipmentById(shipmentId,
                requestUsername, requestRole);
        return ResponseEntity.status(HttpStatus.OK).body((response));
    }

    //배송 다건 조회 API
    @GetMapping
    public ResponseEntity<PagedModel<GetShipmentResponseDto>> getShipments(
            @RequestParam(required = false) String shipmentStatus,
            @RequestParam(required = false) String receiverName,
            @RequestParam(required = false) String shippingAddress,
            @RequestParam(required = false) UUID hubId,
            @RequestParam(required = false) UUID shipmentManagerId,
            Pageable pageable,
            @RequestHeader("X-User-Username") String requestUsername,
            @RequestHeader("X-User-Role") String requestRole) {

        Pageable adjustedPageable = adjustPageSize(pageable, List.of(10, 30, 50), 10);

        Page<GetShipmentResponseDto> response = shipmentService.getShipments(shipmentStatus, receiverName,
                shippingAddress, hubId, shipmentManagerId, adjustedPageable, requestUsername, requestRole);

        return ResponseEntity.status(HttpStatus.OK).body((new PagedModel<>(response)));
    }

    private Pageable adjustPageSize(Pageable pageable, List<Integer> allowSizes, int defaultSize) {
        if (!allowSizes.contains(pageable.getPageSize())) {
            return PageRequest.of(pageable.getPageNumber(), defaultSize, pageable.getSort());
        }
        return pageable;
    }

}
