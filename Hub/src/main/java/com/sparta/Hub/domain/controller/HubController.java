package com.sparta.Hub.domain.controller;

import com.sparta.Hub.domain.dto.request.CreateHubReq;
import com.sparta.Hub.domain.dto.request.UpdateHubReq;
import com.sparta.Hub.domain.dto.response.CreateHubRes;
import com.sparta.Hub.domain.dto.response.DeleteHubRes;
import com.sparta.Hub.domain.dto.response.GetHubInfoRes;
import com.sparta.Hub.domain.dto.response.UpdateHubRes;
import com.sparta.Hub.domain.service.HubService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/hubs")
public class HubController {

    private final HubService hubService;

    @PostMapping
    public ResponseEntity<CreateHubRes> createHub(
        @RequestBody CreateHubReq createHubReq,
        @RequestHeader("X-User-Username") String requestUsername,
        @RequestHeader("X-User-Role") String requestRole
        ){
        CreateHubRes createHubRes = hubService.createHub(createHubReq,requestUsername,requestRole);

        return ResponseEntity.status(HttpStatus.CREATED).body(createHubRes);
    }

    @GetMapping("/{hubId}")
    public ResponseEntity<GetHubInfoRes> getHub (@PathVariable UUID hubId){
        return ResponseEntity.status(HttpStatus.OK).body(hubService.getHub(hubId));
    }
    @GetMapping
    public ResponseEntity<PagedModel<GetHubInfoRes>> getAllHubs(
        @RequestParam(required = false) String keyword,
        Pageable pageable
    ){
        Page<GetHubInfoRes> resPage =hubService.getAllHubs(keyword,pageable);
        return ResponseEntity.status(HttpStatus.OK).body(new PagedModel<>(resPage));
    }
    @PatchMapping("/{hubId}")
    public ResponseEntity<UpdateHubRes> updateHub(
        @PathVariable UUID hubId,
        @RequestBody UpdateHubReq updateHubReq,
        @RequestHeader("X-User-Username") String requestUsername,
        @RequestHeader("X-User-Role") String requestRole){
        return ResponseEntity.status(HttpStatus.OK).body(hubService.updateHub(hubId,updateHubReq,requestUsername,requestRole));
    }
    @DeleteMapping("/{hubId}")
    public ResponseEntity<DeleteHubRes> deleteHub(
        @PathVariable UUID hubId,
        @RequestHeader("X-User-Username") String requestUsername,
        @RequestHeader("X-User-Role") String requestRole){
        return ResponseEntity.status(HttpStatus.OK).body(hubService.deleteHub(hubId,requestUsername,requestRole));
    }
}
