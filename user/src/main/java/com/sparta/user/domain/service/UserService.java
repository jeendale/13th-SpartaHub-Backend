package com.sparta.user.domain.service;

import com.sparta.user.domain.dto.SignupRequestDto;
import com.sparta.user.domain.dto.UsernameResponseDto;
import com.sparta.user.exception.UserExceptionMessage;
import com.sparta.user.model.entity.User;
import com.sparta.user.model.entity.UserRoleEnum;
import com.sparta.user.model.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditorAware<String> auditorAware;

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

    private void validateUsername(String username) {
        Optional<User> checkUsername = userRepository.findById(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException(UserExceptionMessage.DUPLICATED_USERNAME.getMessage());
        }
    }

    private void validateNickname(String nickname) {
        Optional<User> checkUsername = userRepository.findByNickname(nickname);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException(UserExceptionMessage.DUPLICATED_NICKNAME.getMessage());
        }
    }

    private void validateSlackId(String slackId) {
        Optional<User> checkUsername = userRepository.findBySlackId(slackId);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException(UserExceptionMessage.DUPLICATED_SLACKID.getMessage());
        }
    }

    private void validateRole(UserRoleEnum role) {
        if (role == UserRoleEnum.MASTER) {
            throw new IllegalArgumentException(UserExceptionMessage.NOT_ALLOWED_ROLE.getMessage());
        }
    }
}
