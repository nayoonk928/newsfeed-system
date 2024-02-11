package com.nayoon.ecommerce_service.product.repository;

import com.nayoon.ecommerce_service.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductQRepository {

}
