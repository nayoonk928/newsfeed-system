package com.nayoon.ecommerce_service.reservation_product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nayoon.ecommerce_service.common.exception.CustomException;
import com.nayoon.ecommerce_service.common.exception.ErrorCode;
import com.nayoon.ecommerce_service.reservation_product.dto.request.ReservationProductCreateRequest;
import com.nayoon.ecommerce_service.reservation_product.dto.request.ReservationProductUpdateRequest;
import com.nayoon.ecommerce_service.reservation_product.dto.response.ReservationProductResponse;
import com.nayoon.ecommerce_service.reservation_product.dto.response.ReservationProductStockResponse;
import com.nayoon.ecommerce_service.reservation_product.entity.ReservationProduct;
import com.nayoon.ecommerce_service.reservation_product.entity.ReservationProductStock;
import com.nayoon.ecommerce_service.reservation_product.repository.ReservationProductRepository;
import com.nayoon.ecommerce_service.reservation_product.repository.ReservationProductStockRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ReservationProductServiceTest {

  @InjectMocks
  private ReservationProductService reservationProductService;

  @Mock
  private ReservationProductRepository reservationProductRepository;

  @Mock
  private ReservationProductStockRepository reservationProductStockRepository;

  @Nested
  @DisplayName("상품 등록")
  class create {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      ReservationProductCreateRequest request = mockProductCreateRequest();
      ReservationProduct product = mockProduct(request.reservedAt());

      when(reservationProductRepository.save(any(ReservationProduct.class))).thenAnswer(invocation -> {
        return product;
      });
      when(reservationProductStockRepository.save(any(ReservationProductStock.class))).thenAnswer(invocation -> {
        return ReservationProductStock.builder().productId(product.getId()).stock(request.stock()).build();
      });

      //when
      Long productId = reservationProductService.create(product.getId(), request);

      //then
      assertEquals(productId, product.getId());
      verify(reservationProductRepository, times(1)).save(any(ReservationProduct.class));
      verify(reservationProductStockRepository, times(1)).save(any(ReservationProductStock.class));
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
      ReservationProductUpdateRequest request = mockProductUpdateRequest();
      ReservationProduct product = mockProduct(request.reservedAt());
      reservationProductRepository.save(product);

      ReservationProductStock productStock = mock(ReservationProductStock.class);
      reservationProductStockRepository.save(productStock);

      when(reservationProductRepository.findById(product.getId())).thenReturn(Optional.of(product));
      when(reservationProductStockRepository.findById(product.getId())).thenReturn(Optional.of(productStock));

      //when
      reservationProductService.update(principalId, product.getId(), request);

      //then
      verify(reservationProductRepository, times(1)).save(any(ReservationProduct.class));
      verify(reservationProductStockRepository, times(1)).save(any(ReservationProductStock.class));
    }

  }

  @Nested
  @DisplayName("전체 상품 조회")
  class getAllProducts {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      Pageable pageable = Pageable.ofSize(10).withPage(0);

      // Mock data for products
      List<ReservationProduct> products = Arrays.asList(
          mockProduct(LocalDateTime.now()),
          mockProduct2(LocalDateTime.now())
      );
      when(reservationProductRepository.filterAllReservationProducts(eq(pageable))).thenReturn(new PageImpl<>(products));

      //when
      Page<ReservationProduct> result = reservationProductService.getAllProducts(pageable);

      //then
      assertEquals(products, result.getContent());
    }

  }

  @Nested
  @DisplayName("상품 재고 조회")
  class getProductStockById {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      Long productId = 1L;
      ReservationProductStock productStock = new ReservationProductStock(productId, 100);

      when(reservationProductStockRepository.findById(productId)).thenReturn(Optional.of(productStock));

      //when
      ReservationProductStockResponse response = reservationProductService.getProductStockById(productId);

      //then
      assertEquals(productId, response.productId());
      assertEquals(productStock.getStock(), response.stock());
    }

    @Test
    @DisplayName("실패: 재고 정보 찾을 수 없음")
    void productStockNotFound() {
      //given
      Long productId = 1L;

      //when
      CustomException exception = assertThrows(CustomException.class, ()
          -> reservationProductService.getProductStockById(productId));

      //then
      assertEquals(ErrorCode.PRODUCT_STOCK_NOT_FOUND, exception.getErrorCode());
    }

  }

  @Nested
  @DisplayName("상품 상세정보 조회")
  class getProductInfoById {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      Long productId = 1L;
      ReservationProduct product = mockProduct(LocalDateTime.now());

      when(reservationProductRepository.findById(productId)).thenReturn(Optional.of(product));

      //when
      ReservationProductResponse response = reservationProductService.getProductInfoById(productId);

      //then
      assertEquals(product.getName(), response.name());
      assertEquals(product.getContent(), response.content());
      assertEquals(product.getPrice(), response.price());
    }

    @Test
    @DisplayName("실패: 상품 정보 찾을 수 없음")
    void productNotFound() {
      //given
      Long productId = 1L;

      //when
      CustomException exception = assertThrows(CustomException.class, ()
          -> reservationProductService.getProductInfoById(productId));

      //then
      assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
    }

  }

  private ReservationProduct mockProduct2(LocalDateTime now) {
    return ReservationProduct.builder()
        .userId(1L)
        .name("테스트 상품2")
        .content("테스트 상품2 내용")
        .price(20000L)
        .reservedAt(now)
        .build();
  }

  private ReservationProduct mockProduct(LocalDateTime now) {
    return ReservationProduct.builder()
        .userId(1L)
        .name("테스트 상품1")
        .content("테스트 상품1 내용")
        .price(10000L)
        .reservedAt(now)
        .build();
  }

  private ReservationProductCreateRequest mockProductCreateRequest() {
    return ReservationProductCreateRequest.builder()
        .name("테스트 상품1")
        .content("테스트 상품1 내용")
        .price(10000L)
        .stock(100)
        .reservedAt(LocalDateTime.now())
        .build();
  }

  private ReservationProductUpdateRequest mockProductUpdateRequest() {
    return ReservationProductUpdateRequest.builder()
        .content("테스트 상품1 새 내용")
        .build();
  }

}