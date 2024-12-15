package com.sparta.Hub.domain.service;

import com.sparta.Hub.domain.dto.request.CreateHubReq;
import com.sparta.Hub.domain.dto.request.UpdateHubReq;
import com.sparta.Hub.domain.dto.response.DeleteHubRes;
import com.sparta.Hub.domain.dto.response.GetHubInfoRes;
import com.sparta.Hub.domain.dto.response.HubIdRes;
import com.sparta.Hub.exception.FeignClientExceptionMessage;
import com.sparta.Hub.exception.HubExceptionMessage;
import com.sparta.Hub.exception.ServiceNotAvailableException;
import com.sparta.Hub.infrastructure.dto.UserResponseDto;
import com.sparta.Hub.model.entity.Hub;
import com.sparta.Hub.model.repository.HubRepository;
import feign.FeignException.BadRequest;
import feign.FeignException.ServiceUnavailable;
import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HubService {

    private final HubRepository hubRepository;
    private final UserClientService userClientService;

    @Transactional
    @Retry(name = "userServiceRetry")
    @CircuitBreaker(name = "hub-service", fallbackMethod = "fallback")
    public HubIdRes createHub(CreateHubReq createHubReq,String requestName,String requestRole) {
        //권한 확인
        validateRole(requestRole);
        UserResponseDto userResponseDto = getUserResponseDto(createHubReq.getUsername());
        validateUserRoleIsHubManager(userResponseDto.getRole());

        Hub hub=Hub.builder()
                .hubname(createHubReq.getHubname())
                .address(createHubReq.getAddress())
                .lati(createHubReq.getLati())
                .longti(createHubReq.getLongti())
                .isCenterHub(createHubReq.isCenterHub())
                .username(userResponseDto.getUsername())
                .build();

        hubRepository.save(hub);

        return HubIdRes.builder()
                .hubId(hub.getHubId())
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
            .username(hub.getUsername())
            .build();
    }

    @Cacheable(cacheNames = "hubAllCache",key="getMethodName()")
    public Page<GetHubInfoRes> getAllHubs(String keyword, Pageable pageable) {
        return hubRepository.searchHubs(keyword,pageable);
    }

    @Transactional
    @CachePut(cacheNames = "hubCache",key = "args[0]")
    @CacheEvict(cacheNames = "hubAllCache",allEntries = true)
    @CircuitBreaker(name = "hub-service", fallbackMethod = "fallback")
    public HubIdRes updateHub(UUID hubId, UpdateHubReq updateHubReq,String requestName,String requestRole) {

        validateRole(requestRole);
        Hub hub=hubRepository.findById(hubId).orElseThrow(()-> new IllegalArgumentException(
            HubExceptionMessage.HUB_NOT_EXIST.getMessage()));

        Hub updateHub=checkUpdate(hub,updateHubReq);
        hubRepository.save(updateHub);

        return HubIdRes.builder()
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

    private UserResponseDto getUserResponseDto(String username) {
        return userClientService.getUser(username).getBody();
    }

    private void validateUserRoleIsHubManager(String role) {
        if (!role.equals("HUB_MANAGER")) {
            throw new IllegalArgumentException(HubExceptionMessage.NOT_HUB_MANAGER.getMessage());
        }
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
        if(updateHubReq.getUsername()!=null) {
            UserResponseDto userResponseDto = getUserResponseDto(updateHubReq.getUsername());
            validateUserRoleIsHubManager(userResponseDto.getRole());

            hub.updateHubManger(updateHubReq.getUsername());
        }
        if(hub.isCenterHub()) {
            if(!updateHubReq.isCenterHub()) {
                hub.updateIsCenterHub(false);
            }
        }
        return hub;
    }

    public HubIdRes fallback(Throwable throwable) {
        if (throwable instanceof BadRequest) {
            log.warn("User 400 Bad Request 발생: {}", throwable.getMessage());
            if (throwable.getMessage().contains(FeignClientExceptionMessage.USER_NOT_FOUND.getMessage())) {
                throw new IllegalArgumentException(FeignClientExceptionMessage.USER_NOT_FOUND.getMessage());
            }
        }

        if (throwable instanceof RetryableException) {
            log.warn("RetryableException 발생");
            throw new ServiceNotAvailableException(FeignClientExceptionMessage.SERVICE_NOT_AVAILABLE.getMessage());
        }

        if (throwable instanceof ServiceUnavailable) {
            log.warn("ServiceUnavailableException 발생");
            throw new ServiceNotAvailableException(FeignClientExceptionMessage.SERVICE_NOT_AVAILABLE.getMessage());
        }

        log.warn("기타 예외 발생: {}", String.valueOf(throwable));
        throw new ServiceNotAvailableException(FeignClientExceptionMessage.SERVICE_NOT_AVAILABLE.getMessage());
    }
}
