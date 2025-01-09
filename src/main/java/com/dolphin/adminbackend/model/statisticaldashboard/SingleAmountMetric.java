package com.dolphin.adminbackend.model.statisticaldashboard;

import java.math.BigDecimal;
import java.util.function.Supplier;

import com.dolphin.adminbackend.enums.MetricType;

public class SingleAmountMetric extends Metric {

    // Member fields
    private final Supplier<BigDecimal> aggregator; // by the interface method
    private final MetricType type; // by the interface method
    private BigDecimal aggregatedData; // native field

    /*
     * Note on super:
     * super.label and this.label both refer to the same field inherited from parent.
     * However, using super is clearer when you intend to highlight that you're
     * working with the superclass's version of the field.
     */
    //Constructors
    public SingleAmountMetric(String label, boolean isDisabled, Supplier<BigDecimal> aggregator, MetricType type) {
        super.label = label;
        super.isDisabled = isDisabled;
        this.aggregator = aggregator;
        this.type = type;
    }

    // Methods
    @Override
    public Supplier<BigDecimal> getAggregator() {
        return aggregator;
    }

    @Override
    public MetricType getType() {
        return type;
    }

    public BigDecimal getAggregatedData() {
        this.aggregatedData = aggregator.get();
        return aggregatedData;
    }
}
