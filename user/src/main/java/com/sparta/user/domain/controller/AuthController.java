package com.sparta.user.domain.controller;

import com.sparta.user.domain.dto.request.LoginRequestDto;
import com.sparta.user.domain.dto.response.JwtTokenResponseDto;
import com.sparta.user.domain.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping
    ResponseEntity<JwtTokenResponseDto> login(@RequestBody LoginRequestDto requestDto) {

        JwtTokenResponseDto responseDto = authService.login(requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
