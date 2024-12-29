package com.dolphin.adminbackend.models;

import com.dolphin.adminbackend.constants.PaymentStatus; 
import jakarta.persistence.*; 
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // e.g., SUCCESS, FAILED, PENDING

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    private String paymentMethod; // e.g., "Credit Card", "PayPal"
}