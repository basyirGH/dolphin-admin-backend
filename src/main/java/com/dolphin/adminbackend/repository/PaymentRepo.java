package com.dolphin.adminbackend.repository;

import com.dolphin.adminbackend.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepo extends JpaRepository<Payment, Long> {
    // Custom query methods if needed
}
