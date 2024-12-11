package com.sparta.user.domain.service;

import com.sparta.user.config.AuditAwareImpl;
import com.sparta.user.domain.dto.request.SignupRequestDto;
import com.sparta.user.domain.dto.request.UpdateUserRequestDto;
import com.sparta.user.domain.dto.response.UserResponseDto;
import com.sparta.user.domain.dto.response.UsernameResponseDto;
import com.sparta.user.exception.UserExceptionMessage;
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
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditAwareImpl auditAware;

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

    public UserResponseDto getUser(String requestUsername, UserRoleEnum requestRole, String username) {
        // 요청 헤더의 role이 MASTER가 아닌 경우, 자신의 정보만 확인할 수 있음
        if (requestRole != UserRoleEnum.MASTER) {
            validateUsernameIsOwn(requestUsername, username);
        }

        User user = userRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException(UserExceptionMessage.USER_NOT_FOUND.getMessage()));

        validateDeletedUser(user);

        return UserResponseDto.from(user);
    }

    @Transactional
    public UsernameResponseDto updateUser(
            UserRoleEnum requestRole,
            String username,
            @Valid UpdateUserRequestDto requestDto) {

        validateRequestRoleIsMaster(requestRole);

        User user = userRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException(UserExceptionMessage.USER_NOT_FOUND.getMessage()));

        validateDeletedUser(user);

        user.updateUser(requestDto.getNickname(), requestDto.getSlackId());

        return UsernameResponseDto.builder()
                .username(user.getUsername())
                .build();
    }

    @Transactional
    public UsernameResponseDto deleteUser(UserRoleEnum requestRole, String username) {
        validateRequestRoleIsMaster(requestRole);

        User user = userRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException(UserExceptionMessage.USER_NOT_FOUND.getMessage()));

        validateDeletedUser(user);

        user.updateDeleted(auditAware.getCurrentAuditor().orElseThrow());

        return UsernameResponseDto.builder()
                .username(user.getUsername())
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

    private void validateUsernameIsOwn(String requestUsername, String username) {
        if (!requestUsername.equals(username)) {
            throw new IllegalArgumentException(UserExceptionMessage.NOT_ALLOWED_API.getMessage());
        }
    }

    private void validateDeletedUser(User user) {
        if (user.isDeleted()) {
            throw new IllegalArgumentException(UserExceptionMessage.DELETED_USER.getMessage());
        }
    }
}
