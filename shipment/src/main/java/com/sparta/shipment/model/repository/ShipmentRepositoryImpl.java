package com.sparta.shipment.model.repository;


import static com.sparta.shipment.model.entity.QShipment.shipment;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.shipment.domain.dto.response.GetShipmentResponseDto;
import com.sparta.shipment.model.entity.QShipment;
import com.sparta.shipment.model.entity.Shipment;
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
public class ShipmentRepositoryImpl implements ShipmentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<GetShipmentResponseDto> searchShipments(String shipmentStatus, String receiverName,
                                                        String shippingAddress, UUID hubId, UUID shipmentManagerId,
                                                        Pageable pageable) {
        Pageable validatedPageable = validatePageable(pageable);

        // 1️⃣ count 쿼리 (transform으로 count 쿼리 최적화)
        long total = Optional.ofNullable(queryFactory
                .select(shipment.shipmentId.count())
                .from(QShipment.shipment)
                .where(
                        shipmentStatusValid(shipmentStatus),
                        addressContains(shippingAddress),
                        usernameContains(receiverName),
                        hubIdEq(hubId),
                        shipmentManagerIdEq(shipmentManagerId),
                        isNotDeleted()
                )
                .fetchOne()).orElse(0L);
        // ✅ 카운트 쿼리는 단일 결과만 가져옵니다.

        // 2️⃣ 페이징 데이터 조회 (offset/limit 추가)
        List<Shipment> results = queryFactory
                .selectFrom(QShipment.shipment)
                .where(
                        shipmentStatusValid(shipmentStatus),
                        addressContains(shippingAddress),
                        usernameContains(receiverName),
                        hubIdEq(hubId),
                        shipmentManagerIdEq(shipmentManagerId),
                        isNotDeleted()
                )
                .orderBy(validatedPageable.getSort().stream()
                        .map(order -> {
                            if ("createdAt".equals(order.getProperty())) {
                                return order.isAscending()
                                        ? shipment.createdAt.asc()
                                        : shipment.createdAt.desc();
                            } else if ("lastModifiedAt".equals(order.getProperty())) {
                                return order.isAscending()
                                        ? shipment.lastModifiedAt.asc()
                                        : shipment.lastModifiedAt.desc();
                            } else {
                                throw new IllegalArgumentException("정렬 옵션이 잘못되었습니다: " + order.getProperty());
                            }
                        })
                        .toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 3️⃣ 조회한 데이터 DTO로 변환
        List<GetShipmentResponseDto> content = results.stream()
                .map(GetShipmentResponseDto::of)
                .collect(Collectors.toList());

        // 4️⃣ Page 객체로 반환
        return new PageImpl<>(content, validatedPageable, total);
    }

    private BooleanExpression addressContains(String address) {
        return address != null ? shipment.shippingAddress.containsIgnoreCase(address) : null;
    }

    private BooleanExpression usernameContains(String name) {
        return name != null ? shipment.receiverName.containsIgnoreCase(name) : null;
    }

    private BooleanExpression shipmentStatusValid(String shipmentStatus) {
        if (shipmentStatus != null) {
            return shipment.shipmentStatus.stringValue().equalsIgnoreCase(shipmentStatus);
        }
        return null;
    }

    private BooleanExpression shipmentManagerIdEq(UUID shipmentManagerId) {
        return shipmentManagerId != null ? shipment.shipmentManager.shipmentManagerId.eq(shipmentManagerId) : null;
    }

    private BooleanExpression hubIdEq(UUID hubId) {
        return hubId != null ? shipment.endHubId.eq(hubId) : null;
    }

    private BooleanExpression isNotDeleted() {
        return shipment.deleted.isFalse(); // deleted가 false인 값만 가져오기
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