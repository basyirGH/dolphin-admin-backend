package com.dolphin.adminbackend.model.dto.supplier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.units.qual.s;

import com.dolphin.adminbackend.enums.MetricEventEnum;
import com.dolphin.adminbackend.enums.TimeframeEnum;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Line {
    
    // Member fields
    private String type, name, lineColor, yTitle, xTitle;
    private MetricEventEnum metricCode;
    private Map<String, Object> subMetric; //optional data
    private List<List<Object>> data;
    private TimeframeEnum tfEnum;

    // Constructors
    // last few mins trend
    public Line(String type, String name, MetricEventEnum metricCode, String lineColor, List<List<Object>> data, Map<String, Object> subMetric, String yTitle, String xTitle){
        this.type = type;
        this.name = name;
        this.metricCode = metricCode;
        this.lineColor = lineColor;
        this.data = data;
        this.subMetric = subMetric;
        this.yTitle = yTitle;
        this.xTitle = xTitle;
    }

    // timeframed trend
    public Line(TimeframeEnum tfEnum, String type, String name, MetricEventEnum metricCode, String lineColor, List<List<Object>> data, String yTitle, String xTitle){
        this.tfEnum = tfEnum;
        this.type = type;
        this.name = name;
        this.metricCode = metricCode;
        this.lineColor = lineColor;
        this.data = data;
        this.yTitle = yTitle;
        this.xTitle = xTitle;
    }
}
