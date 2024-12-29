package com.dolphin.adminbackend.models;

import java.util.List;

import jakarta.persistence.*; 

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer stockQuantity;

    // Each Product belongs to exactly one Category
    // Lazy Loading means the associated entity will not be fetched immediately when the parent entity is 
    // retrieved. Instead, it will only be fetched on-demand (when accessed for the first time in code).
    // This is useful for improving performance by avoiding unnecessary data fetching.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;
}