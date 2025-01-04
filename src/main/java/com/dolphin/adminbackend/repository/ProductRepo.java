package com.dolphin.adminbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dolphin.adminbackend.model.jpa.Product;

public interface ProductRepo extends JpaRepository<Product, Long> {
    // Custom query methods if needed
}
