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
import com.dolphin.adminbackend.model.dto.supplier.Line;
import com.dolphin.adminbackend.model.statisticaldashboard.LineChartMetric;
import com.dolphin.adminbackend.model.statisticaldashboard.Metric;
import com.dolphin.adminbackend.service.OrderService;

@Component
public class RealTimeTrendsEvent extends ApplicationEvent implements MetricCreator {

    // Beans
    @Autowired
    public OrderService orderService;

    // Constructors
    public RealTimeTrendsEvent() {
        super(new Object());
    }

    public RealTimeTrendsEvent(Object source, Date timeOccured) {
        super(source);
        this.timeOccured = timeOccured;
    }   

    // Member fields
    private final MetricTypeEnum type = MetricTypeEnum.LINE_CHART;
    private final MetricEventEnum event = MetricEventEnum.REAL_TIME_TRENDS;
    private Date timeOccured;

    // Methods
    @Override
    public Metric getMetric(Date timeOccured) {
        Supplier<List<Line>> aggregator = () -> orderService.getLastFewMinutesTrends(timeOccured);
        LineChartMetric metric = new LineChartMetric(null, aggregator, type);
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
