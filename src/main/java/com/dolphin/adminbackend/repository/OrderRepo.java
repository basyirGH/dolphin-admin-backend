package com.dolphin.adminbackend.repository;

import com.dolphin.adminbackend.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, Long> {
    // Custom query methods if needed
}
