package com.dolphin.adminbackend.creator;

import org.springframework.stereotype.Component;

import com.dolphin.adminbackend.enums.MetricEvent;
import com.dolphin.adminbackend.enums.MetricType;
import com.dolphin.adminbackend.model.statisticaldashboard.Metric;

@Component
public interface MetricCreator {
    public Metric getMetricCreator();
    public MetricType getMetricType();
    public MetricEvent getMetricEvent();
}
