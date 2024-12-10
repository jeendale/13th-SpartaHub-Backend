package com.sparta.Hub.domain.service;

import com.sparta.Hub.domain.dto.request.HubCreateRes;
import com.sparta.Hub.domain.dto.response.HubCreateReq;
import com.sparta.Hub.model.entity.Hub;
import com.sparta.Hub.model.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HubService {

    private final HubRepository hubRepository;

    public HubCreateReq createHub(HubCreateRes hubCreateRes) {
        //권한 확인
        Hub hub=Hub.builder()
                .hubname(hubCreateRes.getHubname())
                .address(hubCreateRes.getAdress())
                .lati(hubCreateRes.getLati())
                .longti(hubCreateRes.getLongti())
                .isCenterHub(hubCreateRes.isCenterHub())
                .build();

        hubRepository.save(hub);

        return HubCreateReq.builder()
                .hubUId(hub.getHubId())
                .build();

    }
}
