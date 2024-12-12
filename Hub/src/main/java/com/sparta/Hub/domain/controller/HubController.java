package com.sparta.Hub.domain.controller;

import com.sparta.Hub.domain.dto.request.CreateHubRes;
import com.sparta.Hub.domain.dto.request.UpdateHubReq;
import com.sparta.Hub.domain.dto.response.CreateHubReq;
import com.sparta.Hub.domain.dto.response.DelteHubRes;
import com.sparta.Hub.domain.dto.response.GetHubInfoRes;
import com.sparta.Hub.domain.dto.response.UpdateHubRes;
import com.sparta.Hub.domain.service.HubService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hubs")
public class HubController {

    private final HubService hubService;

    @PostMapping
    public ResponseEntity<CreateHubReq> createHub(@RequestBody CreateHubRes createHubRes){
        CreateHubReq createHubReq = hubService.createHub(createHubRes);

        return ResponseEntity.status(HttpStatus.CREATED).body(createHubReq);
    }

    @GetMapping("/{hubId}")
    public ResponseEntity<GetHubInfoRes> getHub (@PathVariable UUID hubId){
        return ResponseEntity.status(HttpStatus.OK).body(hubService.getHub(hubId));
    }
    @GetMapping
    public ResponseEntity<List<GetHubInfoRes>> getAllHubs(){
        return ResponseEntity.status(HttpStatus.OK).body(hubService.getAllHubs());
    }
    @PatchMapping("/{hubId}")
    public ResponseEntity<UpdateHubRes> updateHub(@PathVariable UUID hubId, @RequestBody UpdateHubReq updateHubReq){
        return ResponseEntity.status(HttpStatus.OK).body(hubService.updateHub(hubId,updateHubReq));
    }
    @DeleteMapping("/{hubId}")
    public ResponseEntity<DelteHubRes> deleteHub(@PathVariable UUID hubId){
        return ResponseEntity.status(HttpStatus.OK).body(hubService.deleteHub(hubId));
    }
}
