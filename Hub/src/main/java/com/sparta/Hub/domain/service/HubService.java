package com.sparta.Hub.domain.service;

import com.sparta.Hub.domain.dto.request.CreateHubRes;
import com.sparta.Hub.domain.dto.request.UpdateHubReq;
import com.sparta.Hub.domain.dto.response.CreateHubReq;
import com.sparta.Hub.domain.dto.response.DelteHubRes;
import com.sparta.Hub.domain.dto.response.GetHubInfoRes;
import com.sparta.Hub.domain.dto.response.UpdateHubRes;
import com.sparta.Hub.exception.HubExceptionMessage;
import com.sparta.Hub.model.entity.Hub;
import com.sparta.Hub.model.repository.HubRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    @Cacheable(cacheNames = "hubCache", key = "args[0]")
    public GetHubInfoRes getHub(UUID hubId) {
        Hub hub=hubRepository.findById(hubId).orElseThrow(()-> new IllegalArgumentException(
            HubExceptionMessage.HUB_NOT_EXIST.getMessage()));
        return GetHubInfoRes.builder()
            .hubName(hub.getHubname())
            .hubId(hub.getHubId())
            .address(hub.getAddress())
            .lati(hub.getLati())
            .longti(hub.getLongti())
            .isCenterHub(hub.isCenterHub())
            .build();
    }

    @Cacheable(cacheNames = "hubAllCache",key="getMethodName()")
    public List<GetHubInfoRes> getAllHubs() {
        List<Hub> hubs=hubRepository.findAll();
        List<GetHubInfoRes> getHubInfoResList = new ArrayList<>();
        for (Hub hub : hubs) {
           GetHubInfoRes getHubs = GetHubInfoRes.builder()
                .hubName(hub.getHubname())
                .hubId(hub.getHubId())
                .address(hub.getAddress())
                .lati(hub.getLati())
                .longti(hub.getLongti())
                .isCenterHub(hub.isCenterHub())
                .build();

                getHubInfoResList.add(getHubs);
        }
        return getHubInfoResList;
    }

    @Transactional
    @CachePut(cacheNames = "hubCache",key = "args[0]")
    @CacheEvict(cacheNames = "hubAllCache",allEntries = true)
    public UpdateHubRes updateHub(UUID hubId, UpdateHubReq updateHubReq) {
        //dummy
        String username="user";


        Hub hub=hubRepository.findById(hubId).orElseThrow(()-> new IllegalArgumentException(
            HubExceptionMessage.HUB_NOT_EXIST.getMessage()));
        hub.updateCreatedByAndLastModifiedBy(username);
        if(updateHubReq.getHubname()!=null) {
            if(updateHubReq.getHubname().equals(hub.getHubname())) {
                throw new IllegalArgumentException(
                    HubExceptionMessage.HUB_NAME_EQUEAL.getMessage()
                );
            }
            hub.updateHubname(updateHubReq.getHubname());
        }
        if(updateHubReq.getAdress()!=null) {
            if(updateHubReq.getAdress().equals(hub.getAddress())) {
                throw new IllegalArgumentException(
                    HubExceptionMessage.HUB_ADRESS_EQUEAL.getMessage()
                );
            }
            hub.updateAdress(
                updateHubReq.getAdress(),
                updateHubReq.getLati(),
                updateHubReq.getLongti());
        }
        if(hub.isCenterHub()) {
            if(!updateHubReq.isCenterHub()) {
                hub.updateIsCenterHub(false);
            }
        }
        hubRepository.save(hub);
        return UpdateHubRes.builder()
            .hubId(hub.getHubId())
            .build();
    }
    @Caching(evict = {
            @CacheEvict(cacheNames = "hubCache",key="args[0]"),
            @CacheEvict(cacheNames = "hubAllCache",allEntries = true)
    })
    public DelteHubRes deleteHub(UUID hubId) {
        //dummy
        String username="user";

        Hub hub=hubRepository.findById(hubId).orElseThrow(()-> new IllegalArgumentException(
            HubExceptionMessage.HUB_NOT_EXIST.getMessage()));
        hub.updateDeleted(username);
        hubRepository.save(hub);

        return  DelteHubRes.builder()
            .hubId(hub.getHubId())
            .build();
    }
}
