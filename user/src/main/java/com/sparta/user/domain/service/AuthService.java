package com.sparta.user.domain.service;

import com.sparta.user.domain.dto.request.LoginRequestDto;
import com.sparta.user.domain.dto.response.JwtTokenResponseDto;
import com.sparta.user.exception.AuthExceptionMessage;
import com.sparta.user.jwt.JwtUtil;
import com.sparta.user.model.entity.User;
import com.sparta.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public JwtTokenResponseDto login(LoginRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUsername())
                .filter(u -> passwordEncoder.matches(requestDto.getPassword(), u.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException(
                        AuthExceptionMessage.WRONG_USERNAME_OR_PASSWORD.getMessage()));

        return JwtTokenResponseDto.builder()
                .accessToken(jwtUtil.createToken(user.getUsername(), user.getRole()))
                .build();
    }
}
