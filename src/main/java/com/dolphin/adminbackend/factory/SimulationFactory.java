package com.dolphin.adminbackend.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.dolphin.adminbackend.creator.SimulationCreator;
import com.dolphin.adminbackend.model.dto.request.SimulatableReq;
import com.dolphin.adminbackend.prototype.Simulation;

@Component
public class SimulationFactory {

    // Member fields
    private Map<String, SimulationCreator> eventToCreatorMap;

    // Constructor
    public SimulationFactory(List<SimulationCreator> creatorBeans) {
        this.eventToCreatorMap = new HashMap<>();
        for (SimulationCreator creator : creatorBeans) {
            eventToCreatorMap.put(creator.getEventStr(), creator);
        }
    }

    // Methods
    public Simulation getSimulation(String eventStr, SimulatableReq req) {
        try {
            Simulation sim = this.eventToCreatorMap.get(eventStr).getSimulation(req);
            return sim;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
