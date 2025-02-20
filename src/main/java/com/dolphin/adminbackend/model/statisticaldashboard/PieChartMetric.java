package com.dolphin.adminbackend.model.statisticaldashboard;

import java.util.List;
import java.util.function.Supplier;

import com.dolphin.adminbackend.enums.MetricTypeEnum;
import com.dolphin.adminbackend.model.dto.supplier.Line;
import com.dolphin.adminbackend.model.dto.supplier.TimeframedAmount;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PieChartMetric extends Metric {

    // Member fields
    private Supplier<List<TimeframedAmount>> aggregator; // by the superclass method
    private MetricTypeEnum type; // by the superclass method
    private List<TimeframedAmount> aggregatedData; // native field
    private String icon;


    public PieChartMetric(String label, Supplier<List<TimeframedAmount>> aggregator, MetricTypeEnum type, String icon) {
        super.label = label;
        this.aggregator = aggregator;
        this.type = type;
        this.icon = icon;
    }

    // Methods
    @Override
    public Supplier<List<TimeframedAmount>> getAggregator() {
        return aggregator;
    }

    @Override
    public MetricTypeEnum getType() {
        return type;
    }

    public List<TimeframedAmount> getAggregatedData() {
        this.aggregatedData = aggregator.get();
        return aggregatedData;
    }

}
