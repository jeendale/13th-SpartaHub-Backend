package com.sparta.user.model.repository;

import com.sparta.user.domain.dto.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
    Page<UserResponseDto> searchUsers(String username, String nickname, Pageable pageable);
}
