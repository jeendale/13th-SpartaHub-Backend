package com.sparta.ai.model.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ai.domain.dto.response.AiMessageResponseDto;
import com.sparta.ai.domain.dto.response.QAiMessageResponseDto;
import com.sparta.ai.model.entity.QAiMessage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class AiMessageRepositoryCustomImpl implements AiMessageRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public AiMessageRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<AiMessageResponseDto> searchAiMessages(String username, Pageable pageable) {
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
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
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

        return PageableExecutionUtils.getPage(content, pageable, () -> total);
    }

    private BooleanExpression usernameContains(String username) {
        return username != null ? QAiMessage.aiMessage.username.contains(username) : null;
    }

    private BooleanExpression isNotDeleted() {
        return QAiMessage.aiMessage.deleted.eq(false);
    }
}
