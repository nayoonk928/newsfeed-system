package com.nayoon.ecommerce_service.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ProductCreateRequest(
    @NotBlank
    String name,
    @NotBlank
    String content,
    @NotNull
    Long price,
    @NotNull
    Integer count,
    @NotBlank
    Boolean isReserved,
    LocalDateTime reservedAt
) {

}
