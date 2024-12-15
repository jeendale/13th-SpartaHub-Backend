package com.sparta.shipment.infrastructure;

import com.sparta.shipment.domain.service.HubClientService;
import com.sparta.shipment.infrastructure.dto.GetHubInfoRes;
import com.sparta.shipment.infrastructure.dto.GetHubRouteInfoRes;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "hub-service")
public interface HubClient extends HubClientService {

    @GetMapping(value = "/api/v1/hubs/{hubId}", headers = {"X-User-Username=ShipmentService", "X-User-Role=MASTER"})
    GetHubInfoRes getHub(@PathVariable UUID hubId);

    @GetMapping(value = "/api/v1/hubroutes", headers = {"X-User-Username=ShipmentService", "X-User-Role=MASTER"})
    Page<GetHubRouteInfoRes> getHubRoutesByHubIds(
            @RequestParam(required = false) UUID startHubId,
            @RequestParam(required = false) UUID endHubId,
            Pageable pageable
    );

}
