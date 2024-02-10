package com.nayoon.ecommerce_service.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "product_id", updatable = false)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "content", nullable = false)
  private String content;

  @Column(name = "price", nullable = false)
  private Long price;

  @Column(name = "is_reserved", nullable = false)
  private Boolean isReserved;

  @Column(name = "reserved_at")
  private LocalDateTime reservedAt;

  @Builder
  public Product(String name, String content, Long price, Boolean isReserved, LocalDateTime reservedAt) {
    this.name = name;
    this.content = content;
    this.price = price;
    this.isReserved = isReserved;
    this.reservedAt = reservedAt;
  }

  public void update() {

  }

}
