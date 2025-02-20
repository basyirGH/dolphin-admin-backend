package com.dolphin.adminbackend.event;

import java.util.Date;
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

@Setter
@Getter
@Component
public class NewOrderEvent extends ApplicationEvent implements SimulationCreator {

    // Beans
    @Autowired
    public OrderService orderService;

    // Member fields
    private final String TYPE = "SIMULATION";
    private final String EVENT = "NEW_ORDER";
    private OrderReq orderReq;

    // Constructors
    // ApplicationEvent requires a non-arg constructor
    public NewOrderEvent() {
        super(new Object());
    }

    public NewOrderEvent(Object source, OrderReq orderReq) {
        super(source);
        this.orderReq = orderReq;
    }

    /*
     * Method overriding rules in Java require that an overriding method must have
     * the same method signature as the method in the parent interface.
     * Since OrderReq is a subclass (or implementing class) of SimulatableReq, your
     * overridden method is not truly overriding the original method because its
     * parameter type is more restrictive.
     */
    @Override
    public Simulation getSimulation(SimulatableReq orderReq) {
        Supplier<Order> aggregator = () -> orderService.createOrder((OrderReq) orderReq);
        return aggregator.get();

    }

    @Override
    public String getEventStr() {
        return this.EVENT;
    }

    @Override
    public OrderReq getSimulatableRequest() {
        return this.orderReq;
    }

}
