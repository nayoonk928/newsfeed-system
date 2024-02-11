package com.nayoon.ecommerce_service.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nayoon.ecommerce_service.product.dto.request.ProductCreateRequest;
import com.nayoon.ecommerce_service.product.dto.request.ProductUpdateRequest;
import com.nayoon.ecommerce_service.product.entity.Product;
import com.nayoon.ecommerce_service.product.entity.ProductStock;
import com.nayoon.ecommerce_service.product.repository.ProductRepository;
import com.nayoon.ecommerce_service.product.repository.ProductStockRepository;
import java.util.Optional;
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

      when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
        return Product.builder().name(request.name()).content(request.content()).price(request.price()).build();
      });
      when(productStockRepository.save(any(ProductStock.class))).thenAnswer(invocation -> {
        return ProductStock.builder().productId(product.getId()).stock(request.stock()).build();
      });

      //when
      Long productId = productService.create(product.getId(), request);

      //then
      assertEquals(productId, product.getId());
      verify(productRepository, times(1)).save(any(Product.class));
      verify(productStockRepository, times(1)).save(any(ProductStock.class));
    }

  }

  @Nested
  @DisplayName("상품 수정")
  class update {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      Long principalId = 1L;
      ProductUpdateRequest request = mockProductUpdateRequest();
      Product product = mockProduct();
      productRepository.save(product);

      ProductStock productStock = mock(ProductStock.class);
      productStockRepository.save(productStock);

      when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
      when(productStockRepository.findById(product.getId())).thenReturn(Optional.of(productStock));

      //when
      productService.update(principalId, product.getId(), request);

      //then
      verify(productRepository, times(1)).save(any(Product.class));
      verify(productStockRepository, times(1)).save(any(ProductStock.class));
    }

  }

  private Product mockProduct() {
    return Product.builder()
        .userId(1L)
        .name("테스트 상품1")
        .content("테스트 상품1 내용")
        .price(10000L)
        .build();
  }

  private ProductCreateRequest mockProductCreateRequest() {
    return ProductCreateRequest.builder()
        .name("테스트 상품1")
        .content("테스트 상품1 내용")
        .price(10000L)
        .stock(100)
        .build();
  }

  private ProductUpdateRequest mockProductUpdateRequest() {
    return ProductUpdateRequest.builder()
        .content("테스트 상품1 새 내용")
        .build();
  }

}