package com.dolphin.adminbackend.creator;

import java.util.Date;
import java.util.UUID;

import com.dolphin.adminbackend.enums.MetricEventEnum;
import com.dolphin.adminbackend.enums.MetricTypeEnum;
import com.dolphin.adminbackend.model.statisticaldashboard.Metric;

public interface MetricCreator {
    public Metric getMetric(Date timeOccured);
    public MetricTypeEnum getMetricType();
    public MetricEventEnum getMetricEventEnum();
    public Date getTimeOccured();
}
