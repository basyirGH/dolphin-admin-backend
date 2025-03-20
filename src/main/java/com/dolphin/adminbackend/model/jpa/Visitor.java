package com.dolphin.adminbackend.model.jpa;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "visitor")
@Setter
@Getter
public class Visitor{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64, unique = true)
    private String ip;

    @Column(nullable = true)
    private LocalDateTime cooldownEndsAt;

    @Column
    private Boolean isCooldownActive;

    @Column
    private Integer simCount;
    
}
