package com.sparta.company.domain.service;

import com.sparta.company.infrastructure.dto.UserResponseDto;
import org.springframework.http.ResponseEntity;

public interface UserClientService {

    ResponseEntity<UserResponseDto> getUser(
            String username);
}
