package com.dolphin.adminbackend.factory;

import java.util.EnumMap;
import java.util.List;

import org.springframework.stereotype.Component;

import com.dolphin.adminbackend.creator.MetricCreator;
import com.dolphin.adminbackend.enums.MetricEvent;
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
    private EnumMap<MetricEvent, MetricCreator> creators;

    // Constructor
    public MetricFactory(List<MetricCreator> creatorBeans) {
        this.creators = new EnumMap<>(MetricEvent.class);
        for (MetricCreator creator : creatorBeans) {
            creators.put(creator.getMetricEvent(), creator);
        }
    }

    // Methods
    public Metric getMetric(MetricEvent event) {
        return this.creators.get(event).getMetricCreator();
    }
}
