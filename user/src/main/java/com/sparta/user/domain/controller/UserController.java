package com.sparta.user.domain.controller;

import com.sparta.user.domain.dto.SignupRequestDto;
import com.sparta.user.domain.dto.UsernameResponseDto;
import com.sparta.user.domain.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UsernameResponseDto> signup(@Valid @RequestBody SignupRequestDto requestDto) {

        UsernameResponseDto responseDto = userService.signup(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
