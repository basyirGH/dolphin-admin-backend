package com.dolphin.adminbackend.creator;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dolphin.adminbackend.model.dto.request.SimulatableReq;
import com.dolphin.adminbackend.prototype.Simulation;

@Component
public interface SimulationCreator {
    public Simulation getSimulation(List<SimulatableReq> req, UUID simID);
    public String getEventStr();
    public List<SimulatableReq> getSimulatableRequest();
    public UUID getSimID();
}
