package com.dolphin.adminbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dolphin.adminbackend.model.jpa.Order;

public interface OrderRepo extends JpaRepository<Order, Long> {
    // Custom query methods if needed
}
