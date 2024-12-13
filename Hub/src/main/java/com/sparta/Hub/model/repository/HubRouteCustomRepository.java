package com.sparta.Hub.model.repository;

import com.sparta.Hub.domain.dto.response.GetHubRouteInfoRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubRouteCustomRepository {
  Page<GetHubRouteInfoRes> searchHubRoutes(String keyword, Pageable pageable);
}
