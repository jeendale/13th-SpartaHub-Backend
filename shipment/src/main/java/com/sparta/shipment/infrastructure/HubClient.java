package com.sparta.shipment.infrastructure;

import com.sparta.shipment.domain.service.HubClientService;
import com.sparta.shipment.infrastructure.dto.GetHubInfoRes;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hub-service")
public interface HubClient extends HubClientService {

    @GetMapping(value = "/api/v1/hubs/{hubId}", headers = {"X-User-Username=ShipmentService", "X-User-Role=MASTER"})
    GetHubInfoRes getHub(@PathVariable UUID hubId);

    /*
    @GetMapping(value = "/api/v1/hubs", headers = {"X-User-Username=ShipmentService", "X-User-Role=MASTER"})
    List<GetHubInfoRes> getAllHubs();
    */

}
