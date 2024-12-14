package com.sparta.Hub.domain.service;

import com.sparta.Hub.infrastructure.dto.UserResponseDto;
import org.springframework.http.ResponseEntity;

public interface UserClientService {

  ResponseEntity<UserResponseDto> getUser(
      String username);
}
