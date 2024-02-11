package com.nayoon.ecommerce_service.reservation_product.dto.request;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ReservationProductUpdateRequest(
    String name,
    String content,
    Long price,
    Integer count,
    LocalDateTime reservedAt
) {

}
