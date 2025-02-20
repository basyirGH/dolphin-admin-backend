package com.dolphin.adminbackend.prototype;

import java.util.function.Supplier;

public abstract class Simulation {
    public abstract Supplier<?> getAggregator();
}
