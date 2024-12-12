package com.sparta.user.model.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.user.domain.dto.response.QUserResponseDto;
import com.sparta.user.domain.dto.response.UserResponseDto;
import com.sparta.user.model.entity.QUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public UserRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<UserResponseDto> searchUsers(String username, String nickname, Pageable pageable) {
        QUser user = QUser.user;

        // QueryDSL 쿼리 작성
        List<UserResponseDto> content = queryFactory
                .select(new QUserResponseDto(
                        user.username,
                        user.nickname,
                        user.slackId,
                        user.role
                ))
                .from(user)
                .where(
                        usernameContains(username),
                        nicknameContains(nickname),
                        isNotDeleted()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Total count 쿼리
        long total = Optional.ofNullable(
                queryFactory
                        .select(user.count())
                        .from(user)
                        .where(
                                usernameContains(username),
                                nicknameContains(nickname),
                                isNotDeleted()
                        )
                        .fetchOne()
        ).orElse(0L);

        return PageableExecutionUtils.getPage(content, pageable, () -> total);
    }

    private BooleanExpression usernameContains(String username) {
        return username != null ? QUser.user.username.contains(username) : null;
    }

    private BooleanExpression nicknameContains(String nickname) {
        return nickname != null ? QUser.user.username.contains(nickname) : null;
    }

    private BooleanExpression isNotDeleted() {
        return QUser.user.deleted.eq(false);
    }
}
