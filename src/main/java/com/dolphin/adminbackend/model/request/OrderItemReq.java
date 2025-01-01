package com.dolphin.adminbackend.model.request;

public class OrderItemReq {

    private Long productId;
    private Integer quantity;
    private Double pricePerUnit;

    // Getters and setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Double price) {
        this.pricePerUnit = price;
    }
}

