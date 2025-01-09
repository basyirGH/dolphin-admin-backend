package com.dolphin.adminbackend.event;

import java.math.BigDecimal;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

import com.dolphin.adminbackend.creator.MetricCreator;
import com.dolphin.adminbackend.enums.MetricEvent;
import com.dolphin.adminbackend.enums.MetricType;
import com.dolphin.adminbackend.model.statisticaldashboard.Metric;
import com.dolphin.adminbackend.model.statisticaldashboard.SingleAmountMetric;
import com.dolphin.adminbackend.service.OrderService;

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
public class OrdersCountMetricEvent extends ApplicationEvent implements MetricCreator {

    // Beans
    @Autowired
    public OrderService orderService;

    // Constructors
    public OrdersCountMetricEvent() {
        super(new Object());
    }

    public OrdersCountMetricEvent(Object source) {
        super(source);
    }    

    // Member fields
    private final MetricType type = MetricType.SINGLE_AMOUNT;
    private final MetricEvent event = MetricEvent.ORDERS_COUNT;

    // Methods
    @Override
    public Metric getMetricCreator() {
        Supplier<BigDecimal> aggregator = () -> BigDecimal.valueOf(orderService.getOrdersCount());
        SingleAmountMetric metric = new SingleAmountMetric("Total Orders", false, aggregator, type);
        return metric;
    }

    @Override
    public MetricType getMetricType() {
        return type;
    }

    @Override
    public MetricEvent getMetricEvent() {
        return event;
    }
}
