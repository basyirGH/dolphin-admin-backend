package com.dolphin.adminbackend.model.dto.queryresult;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;

@Getter
public class RevenueByCategoryOverTime {

    // Member fields
    private Long categoryId;
    private String categoryName;
    private Date orderDate;
    private BigDecimal amount;
    private String lineColor;

    // Constructor
    public RevenueByCategoryOverTime(Long categoryId, String categoryName, Date orderDate, BigDecimal amount, String lineColor) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.orderDate = orderDate;
        this.amount = amount;
        this.lineColor = lineColor;
    }
}
