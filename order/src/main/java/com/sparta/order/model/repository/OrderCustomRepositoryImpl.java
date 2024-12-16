package com.sparta.order.model.repository;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.order.domain.dto.response.GetOrderRes;
import com.sparta.order.model.entity.Order;
import com.sparta.order.model.entity.QOrder;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class OrderCustomRepositoryImpl implements OrderCustomRepository {
  private final JPAQueryFactory queryFactory;

  public OrderCustomRepositoryImpl(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }
  @Override
  public Page<GetOrderRes> searchOrders(String username, UUID requestCompanyId,
      UUID receiveCompanyId,
      UUID productId, UUID shipmentId, Pageable pageable) {


    QOrder order = QOrder.order;
    JPQLQuery<Order> query = queryFactory.selectFrom(order);

    // username 검색 조건
    if (username != null && !username.isBlank()) {
      query = query.where(
          order.username.containsIgnoreCase(username)
      );
    }

    // UUID 검색 조건들
    if (requestCompanyId != null) {
      query = query.where(order.requestCompanyId.eq(requestCompanyId));
    }
    if (receiveCompanyId != null) {
      query = query.where(order.receiveCompanyId.eq(receiveCompanyId));
    }
    if (productId != null) {
      query = query.where(order.productId.eq(productId));
    }
    if (shipmentId != null) {
      query = query.where(order.shipmentId.eq(shipmentId));
    }

    // 페이징 처리
    List<Order> orderList = query.offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    long total = query.fetchCount();

    // 결과 매핑
    List<GetOrderRes> resList = orderList.stream()
        .map(o -> GetOrderRes.builder()
            .orderId(o.getOrderId())
            .username(o.getUsername())
            .requestCompanyId(o.getRequestCompanyId())
            .receiveCompanyId(o.getReceiveCompanyId())
            .productId(o.getProductId())
            .shipmentId(o.getShipmentId())
            .build())
        .collect(Collectors.toList());

    return new PageImpl<>(resList, pageable, total);
  }

}
