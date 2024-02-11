package com.nayoon.ecommerce_service.product.service;

import com.nayoon.ecommerce_service.common.exception.CustomException;
import com.nayoon.ecommerce_service.common.exception.ErrorCode;
import com.nayoon.ecommerce_service.product.dto.request.ProductCreateRequest;
import com.nayoon.ecommerce_service.product.dto.request.ProductUpdateRequest;
import com.nayoon.ecommerce_service.product.dto.response.ProductResponse;
import com.nayoon.ecommerce_service.product.dto.response.ProductStockResponse;
import com.nayoon.ecommerce_service.product.entity.Product;
import com.nayoon.ecommerce_service.product.entity.ProductStock;
import com.nayoon.ecommerce_service.product.repository.ProductRepository;
import com.nayoon.ecommerce_service.product.repository.ProductStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
  public Long create(Long principalId, ProductCreateRequest request) {
    Product product = Product.builder()
        .userId(principalId)
        .name(request.name())
        .content(request.content())
        .price(request.price())
        .build();

    Product saved = productRepository.save(product);
    ProductStock productStock = ProductStock.builder()
        .productId(saved.getId())
        .stock(request.stock())
        .build();

    productStockRepository.save(productStock);

    return saved.getId();
  }

  /**
   * 상품 수정
   */
  @Transactional
  public void update(Long principalId, Long productId, ProductUpdateRequest request) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

    ProductStock productStock = productStockRepository.findById(productId)
        .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_STOCK_NOT_FOUND));

    checkProductOwner(principalId, product);

    product.update(request.name(), request.content(), request.price());
    productStock.update(request.stock());
  }

  private void checkProductOwner(Long principalId, Product product) {
    if (!principalId.equals(product.getUserId())) {
      throw new CustomException(ErrorCode.INVALID_REQUEST);
    }
  }

  /**
   * 상품 상세정보 조회
   */
  @Transactional
  public ProductResponse getProductInfoById(Long productId) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

    return ProductResponse.builder()
        .name(product.getName())
        .content(product.getContent())
        .price(product.getPrice())
        .build();
  }

  /**
   * 전체 상품 조회
   */
  public Page<Product> getAllProducts(Pageable pageable) {
    return productRepository.filterAllProducts(pageable);
  }

  /**
   * 상품 재고 조회
   */
  public ProductStockResponse getProductStockById(Long productId) {
    ProductStock productStock = productStockRepository.findById(productId)
        .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_STOCK_NOT_FOUND));

    return ProductStockResponse.builder()
        .productId(productStock.getProductId())
        .stock(productStock.getStock())
        .build();
  }

}
