package com.sparta.user.domain.dto;

import com.sparta.user.model.entity.User;
import com.sparta.user.model.entity.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private String username;
    private String nickname;
    private String slackId;
    private UserRoleEnum role;

    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .slackId(user.getSlackId())
                .role(user.getRole())
                .build();
    }
}
