package com.dolphin.adminbackend.model.jpa;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

import com.dolphin.adminbackend.constant.OrderStatus;
import com.dolphin.adminbackend.eventlistener.OrderEventListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "order_")
@EntityListeners(OrderEventListener.class)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference // Marks the parent side, prevents circular serialization (nested object/array
                       // in api response)
    private Customer customer;

    // Composition: OrderItem cannot exist without an Order. If an Order is deleted,
    // all associated OrderItems should also be deleted.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Marks the child side, prevents circular serialization (nested object/array in
                          // api response)
    private List<OrderItem> items;

    @Column(nullable = false)
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false)
    private Date orderDate; //LocalDateTime is problematic with Jackson

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Payment> payments;

    /* setters & getters */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}