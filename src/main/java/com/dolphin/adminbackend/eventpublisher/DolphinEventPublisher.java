package com.dolphin.adminbackend.eventpublisher;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class DolphinEventPublisher {
    
    // Beans
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    // Methods
    public void publishOne(ApplicationEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    public void publishMultiple(List<ApplicationEvent> events){
        for (ApplicationEvent event : events) {
            applicationEventPublisher.publishEvent(event);
        }
    } 
}