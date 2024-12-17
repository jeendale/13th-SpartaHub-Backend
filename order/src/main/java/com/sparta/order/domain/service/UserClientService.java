package com.sparta.order.domain.service;

import com.sparta.order.infrastructure.dto.UserResponseDto;
import org.springframework.http.ResponseEntity;

public interface UserClientService {

  ResponseEntity<UserResponseDto> getUser(
      String username);
}
