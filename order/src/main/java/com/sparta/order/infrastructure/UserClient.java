package com.sparta.order.infrastructure;

import com.sparta.order.domain.service.UserClientService;
import com.sparta.order.infrastructure.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient extends UserClientService {

  @GetMapping(value = "/api/v1/users/{username}", headers = {"X-User-Username=\"\"", "X-User-Role=MASTER"})
  ResponseEntity<UserResponseDto> getUser(@PathVariable String username);
}
