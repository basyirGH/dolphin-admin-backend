package com.dolphin.adminbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dolphin.adminbackend.model.jpa.Payment;

public interface PaymentRepo extends JpaRepository<Payment, Long> {
    // Custom query methods if needed
}
