package com.dolphin.adminbackend.factory;

import java.util.Date;
import java.util.EnumMap;
import java.util.List;

import org.springframework.stereotype.Component;

import com.dolphin.adminbackend.creator.MetricCreator;
import com.dolphin.adminbackend.enums.MetricEventEnum;
import com.dolphin.adminbackend.enums.TimeframeEnum;
import com.dolphin.adminbackend.model.statisticaldashboard.Metric;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MetricFactory {
    /*
     * Note on factory:
     * Constructor Injection: Since this constructor has only one parameter,
     * Spring will automatically know that it needs to inject a
     * list of beans into it.
     * Bean Creation: Spring will instantiate this factory when the
     * application context is created and resolve the constructor argument's
     * dependency by
     * looking for all beans of its type in the context. Any class that
     * extends it and is annotated
     * with @Component (or registered as a bean in a configuration class) will be
     * included in that list.
     */

    // Member fields
    private EnumMap<MetricEventEnum, MetricCreator> creators;

    // Constructor
    public MetricFactory(List<MetricCreator> creatorBeans) {
        this.creators = new EnumMap<>(MetricEventEnum.class);
        for (MetricCreator creator : creatorBeans) {
            creators.put(creator.getMetricEventEnum(), creator);
        }
    }

    // Methods
    public Metric getMetric(MetricEventEnum event, Date timeOccured) {
        try {
            Metric metric = this.creators.get(event).getMetric(timeOccured);
            return metric;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
