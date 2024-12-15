package com.sparta.shipment.domain.service;

import com.sparta.shipment.infrastructure.dto.GetHubInfoRes;
import com.sparta.shipment.infrastructure.dto.GetHubRouteInfoRes;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubClientService {

    GetHubInfoRes getHub(UUID hubId);

    //List<GetHubInfoRes> getAllHubs();

    Page<GetHubRouteInfoRes> getHubRoutesByHubIds(UUID startHubId, UUID endHubId, Pageable pageable);
}
