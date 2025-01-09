package com.dolphin.adminbackend.model.jpa;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
@Table(name = "order_item")
public class OrderItem {

    public OrderItem(Long id, Product product, Integer quantity, BigDecimal price, Order order) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.pricePerUnit = price;
        this.order = order;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * By default, JPA uses lazy loading for @ManyToOne associations to avoid
     * unnecessary database queries.
     * 
     * When a Product is lazily loaded, its properties are not initialized unless
     * explicitly accessed before serialization. Since the Product object hasn't
     * been accessed in your code before serializing the response, the JSON
     * serializer (e.g., Jackson) sees an uninitialized proxy object and serializes
     * it as an empty object ({}).
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference // Marks the parent side, prevents circular serialization (nested object/array in api response)
    private Product product;

    // if the @JoinColumn annotation is omitted, the database will still create a
    // foreign key, but it will use the default name and behavior
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference // Marks the child side, prevents circular serialization (nested object/array in api response)
    private Order order;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name="price_per_unit", nullable = false, precision = 15, scale = 2)
    private BigDecimal pricePerUnit;

    /* setters & getters */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal price) {
        this.pricePerUnit = price;
    }

}
