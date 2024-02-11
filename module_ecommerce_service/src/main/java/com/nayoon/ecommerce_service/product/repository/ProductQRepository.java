package com.nayoon.ecommerce_service.product.repository;

import com.nayoon.ecommerce_service.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductQRepository {

  Page<Product> filterAllProducts(Pageable pageable);

}
