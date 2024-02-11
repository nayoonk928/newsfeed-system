package com.nayoon.ecommerce_service.reservation_product.repository.impl;

import static com.nayoon.ecommerce_service.reservation_product.entity.QReservationProduct.reservationProduct;

import com.nayoon.ecommerce_service.reservation_product.entity.ReservationProduct;
import com.nayoon.ecommerce_service.reservation_product.repository.ReservationProductQRepository;
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
public class ReservationProductRepositoryImpl implements ReservationProductQRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<ReservationProduct> filterAllReservationProducts(Pageable pageable) {
    JPAQuery<ReservationProduct> query = queryFactory
        .selectFrom(reservationProduct)
        .where(reservationProduct.deletedAt.isNull()); // 삭제되지 않은 상품만 필터링

    List<OrderSpecifier> order = new ArrayList<>();
    pageable.getSort().stream().forEach(o -> {
      order.add(new OrderSpecifier(
          o.getDirection().isDescending() ? Order.DESC : Order.ASC,
          new PathBuilder(ReservationProduct.class, "reservationProduct").get(o.getProperty())));
    });

    query = query.offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(order.toArray(new OrderSpecifier[0]));

    List<ReservationProduct> result = query.fetch();
    return new PageImpl<>(result, pageable, result.size());
  }

}
