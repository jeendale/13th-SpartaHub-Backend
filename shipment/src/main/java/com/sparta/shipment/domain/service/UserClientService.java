package com.sparta.shipment.domain.service;

import com.sparta.shipment.infrastructure.dto.UserResponseDto;

public interface UserClientService {

    UserResponseDto getUser(
            String username);
}
