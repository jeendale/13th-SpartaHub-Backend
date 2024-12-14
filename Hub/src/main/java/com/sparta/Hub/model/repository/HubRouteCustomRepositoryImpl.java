package com.sparta.Hub.model.repository;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.Hub.domain.dto.response.GetHubRouteInfoRes;
import com.sparta.Hub.model.entity.HubRoute;
import com.sparta.Hub.model.entity.QHubRoute;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class HubRouteCustomRepositoryImpl implements HubRouteCustomRepository {

  private final JPAQueryFactory queryFactory;

  public HubRouteCustomRepositoryImpl(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public Page<GetHubRouteInfoRes> searchHubRoutes(String keyword, Pageable pageable) {
    QHubRoute hubRoute = QHubRoute.hubRoute;

    JPQLQuery<HubRoute> query;
    if (keyword == null || keyword.isBlank()) {
      query = queryFactory.selectFrom(hubRoute); // 조건 없이 전체 검색
    } else {
      query = queryFactory.selectFrom(hubRoute)
          .where(
              hubRoute.startHubName.containsIgnoreCase(keyword)
                  .or(hubRoute.endHubName.containsIgnoreCase(keyword))
          );
    }

    List<HubRoute> hubRouteList=query.offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    long total = query.fetchCount();

    List<GetHubRouteInfoRes> infoResList= hubRouteList.stream()
        .map(h->GetHubRouteInfoRes.builder()
            .hubRouteId(h.getHubId())
            .startHubName(h.getStartHubName())
            .endHubName(h.getEndHubName())
            .deliveryTime(h.getDeliveryTime())
            .distance(h.getDistance())
            .build())
        .collect(Collectors.toList());

        return new PageImpl<>(infoResList, pageable, total);

  }

}
