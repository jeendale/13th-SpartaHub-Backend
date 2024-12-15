package com.sparta.shipment.model.repository;


import static com.sparta.shipment.model.entity.QShipmentManager.shipmentManager;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.shipment.domain.dto.ShipmentManagerSearchDto;
import com.sparta.shipment.domain.dto.response.GetShipmentManagerResponseDto;
import com.sparta.shipment.model.entity.QShipmentManager;
import com.sparta.shipment.model.entity.ShipmentManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public class ShipmentManagerRepositoryImpl implements ShipmentManagerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<GetShipmentManagerResponseDto> searchShipmentManagers(ShipmentManagerSearchDto searchDto,
                                                                      Pageable pageable) {
        List<OrderSpecifier<?>> orders = getAllOrderSpecifiers(pageable);

        // 1️⃣ count 쿼리 (transform으로 count 쿼리 최적화)
        long total = Optional.ofNullable(queryFactory
                .select(shipmentManager.shipmentManagerId.count())
                .from(QShipmentManager.shipmentManager)
                .where(
                        usernameContains(searchDto.username()),
                        managerTypeIsValid(searchDto.managerType()),
                        isNotDeleted()
                )
                .fetchOne()).orElse(0L);
        // ✅ 카운트 쿼리는 단일 결과만 가져옵니다.

        // 2️⃣ 페이징 데이터 조회 (offset/limit 추가)
        List<ShipmentManager> results = queryFactory
                .selectFrom(QShipmentManager.shipmentManager)
                .where(
                        usernameContains(searchDto.username()),
                        managerTypeIsValid(searchDto.managerType()),
                        isNotDeleted()
                )
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 3️⃣ 조회한 데이터 DTO로 변환
        List<GetShipmentManagerResponseDto> content = results.stream()
                .map(GetShipmentManagerResponseDto::of)
                .collect(Collectors.toList());

        // 4️⃣ Page 객체로 반환
        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression usernameContains(String name) {
        return name != null ? shipmentManager.username.containsIgnoreCase(name) : null;
    }

    private BooleanExpression managerTypeIsValid(String managerType) {
        if (managerType != null) {
            return shipmentManager.managerType.stringValue().equalsIgnoreCase(managerType);
        }
        return null;
    }

    private BooleanExpression isNotDeleted() {
        return shipmentManager.deleted.isFalse(); // deleted가 false인 값만 가져오기
    }

    private List<OrderSpecifier<?>> getAllOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        // 정렬 조건이 없는 경우 기본 정렬 추가
        if (pageable.getSort().isUnsorted()) {
            orders.add(new OrderSpecifier<>(com.querydsl.core.types.Order.DESC, shipmentManager.createdAt));
        } else {
            for (Sort.Order sortOrder : pageable.getSort()) {
                com.querydsl.core.types.Order direction = sortOrder.isAscending()
                        ? com.querydsl.core.types.Order.ASC
                        : com.querydsl.core.types.Order.DESC;

                switch (sortOrder.getProperty()) {
                    case "createdAt":
                        orders.add(new OrderSpecifier<>(direction, shipmentManager.createdAt));
                        break;
                    case "lastModifiedAt":
                        orders.add(new OrderSpecifier<>(direction, shipmentManager.lastModifiedAt));
                        break;
                    default:
                        orders.add(new OrderSpecifier<>(direction, shipmentManager.createdAt));
                        break;
                }
            }
        }
        return orders;
    }
}