package com.sparta.Hub.model.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.Hub.domain.dto.response.GetHubInfoRes;
import com.sparta.Hub.model.entity.Hub;
import com.sparta.Hub.model.entity.QHub;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class HubCustomRepositoryImpl implements HubCustomRepository {

  private final JPAQueryFactory queryFactory;

  public HubCustomRepositoryImpl(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public Page<GetHubInfoRes> searchHubs(String keyword, Pageable pageable) {
    QHub hub = QHub.hub;

    Pageable validatedPageable = validatePageable(pageable);


    JPQLQuery<Hub> query = queryFactory.selectFrom(hub);

    if (keyword != null && !keyword.isBlank()) {
      query = query.where(
          hub.hubname.containsIgnoreCase(keyword)
              .or(hub.address.containsIgnoreCase(keyword))
              .or(hub.username.containsIgnoreCase(keyword))
      );
    }

    query = applySorting(query, validatedPageable);


    List<Hub> hubList = query.offset(validatedPageable.getOffset())
        .limit(validatedPageable.getPageSize())
        .fetch();


    long total = query.fetchCount();


    List<GetHubInfoRes> dtoList = hubList.stream()
        .map(h -> GetHubInfoRes.builder()
            .hubId(h.getHubId())
            .hubName(h.getHubname())
            .address(h.getAddress())
            .lati(h.getLati())
            .longti(h.getLongti())
            .username(h.getUsername())
            .build())
        .collect(Collectors.toList());

    return new PageImpl<>(dtoList, validatedPageable, total);
  }

  private Pageable validatePageable(Pageable pageable) {
    int size = pageable.getPageSize();
    if (size != 10 && size != 30 && size != 50) {
      return PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
    }
    return pageable;
  }

  private JPQLQuery<Hub> applySorting(JPQLQuery<Hub> query, Pageable pageable) {
    return query.orderBy(pageable.getSort().stream()
        .map(order -> {
          if ("createdAt".equals(order.getProperty())) {
            return order.isAscending()
                ? QHub.hub.createdAt.asc()
                : QHub.hub.createdAt.desc();
          } else if ("lastModifiedAt".equals(order.getProperty())) {
            return order.isAscending()
                ? QHub.hub.lastModifiedAt.asc()
                : QHub.hub.lastModifiedAt.desc();
          } else {
            throw new IllegalArgumentException("정렬 옵션이 잘못되었습니다: " + order.getProperty());
          }
        })
        .toArray(OrderSpecifier[]::new));
  }
}
