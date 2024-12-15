package com.sparta.Hub.model.repository;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.Hub.domain.dto.response.GetHubInfoRes;
import com.sparta.Hub.model.entity.Hub;
import com.sparta.Hub.model.entity.QHub;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class HubCustomRepositoryImpl implements HubCustomRepository {

  private final JPAQueryFactory queryFactory;

  public HubCustomRepositoryImpl(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public Page<GetHubInfoRes> searchHubs(String keyword, Pageable pageable) {
    QHub hub = QHub.hub;


    JPQLQuery<Hub> query;
    if (keyword == null || keyword.isBlank()) {
      query = queryFactory.selectFrom(hub); // 조건 없이 전체 검색
    } else {
      query = queryFactory.selectFrom(hub)
          .where(
              hub.hubname.containsIgnoreCase(keyword)
                  .or(hub.address.containsIgnoreCase(keyword))
          );
    }


    List<Hub> hubList = query.offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();


    long total = query.fetchCount();

    List<GetHubInfoRes> dtoList = hubList.stream()
        .map(h -> GetHubInfoRes.builder()
            .hubId(h.getHubId())
            .hubName(h.getHubname())
            .address(h.getAddress())
            .lati(h.getLati())
            .longti(h.getLongti())
            .build())
        .collect(Collectors.toList());

    return new PageImpl<>(dtoList, pageable, total);
  }

}
