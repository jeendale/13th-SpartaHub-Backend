package com.sparta.Hub.domain.service;

import com.sparta.Hub.domain.dto.request.CreateHubRes;
import com.sparta.Hub.domain.dto.request.UpdateHubReq;
import com.sparta.Hub.domain.dto.response.CreateHubReq;
import com.sparta.Hub.domain.dto.response.DelteHubRes;
import com.sparta.Hub.domain.dto.response.GetHubInfoRes;
import com.sparta.Hub.domain.dto.response.UpdateHubRes;
import com.sparta.Hub.model.entity.Hub;
import com.sparta.Hub.model.repository.HubRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HubService {

    private final HubRepository hubRepository;

    public CreateHubReq createHub(CreateHubRes createHubRes) {
        //권한 확인
        Hub hub=Hub.builder()
                .hubname(createHubRes.getHubname())
                .address(createHubRes.getAdress())
                .lati(createHubRes.getLati())
                .longti(createHubRes.getLongti())
                .isCenterHub(createHubRes.isCenterHub())
                .build();

        hubRepository.save(hub);

        return CreateHubReq.builder()
                .hubUId(hub.getHubId())
                .build();

    }

    public GetHubInfoRes getHub(UUID hubId) {
        Hub hub=hubRepository.findById(hubId).orElseThrow(()-> new IllegalArgumentException("허브가 존재하지 않습니다."));
        return GetHubInfoRes.builder()
            .hubName(hub.getHubname())
            .hubId(hub.getHubId())
            .address(hub.getAddress())
            .lati(hub.getLati())
            .longti(hub.getLongti())
            .isCenterHub(hub.isCenterHub())
            .build();
    }

    @Transactional
    public DelteHubRes delteHub(UUID hubId) {
        //dummy
        String username="user";

        Hub hub=hubRepository.findById(hubId).orElseThrow(()-> new IllegalArgumentException("허브가 존재하지 않습니다."));
        hub.updateDeleted(username);
        hubRepository.save(hub);

        return  DelteHubRes.builder()
            .hubId(hub.getHubId())
            .build();
    }

    public UpdateHubRes updateHub(UUID hubId, UpdateHubReq updateHubReq) {
        //dummy
        String username="user";

        Hub hub=hubRepository.findById(hubId).orElseThrow(()-> new IllegalArgumentException("허브가 존재하지 않습니다."));
        hub.updateCreatedByAndLastModifiedBy(username);

        hub= Hub.builder()
            .hubname(updateHubReq.getHubname())
            .address(updateHubReq.getAdress())
            .lati(updateHubReq.getLati())
            .longti(updateHubReq.getLongti())
            .isCenterHub(updateHubReq.isCenterHub())
            .build();

        hubRepository.save(hub);
        return UpdateHubRes.builder()
            .hubId(hub.getHubId())
            .build();
    }
}
