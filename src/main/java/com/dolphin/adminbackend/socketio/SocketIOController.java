package com.dolphin.adminbackend.socketio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.dolphin.adminbackend.enums.MetricEvent;
import com.dolphin.adminbackend.eventpublisher.MetricEventPublisher;
import com.dolphin.adminbackend.model.dto.SocketDetail;
import com.dolphin.adminbackend.model.statisticaldashboard.Metric;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SocketIOController {

    protected final SocketIOServer socketServer;

    @Autowired
    private MetricEventPublisher metricEventPublisher;

    public SocketIOController(SocketIOServer socketServer) {
        this.socketServer = socketServer;
        this.socketServer.addEventListener("initDashboard", SocketDetail.class, initDashboard);
    }

    /*
     * Ack is a feature of Socket.IO that allows the sender to receive confirmation
     * that the recipient:
     * Received the event/data.
     * Processed it successfully and, optionally, returned some data in response.
     */
    public DataListener<SocketDetail> initDashboard = new DataListener<>() {
        @Override
        public void onData(SocketIOClient client, SocketDetail socketDetail, AckRequest ackRequest) {
            metricEventPublisher.publishOrdersCountMetricEvent();
            metricEventPublisher.publishOrdersPaymentSumMetricEvent();
            ackRequest.sendAckData("dashboard initated");
        }
    };

    public void broadcastOrdersCountMetric(Metric metric) {        
        socketServer.getBroadcastOperations().sendEvent(MetricEvent.ORDERS_COUNT.toString(), metric);
    }

    public void broadcastOrdersPaymentSumMetric(Metric metric) {        
        socketServer.getBroadcastOperations().sendEvent(MetricEvent.ORDERS_PAYMENTS_SUM.toString(), metric);
    }

}
