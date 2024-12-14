package com.sparta.Hub.domain.controller;

import com.sparta.Hub.domain.dto.request.CreateHubRouteReq;
import com.sparta.Hub.domain.dto.request.UpdateHubRouteReq;
import com.sparta.Hub.domain.dto.response.CreateHubRouteRes;
import com.sparta.Hub.domain.dto.response.DeleteHubRouteRes;
import com.sparta.Hub.domain.dto.response.GetHubRouteInfoRes;
import com.sparta.Hub.domain.dto.response.UpdateHubRouteRes;
import com.sparta.Hub.domain.service.HubRouteService;
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
@RequestMapping("/api/v1/hubroutes")
public class HubRouteController {

  private final HubRouteService hubRouteService;

  @PostMapping
  public ResponseEntity<CreateHubRouteRes> createHubRoute(
      @RequestBody CreateHubRouteReq createHubRouteReq,
      @RequestHeader("X-User-Username") String requestUsername,
      @RequestHeader("X-User-Role") String requestRole) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(hubRouteService.createHubRoute(createHubRouteReq,requestUsername,requestRole));
  }

  @GetMapping("/{hubRouteId}")
  public ResponseEntity<GetHubRouteInfoRes> getHubRoute(@PathVariable UUID hubRouteId) {
    return ResponseEntity.status(HttpStatus.OK).body(hubRouteService.getHubRoute(hubRouteId));
  }
  @GetMapping
  public ResponseEntity<PagedModel<GetHubRouteInfoRes>> getAllHubRoutes(
      @RequestParam(required = false) String keyword,
      Pageable pageable
  ){
    Page<GetHubRouteInfoRes> resPage=hubRouteService.getAllHubRoutes(keyword,pageable);
    return ResponseEntity.status(HttpStatus.OK).body(new PagedModel<>(resPage));
  }


  @PatchMapping("/{hubRouteId}")
  public ResponseEntity<UpdateHubRouteRes> updateHubRoute(
      @PathVariable UUID hubRouteId,
      @RequestBody UpdateHubRouteReq updateHubRouteReq,
      @RequestHeader("X-User-Username") String requestUsername,
      @RequestHeader("X-User-Role") String requestRole
  ){
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(hubRouteService.updateHubRoute(hubRouteId,updateHubRouteReq,requestUsername,requestRole));
  }
  @DeleteMapping("/{hubRouteId}")
  public ResponseEntity<DeleteHubRouteRes> deleteHubRoute(
      @PathVariable UUID hubRouteId,
      @RequestHeader("X-User-Username") String requestUsername,
      @RequestHeader("X-User_Role") String requestRole
  ){
    return ResponseEntity.status(HttpStatus.OK).body(hubRouteService.deleteHubRoute(hubRouteId,requestUsername,requestRole));
  }

}
