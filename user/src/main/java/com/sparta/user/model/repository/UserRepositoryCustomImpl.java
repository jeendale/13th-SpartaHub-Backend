package com.sparta.user.model.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.user.domain.dto.response.QUserResponseDto;
import com.sparta.user.domain.dto.response.UserResponseDto;
import com.sparta.user.model.entity.QUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public UserRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<UserResponseDto> searchUsers(String username, String nickname, Pageable pageable) {
        Pageable validatedPageable = validatePageable(pageable);

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
                .orderBy(validatedPageable.getSort().stream()
                        .map(order -> {
                            if ("createdAt".equals(order.getProperty())) {
                                return order.isAscending()
                                        ? user.createdAt.asc()
                                        : user.createdAt.desc();
                            } else if ("lastModifiedAt".equals(order.getProperty())) {
                                return order.isAscending()
                                        ? user.lastModifiedAt.asc()
                                        : user.lastModifiedAt.desc();
                            } else {
                                throw new IllegalArgumentException("정렬 옵션이 잘못되었습니다: " + order.getProperty());
                            }
                        })
                        .toArray(OrderSpecifier[]::new))
                .offset(validatedPageable.getOffset())
                .limit(validatedPageable.getPageSize())
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

        return PageableExecutionUtils.getPage(content, validatedPageable, () -> total);
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

    private Pageable validatePageable(Pageable pageable) {
        int size = pageable.getPageSize();
        if (size != 10 && size != 30 && size != 50) {
            // 유효하지 않은 경우 기본값 10으로 수정
            return PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
        }
        return pageable;
    }
}
