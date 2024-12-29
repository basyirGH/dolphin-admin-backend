package com.dolphin.adminbackend.repository;

import com.dolphin.adminbackend.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product, Long> {
    // Custom query methods if needed
}
