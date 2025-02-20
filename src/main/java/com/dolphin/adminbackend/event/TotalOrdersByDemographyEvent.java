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
import com.dolphin.adminbackend.model.statisticaldashboard.PieChartMetric;
import com.dolphin.adminbackend.service.OrderService;

@Component
public class TotalOrdersByDemographyEvent extends ApplicationEvent implements MetricCreator {

    // Beans
    @Autowired
    public OrderService orderService;

    // Constructors
    public TotalOrdersByDemographyEvent() {
        super(new Object());
    }

    public TotalOrdersByDemographyEvent(Object source) {
        super(source);
    }   

    // Member fields
    private final MetricTypeEnum type = MetricTypeEnum.PIE_CHART;
    private final MetricEventEnum event = MetricEventEnum.TOTAL_ORDERS_BY_DEMOGRAPHY;
    private Date timeOccured;

    // Methods
    @Override
    public Metric getMetric(Date timeOccured) {
        Supplier<List<TimeframedAmount>> aggregator = () -> orderService.getTimeframedTotalOrdersByDemography();
        PieChartMetric metric = new PieChartMetric("Total Orders By Demography", aggregator, type, "Face");
        metric.setType(this.type);
        metric.setAggregator(aggregator);
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
        return timeOccured;
    }

}
