package com.sparta.ai.model.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ai.domain.dto.response.AiMessageResponseDto;
import com.sparta.ai.domain.dto.response.QAiMessageResponseDto;
import com.sparta.ai.model.entity.QAiMessage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class AiMessageRepositoryCustomImpl implements AiMessageRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public AiMessageRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<AiMessageResponseDto> searchAiMessages(String username, Pageable pageable) {
        Pageable validatedPageable = validatePageable(pageable);

        QAiMessage aiMessage = QAiMessage.aiMessage;

        // QueryDSL 쿼리 작성
        List<AiMessageResponseDto> content = queryFactory
                .select(new QAiMessageResponseDto(
                        aiMessage.aiMessageId,
                        aiMessage.username,
                        aiMessage.prompt,
                        aiMessage.content
                ))
                .from(aiMessage)
                .where(
                        usernameContains(username),
                        isNotDeleted()
                )
                .orderBy(validatedPageable.getSort().stream()
                        .map(order -> {
                            if ("createdAt".equals(order.getProperty())) {
                                return order.isAscending()
                                        ? aiMessage.createdAt.asc()
                                        : aiMessage.createdAt.desc();
                            } else if ("lastModifiedAt".equals(order.getProperty())) {
                                return order.isAscending()
                                        ? aiMessage.lastModifiedAt.asc()
                                        : aiMessage.lastModifiedAt.desc();
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
                        .select(aiMessage.count())
                        .from(aiMessage)
                        .where(
                                usernameContains(username),
                                isNotDeleted()
                        )
                        .fetchOne()
        ).orElse(0L);

        return PageableExecutionUtils.getPage(content, validatedPageable, () -> total);
    }

    private BooleanExpression usernameContains(String username) {
        return username != null ? QAiMessage.aiMessage.username.contains(username) : null;
    }

    private BooleanExpression isNotDeleted() {
        return QAiMessage.aiMessage.deleted.eq(false);
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
