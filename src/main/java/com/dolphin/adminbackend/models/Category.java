package com.dolphin.adminbackend.models;

import jakarta.persistence.*; // For annotations like @Entity, @Id, @Column, etc.
import java.util.List; // For collections like List.

@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // @OneToMany is placed in the parent or one side (in this case, Category).
    // must annotate the reverse side of the relationship with @ManyToOne
    // @ManyToOne is placed in the child or many side (in this case, Product).
    // a category is associated with many products
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;
}