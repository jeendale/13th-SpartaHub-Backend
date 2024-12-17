package com.sparta.order.model.repository;

import com.querydsl.core.types.OrderSpecifier;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class OrderCustomRepositoryImpl implements OrderCustomRepository {
  private final JPAQueryFactory queryFactory;

  public OrderCustomRepositoryImpl(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }
  @Override
  public Page<GetOrderRes> searchOrders(String username, UUID requestCompanyId,
      UUID receiveCompanyId, UUID productId, UUID shipmentId, Pageable pageable) {

    QOrder order = QOrder.order;
    JPQLQuery<Order> query = queryFactory.selectFrom(order);

    query = query.where(order.deleted.eq(false));

    if (username != null && !username.isBlank()) {
      query = query.where(order.username.containsIgnoreCase(username));
    }


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

    Pageable validatedPageable = validatePageable(pageable);


    query = applySorting(query, validatedPageable);


    List<Order> orderList = query.offset(validatedPageable.getOffset())
        .limit(validatedPageable.getPageSize())
        .fetch();


    long total = query.fetchCount();


    List<GetOrderRes> resList = orderList.stream()
        .map(o -> GetOrderRes.builder()
            .orderId(o.getOrderId())
            .orderDate(o.getOrderDate())
            .request(o.getRequest())
            .quantity(o.getQuantity())
            .username(o.getUsername())
            .requestCompanyId(o.getRequestCompanyId())
            .receiveCompanyId(o.getReceiveCompanyId())
            .productId(o.getProductId())
            .shipmentId(o.getShipmentId())
            .build())
        .collect(Collectors.toList());

    return new PageImpl<>(resList, validatedPageable, total);
  }
  private Pageable validatePageable(Pageable pageable) {
    int size = pageable.getPageSize();
    if (size != 10 && size != 30 && size != 50) {
      return PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
    }
    return pageable;
  }
  private JPQLQuery<Order> applySorting(JPQLQuery<Order> query, Pageable pageable) {

    return query.orderBy(pageable.getSort().stream()
        .map(order -> {
          if ("createdAt".equals(order.getProperty())) {
            return order.isAscending()
                ? QOrder.order.createdAt.asc()
                : QOrder.order.createdAt.desc();
          } else if ("lastModifiedAt".equals(order.getProperty())) {
            return order.isAscending()
                ? QOrder.order.lastModifiedAt.asc()
                : QOrder.order.lastModifiedAt.desc();
          } else {
            throw new IllegalArgumentException("정렬 옵션이 잘못되었습니다: " + order.getProperty());
          }
        })
        .toArray(OrderSpecifier[]::new));
  }

}
