package com.nayoon.ecommerce_service.product.dto.request;

import lombok.Builder;

@Builder
public record ProductUpdateRequest(
    String name,
    String content,
    Long price,
    Integer count
) {

}
