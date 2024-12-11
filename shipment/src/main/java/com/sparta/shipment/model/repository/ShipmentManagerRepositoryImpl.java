package com.sparta.shipment.model.repository;


import static com.sparta.shipment.model.entity.QShipmentManager.shipmentManager;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.shipment.domain.dto.GetShipmentManagerResponseDto;
import com.sparta.shipment.domain.dto.ShipmentManagerSearchDto;
import com.sparta.shipment.model.entity.ShipmentManager;
import java.util.ArrayList;
import java.util.List;
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

        QueryResults<ShipmentManager> results = queryFactory
                .selectFrom(shipmentManager)
                .where(
                        usernameContains(searchDto.getUsername()),
                        managerTypeIsValid(searchDto.getManagerType()),
                        isNotDeleted()
                )
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<GetShipmentManagerResponseDto> content = results.getResults().stream()
                .map(GetShipmentManagerResponseDto::of)
                .collect(Collectors.toList());
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression usernameContains(String name) {
        return name != null ? shipmentManager.username.containsIgnoreCase(name) : null;
    }

    private BooleanExpression managerTypeIsValid(String managerType) {
        if (managerType != null) {
            return shipmentManager.managerType.stringValue().toUpperCase().in(managerType);
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