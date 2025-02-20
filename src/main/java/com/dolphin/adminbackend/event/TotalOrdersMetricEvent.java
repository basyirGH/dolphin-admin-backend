package com.dolphin.adminbackend.event;

import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

import com.dolphin.adminbackend.creator.MetricCreator;
import com.dolphin.adminbackend.enums.MetricEventEnum;
import com.dolphin.adminbackend.enums.MetricTypeEnum;
import com.dolphin.adminbackend.model.dto.supplier.TimeframedAmount;
import com.dolphin.adminbackend.model.statisticaldashboard.Metric;
import com.dolphin.adminbackend.model.statisticaldashboard.SingleAmountMetric;
import com.dolphin.adminbackend.service.OrderService;

import lombok.extern.slf4j.Slf4j;

/*
 * Events should ideally be immutable: Once an event is published, 
 * it is expected to represent a snapshot of the state at the time of publication. 
 * Allowing modifications to the event object after it's been published can introduce 
 * bugs or inconsistent behavior in your system.
 * 
* Below is the longer form of the lambda expressions used in this class
* Supplier<BigDecimal> aggregator = new Supplier<BigDecimal>() {
* @Override
* public BigDecimal get() {
* return BigDecimal.valueOf(event.getCount());
* }
* };
* 
 */
@Component
@Slf4j
public class TotalOrdersMetricEvent extends ApplicationEvent implements MetricCreator {

    // Beans
    @Autowired
    public OrderService orderService;

    // Member fields
    private final MetricTypeEnum type = MetricTypeEnum.SINGLE_AMOUNT;
    private final MetricEventEnum event = MetricEventEnum.TOTAL_ORDERS;
    private Date timeOccurred;

    // Constructors
    // ApplicationEvent requires a non-arg constructor
    public TotalOrdersMetricEvent() {
        super(new Object());
    }

    public TotalOrdersMetricEvent(Object source) {
        super(source);
    }

    // Methods
    @Override
    public Metric getMetric(Date timeOccurred) {
        Supplier<List<TimeframedAmount>> aggregator = () -> orderService.getTimeframedSingleAmounts(event);
        SingleAmountMetric metric = new SingleAmountMetric();
        metric.setLabel("Total Orders");
        metric.setAggregator(aggregator);
        metric.setType(type);
        metric.setPrefix(null);
        metric.setIcon("LocalMallIcon");
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
}
