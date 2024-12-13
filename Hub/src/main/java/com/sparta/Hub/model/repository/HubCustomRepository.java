package com.sparta.Hub.model.repository;

import com.sparta.Hub.domain.dto.response.GetHubInfoRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubCustomRepository {
  Page<GetHubInfoRes> searchHubs(String keyword, Pageable pageable);
}
