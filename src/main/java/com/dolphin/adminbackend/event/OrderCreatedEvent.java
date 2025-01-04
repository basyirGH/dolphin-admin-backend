package com.dolphin.adminbackend.event;

import org.springframework.context.ApplicationEvent;


public class OrderCreatedEvent extends ApplicationEvent{

    private final String message;

    public OrderCreatedEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

