package com.dolphin.adminbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dolphin.adminbackend.model.jpa.Customer;

public interface CustomerRepo extends JpaRepository<Customer, Long> {
    // Custom query methods if needed
}
