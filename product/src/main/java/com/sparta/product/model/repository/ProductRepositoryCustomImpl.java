package com.sparta.product.model.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.product.domain.dto.response.ProductResponseDto;
import com.sparta.product.domain.dto.response.QProductResponseDto;
import com.sparta.product.model.entity.QProduct;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public ProductRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<ProductResponseDto> searchProducts(String productName, UUID hubId, UUID companyId, Pageable pageable) {
        Pageable validatedPageable = validatePageable(pageable);

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
                .orderBy(validatedPageable.getSort().stream()
                        .map(order -> {
                            if ("createdAt".equals(order.getProperty())) {
                                return order.isAscending()
                                        ? product.createdAt.asc()
                                        : product.createdAt.desc();
                            } else if ("lastModifiedAt".equals(order.getProperty())) {
                                return order.isAscending()
                                        ? product.lastModifiedAt.asc()
                                        : product.lastModifiedAt.desc();
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

        return PageableExecutionUtils.getPage(content, validatedPageable, () -> total);
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

    private Pageable validatePageable(Pageable pageable) {
        int size = pageable.getPageSize();
        if (size != 10 && size != 30 && size != 50) {
            // 유효하지 않은 경우 기본값 10으로 수정
            return PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
        }
        return pageable;
    }
}
