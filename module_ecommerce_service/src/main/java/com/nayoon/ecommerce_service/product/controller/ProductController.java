package com.nayoon.ecommerce_service.product.controller;

import com.nayoon.ecommerce_service.product.dto.request.ProductCreateRequest;
import com.nayoon.ecommerce_service.product.dto.request.ProductUpdateRequest;
import com.nayoon.ecommerce_service.product.service.ProductService;
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
@RequestMapping("/api/v1/products")
public class ProductController {

  private final ProductService productService;

  /**
   * 상품 등록
   */
  @PostMapping
  public ResponseEntity<Void> create(
      @RequestHeader("X-USER-ID") String userId,
      @Valid @RequestBody ProductCreateRequest request
  ) {
    Long principalId = Long.valueOf(userId);
    Long productId = productService.create(principalId, request);
    return ResponseEntity.created(URI.create("api/v1/products/" + productId)).build();
  }

  /**
   * 상품 수정
   */
  @PatchMapping("/{id}")
  public ResponseEntity<Void> update(
      @RequestHeader("X-USER-ID") String userId,
      @PathVariable(name = "id") Long productId,
      @Valid @RequestBody ProductUpdateRequest request
  ) {
    Long principalId = Long.valueOf(userId);
    productService.update(principalId, productId, request);
    return ResponseEntity.ok().build();
  }

}
