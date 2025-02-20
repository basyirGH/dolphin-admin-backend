package com.dolphin.adminbackend.model.statisticaldashboard;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;

import com.dolphin.adminbackend.enums.DecimalFormat;
import com.dolphin.adminbackend.enums.MetricTypeEnum;
import com.dolphin.adminbackend.model.dto.supplier.TimeframedAmount;

import lombok.Setter;

import lombok.Getter;

@Getter
@Setter
public class SingleAmountMetric extends Metric {

    // Member fields
    private Supplier<List<TimeframedAmount>> aggregator; // by the superclass method
    private MetricTypeEnum type; // by the superclass method
    private List<TimeframedAmount> aggregatedData; // native field
    private String prefix;
    private String icon;

    //Constructors
    /*
     * Note on super:
     * super.label and this.label both refer to the same field inherited from parent.
     * However, using super is clearer when you intend to highlight that you're
     * working with the superclass's version of the field.
     */
    // public SingleAmountMetric(String label, boolean isDisabled, Supplier<List<TimeframedAmount>> aggregator, MetricType type, String prefix, String icon, DecimalFormat format) {
    //     super.label = label;
    //     super.isDisabled = isDisabled;
    //     this.aggregator = aggregator;
    //     this.type = type;
    //     this.prefix = prefix;
    //     this.icon = icon;
    //     this.amountFormat = format;
    // }

    public SingleAmountMetric() {}

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
