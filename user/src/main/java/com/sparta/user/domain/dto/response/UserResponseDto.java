package com.sparta.user.domain.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sparta.user.model.entity.User;
import com.sparta.user.model.entity.UserRoleEnum;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private String username;
    private String nickname;
    private String slackId;
    private UserRoleEnum role;

    @Builder
    @QueryProjection
    public UserResponseDto(String username, String nickname, String slackId, UserRoleEnum role) {
        this.username = username;
        this.nickname = nickname;
        this.slackId = slackId;
        this.role = role;
    }

    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .slackId(user.getSlackId())
                .role(user.getRole())
                .build();
    }
}
