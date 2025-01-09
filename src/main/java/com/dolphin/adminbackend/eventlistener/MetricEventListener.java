package com.dolphin.adminbackend.eventlistener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.dolphin.adminbackend.enums.MetricEvent;
import com.dolphin.adminbackend.event.OrdersCountMetricEvent;
import com.dolphin.adminbackend.event.OrdersPaymentSumMetricEvent;
import com.dolphin.adminbackend.factory.MetricFactory;
import com.dolphin.adminbackend.model.statisticaldashboard.Metric;
import com.dolphin.adminbackend.socketio.SocketIOController;

@Component
public class MetricEventListener {

    // Beans
    @Autowired
    private MetricFactory metricFactory;

    @Autowired
    private SocketIOController webSocketController;

    // Methods
    @EventListener
    public void handleOrdersCountMetricEvent(OrdersCountMetricEvent event) {
        Metric metric = metricFactory.getMetric(event.getMetricEvent());
        webSocketController.broadcastOrdersCountMetric(metric);
    }

    @EventListener
    public void handleOrdersCountMetricEvent(OrdersPaymentSumMetricEvent event) {
        Metric metric = metricFactory.getMetric(event.getMetricEvent());
        webSocketController.broadcastOrdersPaymentSumMetric(metric);
    }
}
