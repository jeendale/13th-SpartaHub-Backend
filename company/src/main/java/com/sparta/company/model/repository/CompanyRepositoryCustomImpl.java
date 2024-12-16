package com.sparta.company.model.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.company.domain.dto.response.CompanyResponseDto;
import com.sparta.company.domain.dto.response.QCompanyResponseDto;
import com.sparta.company.model.entity.CompanyType;
import com.sparta.company.model.entity.QCompany;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class CompanyRepositoryCustomImpl implements CompanyRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public CompanyRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<CompanyResponseDto> searchCompanies(String companyName,CompanyType companyType , UUID hubID, Pageable pageable) {
        Pageable validatedPageable = validatePageable(pageable);

        QCompany company = QCompany.company;

        // QueryDSL 쿼리 작성
        List<CompanyResponseDto> content = queryFactory
                .select(new QCompanyResponseDto(
                        company.companyId,
                        company.hubId,
                        company.username,
                        company.companyName,
                        company.companyAddress,
                        company.companyType
                ))
                .from(company)
                .where(
                        companyNameContains(companyName),
                        companyTypeEq(companyType),
                        hubIdEq(hubID),
                        isNotDeleted()
                )
                .orderBy(validatedPageable.getSort().stream()
                        .map(order -> {
                            if ("createdAt".equals(order.getProperty())) {
                                return order.isAscending()
                                        ? company.createdAt.asc()
                                        : company.createdAt.desc();
                            } else if ("lastModifiedAt".equals(order.getProperty())) {
                                return order.isAscending()
                                        ? company.lastModifiedAt.asc()
                                        : company.lastModifiedAt.desc();
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
                        .select(company.count())
                        .from(company)
                        .where(
                                companyNameContains(companyName),
                                companyTypeEq(companyType),
                                hubIdEq(hubID),
                                isNotDeleted()
                        )
                        .fetchOne()
        ).orElse(0L);

        return PageableExecutionUtils.getPage(content, validatedPageable, () -> total);
    }

    private BooleanExpression companyNameContains(String companyName) {
        return companyName != null ? QCompany.company.companyName.contains(companyName) : null;
    }

    private BooleanExpression companyTypeEq(CompanyType companyType) {
        return companyType != null ? QCompany.company.companyType.eq(companyType) : null;
    }

    private BooleanExpression hubIdEq(UUID hubId) {
        return hubId != null ? QCompany.company.hubId.eq(hubId) : null;
    }

    private BooleanExpression isNotDeleted() {
        return QCompany.company.deleted.eq(false);
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
