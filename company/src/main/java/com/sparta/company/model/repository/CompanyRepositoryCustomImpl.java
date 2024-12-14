package com.sparta.company.model.repository;

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
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class CompanyRepositoryCustomImpl implements CompanyRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public CompanyRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<CompanyResponseDto> searchCompanies(String companyName,CompanyType companyType , UUID hubID, Pageable pageable) {
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
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
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

        return PageableExecutionUtils.getPage(content, pageable, () -> total);
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
}
