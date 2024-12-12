package com.sparta.user.domain.controller;

import com.sparta.user.domain.dto.request.UpdateUserRequestDto;
import com.sparta.user.domain.dto.response.UserResponseDto;
import com.sparta.user.domain.dto.response.UsernameResponseDto;
import com.sparta.user.domain.service.UserService;
import com.sparta.user.model.entity.UserRoleEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDto> getUser(
            @RequestHeader("X-User-Username") String requestUsername,
            @RequestHeader("X-User-Role") UserRoleEnum requestRole,
            @PathVariable String username) {

        UserResponseDto responseDto = userService.getUser(requestUsername, requestRole, username);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/{username}")
    public ResponseEntity<UsernameResponseDto> updateUser(
            @Valid @RequestBody UpdateUserRequestDto requestDto,
            @RequestHeader("X-User-Role") UserRoleEnum requestRole,
            @PathVariable String username) {

        UsernameResponseDto responseDto = userService.updateUser(requestRole, username, requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<UsernameResponseDto> deleteUser(
            @RequestHeader("X-User-Role") UserRoleEnum requestRole,
            @PathVariable String username) {

        UsernameResponseDto responseDto = userService.deleteUser(requestRole, username);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
