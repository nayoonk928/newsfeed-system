package com.nayoon.ecommerce_service.product.dto.request;

import java.time.LocalDateTime;

public record ProductUpdateRequest(
    String name,
    String content,
    Long price,
    Integer count,
    Boolean isReserved,
    LocalDateTime reservedAt
) {

}
