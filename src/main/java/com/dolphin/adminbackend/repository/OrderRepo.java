package com.dolphin.adminbackend.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dolphin.adminbackend.model.jpa.Order;

public interface OrderRepo extends JpaRepository<Order, Long> {
    // Custom query methods if needed
    @Query("SELECT SUM(o.totalAmount) FROM Order o")
    BigDecimal getSumOfTotalAmountAllOrders();
}
