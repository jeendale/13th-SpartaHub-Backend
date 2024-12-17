package com.sparta.shipment.model.repository;


import static com.sparta.shipment.model.entity.QShipmentRoute.shipmentRoute;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.shipment.domain.dto.response.GetShipmentRouteResponseDto;
import com.sparta.shipment.model.entity.QShipmentRoute;
import com.sparta.shipment.model.entity.ShipmentRoute;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class ShipmentRouteRepositoryImpl implements ShipmentRouteRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<GetShipmentRouteResponseDto> searchShipmentRoutes(String shipmentStatus, UUID hubId,
                                                                  UUID shipmentManagerId,
                                                                  Pageable pageable) {
        Pageable validatedPageable = validatePageable(pageable);

        // 1️⃣ count 쿼리 (transform으로 count 쿼리 최적화)
        long total = Optional.ofNullable(queryFactory
                .select(shipmentRoute.shipmentRouteId.count())
                .from(QShipmentRoute.shipmentRoute)
                .where(
                        shipmentStatusValid(shipmentStatus),
                        hubIdEq(hubId),
                        shipmentManagerIdEq(shipmentManagerId),
                        isNotDeleted()
                )
                .fetchOne()).orElse(0L);
        // ✅ 카운트 쿼리는 단일 결과만 가져옵니다.

        // 2️⃣ 페이징 데이터 조회 (offset/limit 추가)
        List<ShipmentRoute> results = queryFactory
                .selectFrom(QShipmentRoute.shipmentRoute)
                .where(
                        shipmentStatusValid(shipmentStatus),
                        hubIdEq(hubId),
                        shipmentManagerIdEq(shipmentManagerId),
                        isNotDeleted()
                )
                .orderBy(validatedPageable.getSort().stream()
                        .map(order -> {
                            if ("createdAt".equals(order.getProperty())) {
                                return order.isAscending()
                                        ? shipmentRoute.createdAt.asc()
                                        : shipmentRoute.createdAt.desc();
                            } else if ("lastModifiedAt".equals(order.getProperty())) {
                                return order.isAscending()
                                        ? shipmentRoute.lastModifiedAt.asc()
                                        : shipmentRoute.lastModifiedAt.desc();
                            } else {
                                throw new IllegalArgumentException("정렬 옵션이 잘못되었습니다: " + order.getProperty());
                            }
                        })
                        .toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 3️⃣ 조회한 데이터 DTO로 변환
        List<GetShipmentRouteResponseDto> content = results.stream()
                .map(GetShipmentRouteResponseDto::of)
                .collect(Collectors.toList());

        // 4️⃣ Page 객체로 반환
        return new PageImpl<>(content, validatedPageable, total);
    }


    private BooleanExpression shipmentStatusValid(String shipmentStatus) {
        if (shipmentStatus != null) {
            return shipmentRoute.shipmentStatus.stringValue().equalsIgnoreCase(shipmentStatus);
        }
        return null;
    }

    private BooleanExpression shipmentManagerIdEq(UUID shipmentManagerId) {
        return shipmentManagerId != null ? shipmentRoute.shipmentManager.shipmentManagerId.eq(shipmentManagerId) : null;
    }

    private BooleanExpression hubIdEq(UUID hubId) {
        return hubId != null ? shipmentRoute.startHubId.eq(hubId) : null;
    }

    private BooleanExpression isNotDeleted() {
        return shipmentRoute.deleted.isFalse(); // deleted가 false인 값만 가져오기
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