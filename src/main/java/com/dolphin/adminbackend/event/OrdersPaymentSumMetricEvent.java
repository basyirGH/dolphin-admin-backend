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
 */
@Component
public class OrdersPaymentSumMetricEvent extends ApplicationEvent implements MetricCreator {

    // Beans
    @Autowired
    public OrderService orderService;

    // Constructor
    public OrdersPaymentSumMetricEvent() {
        super(new Object());
    }

    public OrdersPaymentSumMetricEvent(Object source) {
        super(source);
    }

    // Member fields
    private final MetricType type = MetricType.SINGLE_AMOUNT;
    private final MetricEvent event = MetricEvent.ORDERS_PAYMENTS_SUM;

    // Methods
    @Override
    public Metric getMetricCreator() {
        Supplier<BigDecimal> aggregator = () -> orderService.getSumOfTotalAmountAllOrders();
        SingleAmountMetric metric = new SingleAmountMetric("Total Payment Sum", false, aggregator, type);
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
