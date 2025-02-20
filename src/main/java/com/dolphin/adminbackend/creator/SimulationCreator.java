package com.dolphin.adminbackend.creator;

import org.springframework.stereotype.Component;

import com.dolphin.adminbackend.model.dto.request.SimulatableReq;
import com.dolphin.adminbackend.prototype.Simulation;

@Component
public interface SimulationCreator {
    public Simulation getSimulation(SimulatableReq req);
    public String getEventStr();
    public SimulatableReq getSimulatableRequest();
}
