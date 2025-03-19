package com.dolphin.adminbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dolphin.adminbackend.model.jpa.Visitor;

import java.util.Optional;

public interface VisitorRepo extends JpaRepository<Visitor, Long> {
    Optional<Visitor> findByIp(String ip);
}

