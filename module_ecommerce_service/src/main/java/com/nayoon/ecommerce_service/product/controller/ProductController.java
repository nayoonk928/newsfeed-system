package com.nayoon.ecommerce_service.product.controller;

import com.nayoon.ecommerce_service.product.dto.request.ProductCreateRequest;
import com.nayoon.ecommerce_service.product.service.ProductService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
      @Valid @RequestBody ProductCreateRequest request
  ) {
    Long productId = productService.create(request);
    return ResponseEntity.created(URI.create("api/v1/products/" + productId)).build();
  }

}
