package com.nayoon.ecommerce_service.reservation_product.dto.response;

import com.nayoon.ecommerce_service.reservation_product.entity.ReservationProductStock;
import lombok.Builder;

@Builder
public record ReservationProductStockResponse(
    Long productId,
    Integer stock
) {

  public static ReservationProductStockResponse from(ReservationProductStock productStock) {
    return ReservationProductStockResponse.builder()
        .productId(productStock.getProductId())
        .stock(productStock.getStock())
        .build();
  }

}
