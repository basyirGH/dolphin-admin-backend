package com.dolphin.adminbackend.socketio;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.dolphin.adminbackend.model.dto.Dashboard;
import com.dolphin.adminbackend.model.dto.SocketDetail;
import com.dolphin.adminbackend.service.DashboardService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebSocketController {

    protected final SocketIOServer socketServer;

    @Autowired
    private DashboardService dashboardService;

    public WebSocketController(SocketIOServer socketServer) {
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
            Dashboard dashboard = dashboardService.getDashboard();
            ackRequest.sendAckData(dashboard);
        }
    };

    public void handleNewOrder(long latestOrdersCount) {
        // Broadcast to all connected clients
        socketServer.getBroadcastOperations().sendEvent("newOrder", latestOrdersCount);
    }
}
