package com.dolphin.adminbackend.event;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

import com.dolphin.adminbackend.creator.SimulationCreator;
import com.dolphin.adminbackend.enums.MetricEventEnum;
import com.dolphin.adminbackend.enums.MetricTypeEnum;
import com.dolphin.adminbackend.model.dto.request.OrderReq;
import com.dolphin.adminbackend.model.dto.request.SimulatableReq;
import com.dolphin.adminbackend.model.jpa.Order;
import com.dolphin.adminbackend.prototype.Simulation;
import com.dolphin.adminbackend.service.OrderService;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@Component
@Slf4j
public class NewOrderEvent extends ApplicationEvent implements SimulationCreator {

    // Beans
    @Autowired
    public OrderService orderService;

    // Member fields
    private final String TYPE = "SIMULATION";
    private final String EVENT = "NEW_ORDER";
    private List<OrderReq> orderReq;
    private UUID simID;

    // Constructors
    // ApplicationEvent requires a non-arg constructor
    public NewOrderEvent() {
        super(new Object());
    }

    public NewOrderEvent(Object source, List<OrderReq> orderReq, UUID simID) {
        super(source);
        this.orderReq = orderReq;
        this.simID = simID;
    }

    /*
     * Method overriding rules in Java require that an overriding method must have
     * the same method signature as the method in the parent interface.
     * Since OrderReq is a subclass (or implementing class) of SimulatableReq, the
     * overridden method is not truly overriding the original method because its
     * parameter type is more restrictive.
     */
    @Override
    public Simulation getSimulation(List<SimulatableReq> orders, UUID simID) {
        // log.info("id: " + simID);
        Supplier<Order> aggregator = () -> orderService.createOrders(orders, simID);
        return aggregator.get();
    }

    @Override
    public String getEventStr() {
        return this.EVENT;
    }

    @Override
    public List getSimulatableRequest() {
        return this.orderReq;
    }

    @Override
    public UUID getSimID(){
        return this.simID;
    }

}
