package com.sparta.user.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_user")
public class User extends Audit{

    @Id
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String slackId;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private UserRoleEnum role;

    public void updateUser(String nickname, String slackId) {
        this.nickname = nickname;
        this.slackId = slackId;
    }
}
