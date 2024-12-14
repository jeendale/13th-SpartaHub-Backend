package com.sparta.Hub.model.repository;

import com.sparta.Hub.model.entity.HubRoute;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HubRouteRepository extends JpaRepository<HubRoute, UUID>, HubRouteCustomRepository {

}
