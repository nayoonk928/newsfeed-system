package com.nayoon.ecommerce_service.product.service;

import com.nayoon.ecommerce_service.common.exception.CustomException;
import com.nayoon.ecommerce_service.common.exception.ErrorCode;
import com.nayoon.ecommerce_service.product.dto.request.ProductCreateRequest;
import com.nayoon.ecommerce_service.product.entity.Product;
import com.nayoon.ecommerce_service.product.entity.ProductStock;
import com.nayoon.ecommerce_service.product.repository.ProductRepository;
import com.nayoon.ecommerce_service.product.repository.ProductStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductStockRepository productStockRepository;

  /**
   * 상품 등록
   */
  @Transactional
  public Long create(ProductCreateRequest request) {
    if (request.isReserved() && request.reservedAt() == null) {
      throw new CustomException(ErrorCode.REQUIRED_RESERVED_AT);
    }

    Product product = Product.builder()
        .name(request.name())
        .content(request.content())
        .price(request.price())
        .isReserved(request.isReserved())
        .reservedAt(request.reservedAt())
        .build();

    Product saved = productRepository.save(product);
    ProductStock productStock = ProductStock.builder()
        .productId(saved.getId())
        .count(request.count())
        .build();

    productStockRepository.save(productStock);

    return saved.getId();
  }

}
