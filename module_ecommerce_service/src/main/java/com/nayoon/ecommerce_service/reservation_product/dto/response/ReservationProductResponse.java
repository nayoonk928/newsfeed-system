package com.nayoon.ecommerce_service.reservation_product.dto.response;

import com.nayoon.ecommerce_service.reservation_product.entity.ReservationProduct;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ReservationProductResponse(
    String name,
    String content,
    Long price,
    LocalDateTime reservedAt
) {

  public static ReservationProductResponse from(ReservationProduct product) {
    return ReservationProductResponse.builder()
        .name(product.getName())
        .content(product.getContent())
        .price(product.getPrice())
        .reservedAt(product.getReservedAt())
        .build();
  }

}
