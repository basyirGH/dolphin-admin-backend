package com.dolphin.adminbackend.model.statisticaldashboard;

import java.util.function.Supplier;

import com.dolphin.adminbackend.enums.MetricType;

public abstract class Metric {
    
    public String label;
    public boolean isDisabled;
    public abstract MetricType getType();
    public abstract Supplier<?> getAggregator(); //Wildcards allow us to handle generics without specifying a concrete type
}
