package com.dolphin.adminbackend.socketio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.context.ApplicationEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.dolphin.adminbackend.enums.Prompt;
import com.dolphin.adminbackend.event.*;

import com.dolphin.adminbackend.eventpublisher.DolphinEventPublisher;
import com.dolphin.adminbackend.model.dto.pojo.DiverPromptDetail;
import com.dolphin.adminbackend.model.dto.pojo.SimStatusDetail;
import com.dolphin.adminbackend.model.dto.pojo.SimulationDetail;
import com.dolphin.adminbackend.model.dto.pojo.SocketDetail;
import com.dolphin.adminbackend.model.dto.request.OrderReq;
import com.dolphin.adminbackend.model.dto.response.GeminiRes;
import com.dolphin.adminbackend.model.statisticaldashboard.Metric;
import com.dolphin.adminbackend.prototype.Simulation;
import com.dolphin.adminbackend.service.GenerativeSQLService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class SocketIOController {

    protected final SocketIOServer socketServer;

    @Autowired
    private DolphinEventPublisher eventPublisher;

    @Autowired
    private GenerativeSQLService genSQLService;

    public SocketIOController(SocketIOServer socketServer) {
        this.socketServer = socketServer;
        this.socketServer.addEventListener("INIT_SINGLE_AMOUNTS", SocketDetail.class, initSingleAmount);
        this.socketServer.addEventListener("INIT_LINE_CHARTS", SocketDetail.class, initLineCharts);
        this.socketServer.addEventListener("INIT_PIE_CHARTS", SocketDetail.class, initPieCharts);
        this.socketServer.addEventListener("ASK_SIM_STATUS", SimStatusDetail.class, askSimStatus);
        this.socketServer.addEventListener("ANSWER_SIM_STATUS", SimStatusDetail.class, answerSimStatus);
        this.socketServer.addEventListener("SIMULATE_NEW_ORDER", SimulationDetail.class, simulateNewOrder);
        this.socketServer.addEventListener("DIVER_NEW_PROMPT", DiverPromptDetail.class, handleDiverPrompt);
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

    public DataListener<SimStatusDetail> askSimStatus = new DataListener<>() {
        @Override
        public void onData(SocketIOClient client, SimStatusDetail statusDetail, AckRequest ackRequest) {
            Map<String, String> response = new HashMap<>();
            response.put("code", "200");
            ackRequest.sendAckData(response);
            Date now = new Date();
            statusDetail.setSessionID(client.getSessionId());
            statusDetail.setDate(now);
            statusDetail.setStatus("Asking all active clients if one of them is running a simulation");
            socketServer.getBroadcastOperations().sendEvent("BROADCAST_ASK_SIM_STATUS", statusDetail);
        }
    };

    public DataListener<SimStatusDetail> answerSimStatus = new DataListener<>() {
        @Override
        public void onData(SocketIOClient client, SimStatusDetail statusDetail, AckRequest ackRequest) {
            Map<String, String> response = new HashMap<>();
            response.put("code", "200");
            ackRequest.sendAckData(response);
            Date now = new Date();
            statusDetail.setSessionID(client.getSessionId());
            statusDetail.setDate(now);
            statusDetail.setStatus("I am running one!");
            socketServer.getBroadcastOperations().sendEvent("BROADCAST_ANSWER_SIM_STATUS", statusDetail);
        }
    };

    public DataListener<SimulationDetail> simulateNewOrder = new DataListener<>() {
        @Override
        public void onData(SocketIOClient client, SimulationDetail simDetail, AckRequest ackRequest) {
            //log.info("id: " + client.getSessionId());
            NewOrderEvent newOrder = new NewOrderEvent(this, simDetail.getSimulationDataList(), client.getSessionId());
            eventPublisher.publishOne(newOrder);
            Map<String, String> response = new HashMap<>();
            response.put("status", "200");
            response.put("message", "new order created");
            ackRequest.sendAckData(response); 
        }
    };

    public DataListener<DiverPromptDetail> handleDiverPrompt = new DataListener<>() {
        @Override
        public void onData(SocketIOClient client, DiverPromptDetail prompt, AckRequest ackRequest) {
            int maxRetries = 5;
            int attempt = 0;
            LocalDateTime now = LocalDateTime.now();
            while (attempt < maxRetries) {
                try {
                    String userPrompt = prompt.getText();
                    String systemPrompt = Prompt.DIVER_INSTRUCT_PROMPT.format(userPrompt, now);
                    ResponseEntity<GeminiRes> respEntity = genSQLService.askGeminiAPI(systemPrompt);
                    GeminiRes res = null;
                    String geminiQuery = null;
                    String natLangResponse = null;
                    int responseCode = respEntity.getStatusCode().value();

                    if (responseCode == HttpStatus.ACCEPTED.value()) {
                        res = respEntity.getBody();
                        geminiQuery = res.getCandidates().get(0).getContent().getParts().get(0).getText();
                        log.info("geminiQuery: " + genSQLService.cleanSQL(geminiQuery));
                        List<Map<String, Object>> result = genSQLService.runQuery(genSQLService.cleanSQL(geminiQuery));
                        ObjectMapper objectMapper = new ObjectMapper();
                        String resultStr = objectMapper.writeValueAsString(result);
                        log.info("resultStr: " + resultStr);
                        log.info("userPrompt: " + userPrompt);
                        systemPrompt = Prompt.DIVER_REPLIER_PROMPT.format(resultStr, userPrompt, now);

                    } else {
                        client.sendEvent("DIVER_RESPONSE",
                                "Error: Gemini API returned " + responseCode + " during query building.");
                        return;
                    }

                    respEntity = genSQLService.askGeminiAPI(systemPrompt);
                    responseCode = respEntity.getStatusCode().value();

                    if (responseCode == HttpStatus.ACCEPTED.value()) {
                        res = respEntity.getBody();
                        natLangResponse = res.getCandidates().get(0).getContent().getParts().get(0).getText();
                        Map<String, String> fullResponse = new HashMap<>();
                        fullResponse.put("response", natLangResponse);
                        fullResponse.put("query", genSQLService.cleanSQL(geminiQuery));
                        client.sendEvent("DIVER_RESPONSE", fullResponse);
                        // log.info("id: " + client.getSessionId());
                        return;
                    } else {
                        client.sendEvent("DIVER_RESPONSE",
                                "Error: Gemini API returned " + responseCode + " during query building.");
                        return;
                    }
                } catch (org.springframework.jdbc.BadSqlGrammarException e) {
                    attempt++;
                    if (attempt >= maxRetries) {
                        client.sendEvent("DIVER_RESPONSE", "Gemini could not correctly produce query for this prompt.");
                    } else {
                        client.sendEvent("DIVER_RESPONSE", "Gemini built BadSqlGrammarException query, retrying... " + attempt + "/" + maxRetries);
                    }
                } catch (Exception e) {
                    log.error("Exception:", e);
                    client.sendEvent("DIVER_RESPONSE", e);
                }
            }
        }
    };

    public void broadcastMetricSocketEvent(String eventStr, Metric metric) {
        socketServer.getBroadcastOperations().sendEvent(eventStr, metric);
    }

    public void broadcastSimulatedOrderSocketEvent(String eventStr, Simulation sim) {
        socketServer.getBroadcastOperations().sendEvent(eventStr, sim);
    }

}
