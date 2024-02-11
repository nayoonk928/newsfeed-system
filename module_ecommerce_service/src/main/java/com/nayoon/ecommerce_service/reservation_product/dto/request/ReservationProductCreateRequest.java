package com.nayoon.ecommerce_service.reservation_product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ReservationProductCreateRequest(
    @NotBlank
    String name,
    @NotBlank
    String content,
    @NotNull
    Long price,
    @NotNull
    Integer stock,
    @NotNull
    LocalDateTime reservedAt
) {

}
