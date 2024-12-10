package com.sparta.Hub.domain.controller;

import com.sparta.Hub.domain.dto.request.HubCreateRes;
import com.sparta.Hub.domain.dto.response.HubCreateReq;
import com.sparta.Hub.domain.service.HubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<HubCreateReq> createHub(@RequestBody HubCreateRes hubCreateRes){
        HubCreateReq hubCreateReq = hubService.createHub(hubCreateRes);

        return ResponseEntity.status(HttpStatus.CREATED).body(hubCreateReq);
    }


}
