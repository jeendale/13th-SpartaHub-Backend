package com.sparta.slack.model.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.slack.domain.dto.response.QSlackHistoryResponseDto;
import com.sparta.slack.domain.dto.response.SlackHistoryResponseDto;
import com.sparta.slack.model.entity.QSlackHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class SlackHistoryRepositoryCustomImpl implements SlackHistoryRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public SlackHistoryRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<SlackHistoryResponseDto> searchSlackHistories(String recievedSlackId, Pageable pageable) {
        Pageable validatedPageable = validatePageable(pageable);

        QSlackHistory slackHistory = QSlackHistory.slackHistory;

        // QueryDSL 쿼리 작성
        List<SlackHistoryResponseDto> content = queryFactory
                .select(new QSlackHistoryResponseDto(
                        slackHistory.slackHistoryId,
                        slackHistory.username,
                        slackHistory.recievedSlackId,
                        slackHistory.message,
                        slackHistory.sentAt
                ))
                .from(slackHistory)
                .where(
                        recievedSlackIdContains(recievedSlackId),
                        isNotDeleted()
                )
                .orderBy(validatedPageable.getSort().stream()
                        .map(order -> {
                            if ("createdAt".equals(order.getProperty())) {
                                return order.isAscending()
                                        ? slackHistory.createdAt.asc()
                                        : slackHistory.createdAt.desc();
                            } else if ("lastModifiedAt".equals(order.getProperty())) {
                                return order.isAscending()
                                        ? slackHistory.lastModifiedAt.asc()
                                        : slackHistory.lastModifiedAt.desc();
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
                        .select(slackHistory.count())
                        .from(slackHistory)
                        .where(
                                recievedSlackIdContains(recievedSlackId),
                                isNotDeleted()
                        )
                        .fetchOne()
        ).orElse(0L);

        return PageableExecutionUtils.getPage(content, validatedPageable, () -> total);
    }

    private BooleanExpression recievedSlackIdContains(String recievedSlackId) {
        return recievedSlackId != null ? QSlackHistory.slackHistory.recievedSlackId.contains(recievedSlackId) : null;
    }

    private BooleanExpression isNotDeleted() {
        return QSlackHistory.slackHistory.deleted.eq(false);
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
