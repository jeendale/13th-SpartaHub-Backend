package com.sparta.Hub.domain.controller;

import com.sparta.Hub.domain.service.HubRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ap/v1/hubroutes")
public class HubRouteController {

  private final HubRouteService hubRouteService;

}
