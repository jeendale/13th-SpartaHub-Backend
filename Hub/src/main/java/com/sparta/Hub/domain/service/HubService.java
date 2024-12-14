package com.sparta.Hub.domain.service;

import com.sparta.Hub.domain.dto.request.CreateHubReq;
import com.sparta.Hub.domain.dto.request.UpdateHubReq;
import com.sparta.Hub.domain.dto.response.CreateHubRes;
import com.sparta.Hub.domain.dto.response.DeleteHubRes;
import com.sparta.Hub.domain.dto.response.GetHubInfoRes;
import com.sparta.Hub.domain.dto.response.UpdateHubRes;
import com.sparta.Hub.exception.HubExceptionMessage;
import com.sparta.Hub.model.entity.Hub;
import com.sparta.Hub.model.repository.HubRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HubService {

    private final HubRepository hubRepository;

    public CreateHubRes createHub(CreateHubReq createHubReq,String requestName,String requestRole) {
        //권한 확인
        validateRole(requestRole);

        Hub hub=Hub.builder()
                .hubname(createHubReq.getHubname())
                .address(createHubReq.getAddress())
                .lati(createHubReq.getLati())
                .longti(createHubReq.getLongti())
                .isCenterHub(createHubReq.isCenterHub())
                .build();

        hubRepository.save(hub);

        return CreateHubRes.builder()
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
    public Page<GetHubInfoRes> getAllHubs(String keyword, Pageable pageable) {
        return hubRepository.searchHubs(keyword,pageable);
    }

    @Transactional
    @CachePut(cacheNames = "hubCache",key = "args[0]")
    @CacheEvict(cacheNames = "hubAllCache",allEntries = true)
    public UpdateHubRes updateHub(UUID hubId, UpdateHubReq updateHubReq,String requestName,String requestRole) {

        validateRole(requestRole);

        Hub hub=hubRepository.findById(hubId).orElseThrow(()-> new IllegalArgumentException(
            HubExceptionMessage.HUB_NOT_EXIST.getMessage()));

        hub.updateCreatedByAndLastModifiedBy(requestName);

        hubRepository.save(checkUpdate(hub,updateHubReq));

        return UpdateHubRes.builder()
            .hubId(hub.getHubId())
            .build();
    }



    @Caching(evict = {
            @CacheEvict(cacheNames = "hubCache",key="args[0]"),
            @CacheEvict(cacheNames = "hubAllCache",allEntries = true)
    })
    public DeleteHubRes deleteHub(UUID hubId,String requestName,String requestRole) {
        validateRole(requestRole);

        Hub hub=hubRepository.findById(hubId).orElseThrow(()-> new IllegalArgumentException(
            HubExceptionMessage.HUB_NOT_EXIST.getMessage()));
        hub.updateDeleted(requestName);
        hubRepository.save(hub);

        return  DeleteHubRes.builder()
            .hubId(hub.getHubId())
            .build();
    }

    private void validateRole(String requestRole) {
        if(!requestRole.equals("MASTER")){
            throw new IllegalArgumentException(HubExceptionMessage.NOT_ALLOWED_API.getMessage());
        }
    }
    private Hub checkUpdate(Hub hub, UpdateHubReq updateHubReq) {
        if(updateHubReq.getHubname()!=null) {
            hub.updateHubname(updateHubReq.getHubname());
        }
        if(updateHubReq.getAdress()!=null) {
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
        return hub;
    }
}
