package com.sparta.Hub.domain.service;

import com.sparta.Hub.model.repository.HubRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HubRouteService {

  private final HubRouteRepository hubRouteRepository;
}
