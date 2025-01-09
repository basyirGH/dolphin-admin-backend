package com.dolphin.adminbackend.model.dto.request;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderItemReq {

    private Long productId;
    private Integer quantity;
    private BigDecimal pricePerUnit;

}

