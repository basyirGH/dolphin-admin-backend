package com.dolphin.adminbackend.event;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

import com.dolphin.adminbackend.creator.MetricCreator;
import com.dolphin.adminbackend.enums.MetricEventEnum;
import com.dolphin.adminbackend.enums.MetricTypeEnum;
import com.dolphin.adminbackend.enums.TimeframeEnum;
import com.dolphin.adminbackend.model.dto.supplier.TimeframedAmount;
import com.dolphin.adminbackend.model.statisticaldashboard.Metric;
import com.dolphin.adminbackend.model.statisticaldashboard.SingleAmountMetric;
import com.dolphin.adminbackend.service.OrderService;

/*
 * Events should ideally be immutable: Once an event is published, 
 * it is expected to represent a snapshot of the state at the time of publication. 
 * Allowing modifications to the event object after it's been published can introduce 
 * bugs or inconsistent behavior in your system.
 */
@Component
public class TotalRevenueMetricEvent extends ApplicationEvent implements MetricCreator {

    // Beans
    @Autowired
    public OrderService orderService;

    // Constructor
    public TotalRevenueMetricEvent() {
        super(new Object());
    }

    public TotalRevenueMetricEvent(Object source) {
        super(source);
    }

    // Member fields
    private final MetricTypeEnum type = MetricTypeEnum.SINGLE_AMOUNT;
    private final MetricEventEnum event = MetricEventEnum.TOTAL_REVENUE;
    private Date timeOccurred;
    private UUID sessionID;

    // Methods
    @Override
    public Metric getMetric(Date timeOccurred) {
        Supplier<List<TimeframedAmount>> aggregator = () -> orderService.getTimeframedSingleAmounts(event);
        // SingleAmountMetric metric = new SingleAmountMetric("Revenue", false,
        // aggregator, type, "RM", "StackedLineChartIcon");
        SingleAmountMetric metric = new SingleAmountMetric();
        metric.setLabel("Total Revenue");
        metric.setAggregator(aggregator);
        metric.setType(type);
        metric.setPrefix("RM");
        metric.setIcon("StackedLineChartIcon");
        return metric;
    }

    @Override
    public MetricTypeEnum getMetricType() {
        return type;
    }

    @Override
    public MetricEventEnum getMetricEventEnum() {
        return event;
    }

    @Override
    public Date getTimeOccured(){
        return timeOccurred;
    }

    public UUID getSessionID() {
        return this.sessionID;
    }
}
