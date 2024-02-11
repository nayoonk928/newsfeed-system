package com.nayoon.ecommerce_service.product.dto.response;

import com.nayoon.ecommerce_service.product.entity.Product;
import lombok.Builder;

@Builder
public record ProductResponse(
    String name,
    String content,
    Long price
) {

  public static ProductResponse from(Product product) {
    return ProductResponse.builder()
        .name(product.getName())
        .content(product.getContent())
        .price(product.getPrice())
        .build();
  }

}
