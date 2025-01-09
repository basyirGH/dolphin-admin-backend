package com.dolphin.adminbackend.eventpublisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import com.dolphin.adminbackend.event.OrdersCountMetricEvent;
import com.dolphin.adminbackend.event.OrdersPaymentSumMetricEvent;

@Component
public class MetricEventPublisher {
    
    // Beans
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    // Methods
    public void publishOrdersCountMetricEvent() {
        OrdersCountMetricEvent event = new OrdersCountMetricEvent(this);
        applicationEventPublisher.publishEvent(event);
    }

    public void publishOrdersPaymentSumMetricEvent() {
        OrdersPaymentSumMetricEvent event = new OrdersPaymentSumMetricEvent(this);
        applicationEventPublisher.publishEvent(event);
    }
}