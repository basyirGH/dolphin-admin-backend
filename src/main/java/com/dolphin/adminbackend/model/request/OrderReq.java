package com.dolphin.adminbackend.model.request;

import java.util.List;

public class OrderReq {

    private Long customerId;
    private List<OrderItemReq> items;

    // Getters and setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<OrderItemReq> getItems() {
        return items;
    }

    public void setItems(List<OrderItemReq> items) {
        this.items = items;
    }
}
