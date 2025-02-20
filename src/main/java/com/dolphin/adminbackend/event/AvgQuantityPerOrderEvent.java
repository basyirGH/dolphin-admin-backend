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

@Component
public class AvgQuantityPerOrderEvent extends ApplicationEvent implements MetricCreator{
    // Beans
    @Autowired
    public OrderService orderService;

    // Constructor
    // 0 arg for spring context scanning
    public AvgQuantityPerOrderEvent() {
        super(new Object());
    }

    // 1 arg for instantiation
    public AvgQuantityPerOrderEvent(Object source) {
        super(source);
    }

    // Member fields
    private final MetricTypeEnum type = MetricTypeEnum.SINGLE_AMOUNT;
    private final MetricEventEnum event = MetricEventEnum.AVERAGE_QUANTITY;
    private Date timeOccurred;

    // Methods
    @Override
    public Metric getMetric(Date timeOccurred) {
        //SingleAmountMetric metric = new SingleAmountMetric("Avg Quantity/Order", false, aggregator, type, null, "ScatterPlotIcon");
        Supplier<List<TimeframedAmount>> aggregator = () -> orderService.getTimeframedSingleAmounts(event);
        SingleAmountMetric metric = new SingleAmountMetric();
        metric.setLabel("Avg Quantity/Order");
        metric.setAggregator(aggregator);
        metric.setType(type);
        metric.setPrefix(null);
        metric.setIcon("ScatterPlotIcon");
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
