package com.sparta.user.domain.dto;

import com.sparta.user.model.entity.User;
import com.sparta.user.model.entity.UserRoleEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Builder
public class SignupRequestDto {
    @NotBlank
    @Size(min = 4, max = 10)
    @Pattern(regexp = "^[a-z0-9]+$")
    private String username;

    @NotBlank
    @Size(min = 4, max = 10)
    @Pattern(regexp = "^[a-z0-9]+$")
    private String nickname;

    @NotBlank
    @Size(min = 8, max = 15)
    @Pattern(regexp = "^[a-zA-Z\\d!@#$%^&*()_+\\-=]*$")
    private String password;

    @NotBlank
    private String slackId;

    @NotNull
    private UserRoleEnum role;

    public User toEntity(PasswordEncoder passwordEncoder) {
        return User.builder()
                .username(username)
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .slackId(slackId)
                .role(role)
                .build();
    }
}
