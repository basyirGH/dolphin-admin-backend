package com.dolphin.adminbackend.model.jpa;

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

    // setters & getters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    
}