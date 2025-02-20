package com.dolphin.adminbackend.model.statisticaldashboard;

import java.util.List;
import java.util.function.Supplier;

import com.dolphin.adminbackend.enums.MetricTypeEnum;
import com.dolphin.adminbackend.model.dto.supplier.Line;

public class LineChartMetric extends Metric {

    // Member fields
    private final Supplier<List<Line>> aggregator; // by the superclass method
    private final MetricTypeEnum type; // by the superclass method
    private List<Line> aggregatedData; // native field

    // Constructors
    public LineChartMetric(String label, Supplier<List<Line>> aggregator, MetricTypeEnum type) {
        super.label = label;
        this.aggregator = aggregator;
        this.type = type;
    }

    // Methods
    @Override
    public Supplier<List<Line>> getAggregator() {
        return aggregator;
    }

    @Override
    public MetricTypeEnum getType() {
        return type;
    }

    public List<Line> getAggregatedData() {
        this.aggregatedData = aggregator.get();
        return aggregatedData;
    }

}
