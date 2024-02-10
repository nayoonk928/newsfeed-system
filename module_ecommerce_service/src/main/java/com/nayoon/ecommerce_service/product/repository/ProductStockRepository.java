package com.nayoon.ecommerce_service.product.repository;

import com.nayoon.ecommerce_service.product.entity.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {

}
