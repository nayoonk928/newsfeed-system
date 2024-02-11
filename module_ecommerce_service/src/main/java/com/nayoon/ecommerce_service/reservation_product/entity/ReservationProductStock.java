package com.nayoon.ecommerce_service.reservation_product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "reservation_product_stock")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationProductStock {

  @Id
  @Column(name = "product_id", updatable = false)
  private Long productId;

  @Column(name = "stock", nullable = false)
  private Integer stock;

  @Builder
  public ReservationProductStock(Long productId, Integer stock) {
    this.productId = productId;
    this.stock = stock;
  }

  public void update(Integer stock) {
    this.stock = stock;
  }

}
