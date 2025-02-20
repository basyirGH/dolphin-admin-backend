package com.dolphin.adminbackend.model.dto.supplier;

import java.math.BigDecimal;

import com.dolphin.adminbackend.enums.TimeframeEnum;

import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
public class TimeframedAmount {

    // Member fields
    private TimeframeEnum timeframe;
    Date startDate, previousDate;
    private BigDecimal amount, previousAmount;
    private String message;
    private BigDecimal rateOfChange;
    private List<List<Object>> series;

    // Constructors
    // timeframed single amounts
    public TimeframedAmount(TimeframeEnum timeframe, Date startDate, Date previousDate, BigDecimal amount, BigDecimal previousAmount, String message, BigDecimal rateOfChange) {
        this.timeframe = timeframe;
        this.startDate = startDate;
        this.previousDate = previousDate;
        this.amount = amount;
        this.previousAmount = previousAmount;
        this.message = message;
        this.rateOfChange = rateOfChange;
    }

    // timeframed chart series
    public TimeframedAmount(TimeframeEnum timeframe, List<List<Object>> series) {
        this.timeframe = timeframe;
        this.series = series;
    }

}
