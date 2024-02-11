package com.nayoon.ecommerce_service.reservation_product.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationProductService {

  private final ReservationProductRepository reservationProductRepository;
  private final ReservationProductStockRepository reservationProductStockRepository;

  /**
   * 상품 등록
   */
  @Transactional
  public Long create(Long principalId, ReservationProductCreateRequest request) {
    ReservationProduct reservationProduct = ReservationProduct.builder()
        .userId(principalId)
        .name(request.name())
        .content(request.content())
        .price(request.price())
        .reservedAt(request.reservedAt())
        .build();

    ReservationProduct saved = reservationProductRepository.save(reservationProduct);
    ReservationProductStock reservationProductStock = ReservationProductStock.builder()
        .productId(saved.getId())
        .stock(request.stock())
        .build();

    reservationProductStockRepository.save(reservationProductStock);

    return saved.getId();
  }

  /**
   * 상품 수정
   */
  @Transactional
  public void update(Long principalId, Long productId, ReservationProductUpdateRequest request) {
    ReservationProduct reservationProduct = reservationProductRepository.findById(productId)
        .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

    checkProductOwner(principalId, reservationProduct);

    ReservationProductStock reservationProductStock = reservationProductStockRepository.findById(productId)
        .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_STOCK_NOT_FOUND));

    reservationProduct.update(request.name(), request.content(), request.price(), request.reservedAt());
    reservationProductStock.update(request.stock());
  }

  private void checkProductOwner(Long principalId, ReservationProduct reservationProduct) {
    if (!principalId.equals(reservationProduct.getUserId())) {
      throw new CustomException(ErrorCode.INVALID_REQUEST);
    }
  }

  /**
   * 상품 상세정보 조회
   */
  @Transactional
  public ReservationProductResponse getProductInfoById(Long productId) {
    ReservationProduct product = reservationProductRepository.findById(productId)
        .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

    return ReservationProductResponse.builder()
        .name(product.getName())
        .content(product.getContent())
        .price(product.getPrice())
        .build();
  }

  /**
   * 전체 상품 조회
   */
  public Page<ReservationProduct> getAllProducts(Pageable pageable) {
    return reservationProductRepository.filterAllReservationProducts(pageable);
  }

  /**
   * 상품 재고 조회
   */
  public ReservationProductStockResponse getProductStockById(Long productId) {
    ReservationProductStock productStock = reservationProductStockRepository.findById(productId)
        .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_STOCK_NOT_FOUND));

    return ReservationProductStockResponse.builder()
        .productId(productStock.getProductId())
        .stock(productStock.getStock())
        .build();
  }

}
