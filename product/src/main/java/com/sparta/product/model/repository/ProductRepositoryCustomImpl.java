package com.sparta.product.model.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.product.domain.dto.response.ProductResponseDto;
import com.sparta.product.domain.dto.response.QProductResponseDto;
import com.sparta.product.model.entity.QProduct;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public ProductRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<ProductResponseDto> searchProducts(String productName, UUID hubId, UUID companyId, Pageable pageable) {
        QProduct product = QProduct.product;

        // QueryDSL 쿼리 작성
        List<ProductResponseDto> content = queryFactory
                .select(new QProductResponseDto(
                        product.productId,
                        product.hubId,
                        product.companyId,
                        product.productName
                ))
                .from(product)
                .where(
                        productNameContains(productName),
                        hubIdEq(hubId),
                        companyIdEq(companyId),
                        isNotDeleted()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Total count 쿼리
        long total = Optional.ofNullable(
                queryFactory
                        .select(product.count())
                        .from(product)
                        .where(
                                productNameContains(productName),
                                hubIdEq(hubId),
                                companyIdEq(companyId),
                                isNotDeleted()
                        )
                        .fetchOne()
        ).orElse(0L);

        return PageableExecutionUtils.getPage(content, pageable, () -> total);
    }

    private BooleanExpression productNameContains(String productName) {
        return productName != null ? QProduct.product.productName.contains(productName) : null;
    }

    private BooleanExpression companyIdEq(UUID companyId) {
        return companyId != null ? QProduct.product.companyId.eq(companyId) : null;
    }

    private BooleanExpression hubIdEq(UUID hubId) {
        return hubId != null ? QProduct.product.hubId.eq(hubId) : null;
    }

    private BooleanExpression isNotDeleted() {
        return QProduct.product.deleted.eq(false);
    }
}
