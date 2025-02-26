package com.dolphin.adminbackend.socketio;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.dolphin.adminbackend.event.AvgQuantityPerOrderEvent;
import com.dolphin.adminbackend.event.AvgRevenueMetricEvent;
import com.dolphin.adminbackend.event.NewOrderEvent;
import com.dolphin.adminbackend.event.RealTimeTrendsEvent;
import com.dolphin.adminbackend.event.TotalOrdersByDemographyEvent;
import com.dolphin.adminbackend.event.TotalOrdersMetricEvent;
import com.dolphin.adminbackend.event.TotalRevenueMetricEvent;
import com.dolphin.adminbackend.eventpublisher.DolphinEventPublisher;
import com.dolphin.adminbackend.model.dto.pojo.SocketDetail;
import com.dolphin.adminbackend.model.dto.request.OrderReq;
import com.dolphin.adminbackend.model.jpa.Order;
import com.dolphin.adminbackend.model.statisticaldashboard.Metric;
import com.dolphin.adminbackend.prototype.Simulation;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SocketIOController {

    protected final SocketIOServer socketServer;

    @Autowired
    private DolphinEventPublisher eventPublisher;

    public SocketIOController(SocketIOServer socketServer) {
        this.socketServer = socketServer;
        this.socketServer.addEventListener("INIT_SINGLE_AMOUNTS", SocketDetail.class, initSingleAmount);
        this.socketServer.addEventListener("INIT_LINE_CHARTS", SocketDetail.class, initLineCharts);
        this.socketServer.addEventListener("INIT_PIE_CHARTS", SocketDetail.class, initPieCharts);
        this.socketServer.addEventListener("SIMULATE_NEW_ORDER", OrderReq.class, simulateNewOrder);
    }

    /*
     * Ack is a feature of Socket.IO that allows the sender to receive confirmation
     * that the recipient:
     * Received the event/data.
     * Processed it successfully and, optionally, returned some data in response.
     */
    public DataListener<SocketDetail> initSingleAmount = new DataListener<>() {
        @Override
        public void onData(SocketIOClient client, SocketDetail socketDetail, AckRequest ackRequest) {

            TotalRevenueMetricEvent totalRevenueEvent = new TotalRevenueMetricEvent(this);
            TotalOrdersMetricEvent totalOrdersEvent = new TotalOrdersMetricEvent(this);
            AvgRevenueMetricEvent avgRevenueEvent = new AvgRevenueMetricEvent(this);
            AvgQuantityPerOrderEvent avgQuantityEvent = new AvgQuantityPerOrderEvent(this);

            List<ApplicationEvent> events = new ArrayList<ApplicationEvent>();
            events.add(totalRevenueEvent);
            events.add(totalOrdersEvent);
            events.add(avgRevenueEvent);
            events.add(avgQuantityEvent);
            eventPublisher.publishMultiple(events);

            ackRequest.sendAckData("single amount metrics initiated");
        }
    };

    public DataListener<SocketDetail> initLineCharts = new DataListener<>() {
        @Override
        public void onData(SocketIOClient client, SocketDetail socketDetail, AckRequest ackRequest) {
            RealTimeTrendsEvent lineEvent = new RealTimeTrendsEvent(this, socketDetail.getTimeOccured());
            eventPublisher.publishOne(lineEvent);
            ackRequest.sendAckData("line chart metrics initiated");
        }
    };

    public DataListener<SocketDetail> initPieCharts = new DataListener<>() {
        @Override
        public void onData(SocketIOClient client, SocketDetail socketDetail, AckRequest ackRequest) {
            TotalOrdersByDemographyEvent ordersByDemographyEvent = new TotalOrdersByDemographyEvent(this);
            eventPublisher.publishOne(ordersByDemographyEvent);
            ackRequest.sendAckData("pie chart metrics initiated");
        }
    };

    public DataListener<OrderReq> simulateNewOrder = new DataListener<>() {
        @Override
        public void onData(SocketIOClient client, OrderReq orderReq, AckRequest ackRequest) {
            
            NewOrderEvent newOrder = new NewOrderEvent(this, orderReq);
            eventPublisher.publishOne(newOrder);
            log.info("+1 new order: ");
            Map<String, String> response = new HashMap<>();
            response.put("status", "200");
            response.put("message", "new order created");
            ackRequest.sendAckData(response);
        }
    };

    public void broadcastMetricSocketEvent(String eventStr, Metric metric) {
        socketServer.getBroadcastOperations().sendEvent(eventStr, metric);
    }

    public void broadcastSimulatedOrderSocketEvent(String eventStr, Simulation sim) {
        socketServer.getBroadcastOperations().sendEvent(eventStr, sim);
    }

}
