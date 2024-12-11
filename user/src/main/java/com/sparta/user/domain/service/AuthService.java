package com.sparta.user.domain.service;

import com.sparta.user.domain.dto.request.LoginRequestDto;
import com.sparta.user.domain.dto.request.SignupRequestDto;
import com.sparta.user.domain.dto.response.JwtTokenResponseDto;
import com.sparta.user.domain.dto.response.UsernameResponseDto;
import com.sparta.user.exception.AuthExceptionMessage;
import com.sparta.user.exception.UserExceptionMessage;
import com.sparta.user.jwt.JwtUtil;
import com.sparta.user.model.entity.User;
import com.sparta.user.model.entity.UserRoleEnum;
import com.sparta.user.model.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public UsernameResponseDto signup(@Valid SignupRequestDto requestDto) {
        validateUsername(requestDto.getUsername());
        validateNickname(requestDto.getNickname());
        validateSlackId(requestDto.getSlackId());
        validateRole(requestDto.getRole());

        User user = requestDto.toEntity(passwordEncoder);
        user.updateCreatedByAndLastModifiedBy(requestDto.getUsername());
        userRepository.save(user);

        return UsernameResponseDto.builder()
                .username(user.getUsername())
                .build();
    }

    @Transactional
    public UsernameResponseDto createUser(@Valid SignupRequestDto requestDto, UserRoleEnum requestRole) {
        validateRequestRoleIsMaster(requestRole);

        validateUsername(requestDto.getUsername());
        validateNickname(requestDto.getNickname());
        validateSlackId(requestDto.getSlackId());

        User user = requestDto.toEntity(passwordEncoder);
        userRepository.save(user);

        return UsernameResponseDto.builder()
                .username(user.getUsername())
                .build();
    }

    public JwtTokenResponseDto login(LoginRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUsername())
                .filter(u -> passwordEncoder.matches(requestDto.getPassword(), u.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException(
                        AuthExceptionMessage.WRONG_USERNAME_OR_PASSWORD.getMessage()));

        return JwtTokenResponseDto.builder()
                .accessToken(jwtUtil.createToken(user.getUsername(), user.getRole()))
                .build();
    }

    private void validateUsername(String username) {
        boolean checkUsername = userRepository.existsById(username);
        if (checkUsername) {
            throw new IllegalArgumentException(UserExceptionMessage.DUPLICATED_USERNAME.getMessage());
        }
    }

    private void validateNickname(String nickname) {
        boolean checkNickname = userRepository.existsByNickname(nickname);
        if (checkNickname) {
            throw new IllegalArgumentException(UserExceptionMessage.DUPLICATED_NICKNAME.getMessage());
        }
    }

    private void validateSlackId(String slackId) {
        boolean checkSlackId = userRepository.existsBySlackId(slackId);
        if (checkSlackId) {
            throw new IllegalArgumentException(UserExceptionMessage.DUPLICATED_SLACKID.getMessage());
        }
    }

    // 회원가입 시 Request Body의 role이 MASTER인지 검증하는 메서드
    private void validateRole(UserRoleEnum role) {
        if (role == UserRoleEnum.MASTER) {
            throw new IllegalArgumentException(UserExceptionMessage.NOT_ALLOWED_ROLE.getMessage());
        }
    }

    // 요청 헤더의 role이 MASTER인지 검증하는 메서드
    private void validateRequestRoleIsMaster(UserRoleEnum role) {
        if (role != UserRoleEnum.MASTER) {
            throw new IllegalArgumentException(UserExceptionMessage.NOT_ALLOWED_API.getMessage());
        }
    }
}
