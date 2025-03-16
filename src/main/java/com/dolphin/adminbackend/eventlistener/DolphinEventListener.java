package com.dolphin.adminbackend.eventlistener;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.dolphin.adminbackend.creator.MetricCreator;
import com.dolphin.adminbackend.creator.SimulationCreator;
import com.dolphin.adminbackend.enums.MetricEventEnum;
import com.dolphin.adminbackend.factory.MetricFactory;
import com.dolphin.adminbackend.factory.SimulationFactory;
import com.dolphin.adminbackend.model.dto.request.SimulatableReq;
import com.dolphin.adminbackend.model.statisticaldashboard.Metric;
import com.dolphin.adminbackend.prototype.Simulation;
import com.dolphin.adminbackend.socketio.SocketIOController;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DolphinEventListener {

    // Beans
    @Autowired
    private MetricFactory metricFactory;

    @Autowired
    private SimulationFactory simFactory;

    @Autowired
    private SocketIOController webSocketController;

    
    /*
     * Generic type bound - to restrict the types that can be used as type arguments in a parameterized type.
     * T can be, for example, OrdersCountMetricEvent, but we don't want to define a method
     * for each event. Instead, we restrict a new type T, where it references all classes 
     * that extends ApplicationEvent and implements MetricCreator
     */
    // Methods
    @EventListener
    public <T extends ApplicationEvent & MetricCreator> void handleMetricEvent(T event) {
        MetricEventEnum eventEnum = event.getMetricEventEnum();
        String eventStr = eventEnum.toString();
        Date timeOccured = event.getTimeOccured();
        // log.info("timeOccured:  " + timeOccured);
        Metric metric = metricFactory.getMetric(eventEnum, timeOccured);
        webSocketController.broadcastMetricSocketEvent(eventStr, metric);
    }

    @EventListener
    public <T extends ApplicationEvent & SimulationCreator> void handleSimulatedOrderEvent(T event) {
        String eventStr = event.getEventStr();
        UUID simID = event.getSimID();
        List<SimulatableReq> req = event.getSimulatableRequest();
        Simulation sim = simFactory.getSimulation(eventStr, req, simID);
        webSocketController.broadcastSimulatedOrderSocketEvent(event.getEventStr(), sim);
    }
}
