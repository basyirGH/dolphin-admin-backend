package com.dolphin.adminbackend.eventpublisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.dolphin.adminbackend.event.OrderCreatedEvent;

@Component
public class OrderEventPublisher {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishOrderCreatedEvent(final String message) {
        OrderCreatedEvent customSpringEvent = new OrderCreatedEvent(this, message);
        applicationEventPublisher.publishEvent(customSpringEvent);
    }
}