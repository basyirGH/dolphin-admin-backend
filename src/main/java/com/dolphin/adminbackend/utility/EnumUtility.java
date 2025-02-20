package com.dolphin.adminbackend.utility;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.dolphin.adminbackend.creator.MetricCreator;
import com.dolphin.adminbackend.enums.MetricEventEnum;
import com.dolphin.adminbackend.model.statisticaldashboard.SingleAmountMetric;

@Component
public class EnumUtility {

    /* @Lazy note
     * By default, Spring creates all singleton beans eagerly at the
     * startup/bootstrapping of the application context. The reason behind this is
     * simple: to avoid and detect all possible errors immediately rather than at
     * runtime.
     * 
     * However, there’re cases when we need to create a bean, not at the application
     * context startup, but when we request it.
     */
    @Lazy
    @Autowired
    List<MetricCreator> metricCreators;

    public List<MetricEventEnum> getSingleAmountEventEnums() {
        return metricCreators.stream()
                .filter(e -> e.getMetric(new Date()) instanceof SingleAmountMetric)
                .map(MetricCreator::getMetricEventEnum)
                .collect(Collectors.toList());
    }
}
