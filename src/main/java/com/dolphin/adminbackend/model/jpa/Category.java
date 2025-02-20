package com.dolphin.adminbackend.model.jpa;

import jakarta.persistence.*; // For annotations like @Entity, @Id, @Column, etc.
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List; // For collections like List.

@Setter
@Getter
@Entity
@NoArgsConstructor
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

    @Column(name="line_color", nullable = false)
    private String lineColor;

    // Constructors
    public Category(Long id, String name, String lineColor){
        this.id = id;
        this.name = name;
        this.lineColor = lineColor;
    }
    
}