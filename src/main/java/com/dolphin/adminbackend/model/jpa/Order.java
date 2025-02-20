package com.dolphin.adminbackend.model.jpa;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import com.dolphin.adminbackend.enums.OrderStatus;
import com.dolphin.adminbackend.eventlistener.DolphinEventListener;
import com.dolphin.adminbackend.prototype.Simulation;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "order_")
@EntityListeners(DolphinEventListener.class)
public class Order extends Simulation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    // Marks the parent side, prevents circular serialization (nested object/array
    // in api response)
    @JsonBackReference
    private Customer customer;

    // Composition: OrderItem cannot exist without an Order. If an Order is deleted,
    // all associated OrderItems should also be deleted.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Marks the child side, prevents circular serialization (nested object/array in
                          // api response)
    private List<OrderItem> items;

    // "name" explicitly tell the db to not duplicate this column when the type or
    // variable name changes
    // precision = 15: Allows up to 15 significant digits in total (before and after
    // dp).
    // scale = 2: Reserves 2 of those digits for the decimal places.
    // 1 trillion
    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false)
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date orderDate; // LocalDateTime is problematic with Jackson

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Payment> payments;

    @Transient
    private Supplier<Order> aggregator;

    @Override
    public Supplier<Order> getAggregator() {
        return aggregator;
    }

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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
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