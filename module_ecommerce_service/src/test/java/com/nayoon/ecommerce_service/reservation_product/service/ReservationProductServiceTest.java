package com.nayoon.ecommerce_service.reservation_product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nayoon.ecommerce_service.reservation_product.dto.request.ReservationProductCreateRequest;
import com.nayoon.ecommerce_service.reservation_product.dto.request.ReservationProductUpdateRequest;
import com.nayoon.ecommerce_service.reservation_product.entity.ReservationProduct;
import com.nayoon.ecommerce_service.reservation_product.entity.ReservationProductStock;
import com.nayoon.ecommerce_service.reservation_product.repository.ReservationProductRepository;
import com.nayoon.ecommerce_service.reservation_product.repository.ReservationProductStockRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        return ReservationProductStock.builder().productId(product.getId()).count(request.count()).build();
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
        .count(100)
        .reservedAt(LocalDateTime.now())
        .build();
  }

  private ReservationProductUpdateRequest mockProductUpdateRequest() {
    return ReservationProductUpdateRequest.builder()
        .content("테스트 상품1 새 내용")
        .build();
  }

}