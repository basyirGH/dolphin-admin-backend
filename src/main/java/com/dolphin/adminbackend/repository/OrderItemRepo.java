package com.dolphin.adminbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dolphin.adminbackend.model.OrderItem;

public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {
    // Custom query methods if needed
}

