package com.nayoon.ecommerce_service.reservation_product.controller;

import com.nayoon.ecommerce_service.reservation_product.dto.request.ReservationProductCreateRequest;
import com.nayoon.ecommerce_service.reservation_product.dto.request.ReservationProductUpdateRequest;
import com.nayoon.ecommerce_service.reservation_product.service.ReservationProductService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation-products")
public class ReservationProductController {

  private final ReservationProductService reservationProductService;

  /**
   * 예약 상품 등록
   */
  @PostMapping
  public ResponseEntity<Void> create(
      @RequestHeader("X-USER-ID") String userId,
      @Valid @RequestBody ReservationProductCreateRequest request
  ) {
    Long principalId = Long.valueOf(userId);
    Long productId = reservationProductService.create(principalId, request);
    return ResponseEntity.created(URI.create("api/v1/products/" + productId)).build();
  }

  /**
   * 예약 상품 수정
   */
  @PatchMapping("/{id}")
  public ResponseEntity<Void> update(
      @RequestHeader("X-USER-ID") String userId,
      @PathVariable(name = "id") Long productId,
      @Valid @RequestBody ReservationProductUpdateRequest request
  ) {
    Long principalId = Long.valueOf(userId);
    reservationProductService.update(principalId, productId, request);
    return ResponseEntity.ok().build();
  }

}
