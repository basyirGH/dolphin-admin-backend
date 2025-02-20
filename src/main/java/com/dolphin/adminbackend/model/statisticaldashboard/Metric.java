package com.dolphin.adminbackend.model.statisticaldashboard;

import java.util.function.Supplier;

import com.dolphin.adminbackend.enums.MetricTypeEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Metric {
    
    // Member fields
    public String label;

    // Methods
    public abstract MetricTypeEnum getType();
    public abstract Supplier<?> getAggregator(); //Wildcards allow us to handle generics without specifying a concrete type
}
