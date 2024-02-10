package com.nayoon.ecommerce_service.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nayoon.ecommerce_service.common.exception.CustomException;
import com.nayoon.ecommerce_service.common.exception.ErrorCode;
import com.nayoon.ecommerce_service.product.dto.request.ProductCreateRequest;
import com.nayoon.ecommerce_service.product.entity.Product;
import com.nayoon.ecommerce_service.product.entity.ProductStock;
import com.nayoon.ecommerce_service.product.repository.ProductRepository;
import com.nayoon.ecommerce_service.product.repository.ProductStockRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @InjectMocks
  private ProductService productService;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private ProductStockRepository productStockRepository;

  @Nested
  @DisplayName("상품 등록")
  class create {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      ProductCreateRequest request = mockProductCreateRequest();
      Product product = mockProduct();

      when(productRepository.save(any(Product.class))).thenReturn(product);

      //when
      productService.create(request);

      //then
      verify(productRepository, times(1)).save(any(Product.class));
      verify(productStockRepository, times(1)).save(any(ProductStock.class));
    }

    @Test
    @DisplayName("실패: 예약 상품 시간이 없음")
    void requiredReservedAt() {
      //given
      ProductCreateRequest request = mockInvalidProductCreateRequest();

      //when
      CustomException exception = assertThrows(CustomException.class, ()
          -> productService.create(request));

      //then
      assertEquals(ErrorCode.REQUIRED_RESERVED_AT, exception.getErrorCode());
    }

  }

  private ProductStock mockProductStock(Long productId) {
    return ProductStock.builder()
        .productId(productId)
        .count(100)
        .build();
  }

  private Product mockProduct() {
    return Product.builder()
        .name("테스트 상품1")
        .content("테스트 상품1 내용")
        .price(10000L)
        .isReserved(false)
        .reservedAt(null)
        .build();
  }

  private ProductCreateRequest mockInvalidProductCreateRequest() {
    return ProductCreateRequest.builder()
        .name("테스트 상품1")
        .content("테스트 상품1 내용")
        .price(10000L)
        .count(100)
        .isReserved(true)
        .reservedAt(null)
        .build();
  }

  private ProductCreateRequest mockProductCreateRequest() {
    return ProductCreateRequest.builder()
        .name("테스트 상품1")
        .content("테스트 상품1 내용")
        .price(10000L)
        .count(100)
        .isReserved(false)
        .reservedAt(null)
        .build();
  }

}