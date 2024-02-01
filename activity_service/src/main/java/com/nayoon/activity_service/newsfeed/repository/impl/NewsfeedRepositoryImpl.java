package com.nayoon.activity_service.newsfeed.repository.impl;

import static com.nayoon.activity_service.newsfeed.entity.QNewsfeed.newsfeed;

import com.nayoon.activity_service.newsfeed.entity.Newsfeed;
import com.nayoon.activity_service.newsfeed.repository.NewsfeedQRepository;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class NewsfeedRepositoryImpl implements NewsfeedQRepository {

  private final JPAQueryFactory queryFactory;

  // TODO: No-offset 방식과 비교해보기
  // 팔로우하고 있는 사람들의 활동 반환
  @Override
  public Page<Newsfeed> filterNewsfeeds(Long userId, List<Long> followingIds, Pageable pageable) {
    JPAQuery<Newsfeed> query = queryFactory
        .selectFrom(newsfeed)
        .where(newsfeed.actionUserId.in(followingIds)
            .or(newsfeed.relatedUserId.eq(userId)));

    List<OrderSpecifier> order = new ArrayList<>();
    pageable.getSort().stream().forEach(o -> {
      order.add(new OrderSpecifier(
          o.getDirection().isDescending() ? Order.DESC : Order.ASC,
          new PathBuilder(Newsfeed.class, "newsfeed").get(o.getProperty())));
    });

    query = query.offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(order.toArray(new OrderSpecifier[0]));

    List<Newsfeed> result = query.fetch();
    return new PageImpl<>(result, pageable, result.size());
  }

}
