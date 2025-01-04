package com.dolphin.adminbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dolphin.adminbackend.model.jpa.Category;

public interface CategoryRepo extends JpaRepository<Category, Long> {
    // Custom query methods if needed
}
