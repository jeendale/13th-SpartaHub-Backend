package com.sparta.shipment.domain.service;

import com.sparta.shipment.infrastructure.dto.GetHubInfoRes;
import java.util.UUID;

public interface HubClientService {

    GetHubInfoRes getHub(UUID hubId);

    //List<GetHubInfoRes> getAllHubs();
}
