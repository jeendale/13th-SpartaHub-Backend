package com.sparta.Hub.model.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.core.types.OrderSpecifier;
import com.sparta.Hub.domain.dto.response.GetHubRouteInfoRes;
import com.sparta.Hub.model.entity.HubRoute;
import com.sparta.Hub.model.entity.QHubRoute;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class HubRouteCustomRepositoryImpl implements HubRouteCustomRepository {

  private final JPAQueryFactory queryFactory;

  public HubRouteCustomRepositoryImpl(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public Page<GetHubRouteInfoRes> searchHubRoutes(String keyword, Pageable pageable) {
    QHubRoute hubRoute = QHubRoute.hubRoute;


    Pageable validatedPageable = validatePageable(pageable);


    JPQLQuery<HubRoute> query;
    if (keyword == null || keyword.isBlank()) {
      query = queryFactory.selectFrom(hubRoute);
    } else {
      query = queryFactory.selectFrom(hubRoute)
          .where(
              hubRoute.startHubName.containsIgnoreCase(keyword)
                  .or(hubRoute.endHubName.containsIgnoreCase(keyword))
          );
    }

    query = applySorting(query, validatedPageable);

    List<HubRoute> hubRouteList = query.offset(validatedPageable.getOffset())
        .limit(validatedPageable.getPageSize())
        .fetch();

    long total = query.fetchCount();

    List<GetHubRouteInfoRes> infoResList = hubRouteList.stream()
        .map(h -> GetHubRouteInfoRes.builder()
            .hubRouteId(h.getHubId())
            .startHubId(h.getStartHub().getHubId())
            .endHubId(h.getEndHub().getHubId())
            .startHubName(h.getStartHubName())
            .endHubName(h.getEndHubName())
            .deliveryTime(h.getDeliveryTime())
            .distance(h.getDistance())
            .build())
        .collect(Collectors.toList());

    return new PageImpl<>(infoResList, validatedPageable, total);
  }

  @Override
  public Page<GetHubRouteInfoRes> searchHubRoutesByHubIds(UUID startHubId, UUID endHubId, Pageable pageable) {
    QHubRoute hubRoute = QHubRoute.hubRoute;

    Pageable validatedPageable = validatePageable(pageable);

    JPQLQuery<HubRoute> query = queryFactory.selectFrom(hubRoute);

    if (startHubId != null && endHubId != null) {
      query = query.where(
          hubRoute.startHub.hubId.eq(startHubId)
              .and(hubRoute.endHub.hubId.eq(endHubId))
      );
    } else if (startHubId != null) {
      query = query.where(hubRoute.startHub.hubId.eq(startHubId));
    } else if (endHubId != null) {
      query = query.where(hubRoute.endHub.hubId.eq(endHubId));
    }

    query = applySorting(query, validatedPageable);


    List<HubRoute> hubRouteList = query.offset(validatedPageable.getOffset())
        .limit(validatedPageable.getPageSize())
        .fetch();

    long total = query.fetchCount();

    List<GetHubRouteInfoRes> infoResList = hubRouteList.stream()
        .map(h -> GetHubRouteInfoRes.builder()
            .hubRouteId(h.getHubId())
            .startHubId(h.getStartHub().getHubId())
            .endHubId(h.getEndHub().getHubId())
            .startHubName(h.getStartHubName())
            .endHubName(h.getEndHubName())
            .deliveryTime(h.getDeliveryTime())
            .distance(h.getDistance())
            .build())
        .collect(Collectors.toList());

    return new PageImpl<>(infoResList, validatedPageable, total);
  }

  private Pageable validatePageable(Pageable pageable) {
    int size = pageable.getPageSize();
    if (size != 10 && size != 30 && size != 50) {
      return PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
    }
    return pageable;
  }


  private JPQLQuery<HubRoute> applySorting(JPQLQuery<HubRoute> query, Pageable pageable) {
    return query.orderBy(pageable.getSort().stream()
        .map(order -> {
          if ("createdAt".equals(order.getProperty())) {
            return order.isAscending()
                ? QHubRoute.hubRoute.createdAt.asc()
                : QHubRoute.hubRoute.createdAt.desc();
          } else if ("lastModifiedAt".equals(order.getProperty())) {
            return order.isAscending()
                ? QHubRoute.hubRoute.lastModifiedAt.asc()
                : QHubRoute.hubRoute.lastModifiedAt.desc();
          } else {
            throw new IllegalArgumentException("정렬 옵션이 잘못되었습니다: " + order.getProperty());
          }
        })
        .toArray(OrderSpecifier[]::new));
  }
}
