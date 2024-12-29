package com.dolphin.adminbackend.repository;

import com.dolphin.adminbackend.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<Category, Long> {
    // Custom query methods if needed
}
