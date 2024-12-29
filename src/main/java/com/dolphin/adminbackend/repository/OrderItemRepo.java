package com.dolphin.adminbackend.repository;

import com.dolphin.adminbackend.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {
    // Custom query methods if needed
}

