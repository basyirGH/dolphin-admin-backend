package com.dolphin.adminbackend.eventlistener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.dolphin.adminbackend.event.OrderCreatedEvent;
import com.dolphin.adminbackend.repository.OrderRepo;
import com.dolphin.adminbackend.socketio.WebSocketController;


@Component
public class OrderEventListener implements ApplicationListener<OrderCreatedEvent> {

    @Autowired
    private WebSocketController webSocketController;

    @Autowired
    private OrderRepo orderRepo;

    @Override
    public void onApplicationEvent(OrderCreatedEvent event) {
        webSocketController.handleNewOrder(orderRepo.count());
    }
}
