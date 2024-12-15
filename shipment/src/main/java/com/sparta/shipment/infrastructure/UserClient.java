package com.sparta.shipment.infrastructure;

import com.sparta.shipment.domain.service.UserClientService;
import com.sparta.shipment.infrastructure.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient extends UserClientService {

    @GetMapping(value = "/api/v1/users/{username}", headers = {"X-User-Username=\"\"", "X-User-Role=MASTER"})
    UserResponseDto getUser(@PathVariable String username);
}
