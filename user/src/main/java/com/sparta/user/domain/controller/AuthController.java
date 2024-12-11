package com.sparta.user.domain.controller;

import com.sparta.user.domain.dto.request.LoginRequestDto;
import com.sparta.user.domain.dto.request.SignupRequestDto;
import com.sparta.user.domain.dto.response.JwtTokenResponseDto;
import com.sparta.user.domain.dto.response.UsernameResponseDto;
import com.sparta.user.domain.service.AuthService;
import com.sparta.user.model.entity.UserRoleEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UsernameResponseDto> signup(@Valid @RequestBody SignupRequestDto requestDto) {

        UsernameResponseDto responseDto = authService.signup(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/create")
    public ResponseEntity<UsernameResponseDto> createUser(
            @Valid @RequestBody SignupRequestDto requestDto,
            @RequestHeader("X-User-Role") UserRoleEnum requestRole) {

        UsernameResponseDto responseDto = authService.createUser(requestDto, requestRole);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping
    ResponseEntity<JwtTokenResponseDto> login(@RequestBody LoginRequestDto requestDto) {

        JwtTokenResponseDto responseDto = authService.login(requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
