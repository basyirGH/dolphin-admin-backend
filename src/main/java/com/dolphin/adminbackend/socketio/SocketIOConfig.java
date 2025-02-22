package com.dolphin.adminbackend.socketio;

import com.corundumstudio.socketio.AuthorizationResult;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.dolphin.adminbackend.auth.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
/*
 * This constructor will include all final fields
 * and any fields annotated with @NonNull, ensuring
 * that these required fields are initialized when
 * an instance of the class is created.
 */
@RequiredArgsConstructor
@Slf4j
public class SocketIOConfig {

    private final JwtUtil jwtUtil;

    @Value("${socket.host:0.0.0.0}") // Default to 0.0.0.0 if not set
    private String socketHost;
    @Value("${socket.port:8081}") // Default port
    private int socketPort;

    // create a socket server
    private SocketIOServer server;

    @PostConstruct
    public void init() {
        log.info("Socket Host: " + socketHost);
        log.info("Socket Port: " + socketPort);
    }

    @Bean
    public SocketIOServer socketIOServer() {
        log.info("__________attempting socket conn");
        Configuration privateSocketConfig = new Configuration();
        privateSocketConfig.setHostname(socketHost);
        log.info("__________socket host name:" + privateSocketConfig.getHostname());
        privateSocketConfig.setPort(socketPort);
        privateSocketConfig.setOrigin("*");
        privateSocketConfig.setAllowHeaders("authorization,content-type");
        log.info("********** Pre-Authorization Listener **********"); 

        privateSocketConfig.setAuthorizationListener(data -> {
            log.info("********** Authorization Listener CALLED **********"); // Crucial check
            String token = null;
            String headerToken = data.getHttpHeaders().get("Authorization");
            String paramToken = data.getSingleUrlParam("token");
            String cookieToken = null;

            List<Entry<String, String>> headers = data.getHttpHeaders().entries();
            if (headerToken == null) { // somehow the Authorization header is undetected from browser origins
                for (Entry<String, String> entry : headers) {
                    // get Authorization from Cookie header instead
                    if (entry.getKey().equals("Cookie")) {
                        cookieToken = extractAuthorizationToken(entry.getValue());
                        // System.out.println("cookie token: " + cookieToken);
                        token = cookieToken;
                        break;
                    }
                }
            } else {
                token = headerToken; // postman authorization header works (but only locally)
            }

            // prioritize query param
            if (paramToken != null) {
                token = paramToken;
            }

            //debug
            log.info("headerToken: " + headerToken);
            log.info("paramToken: " + paramToken);
            log.info("cookieToken: " + cookieToken);
            log.info("objectToken: " + data.getAuthToken().toString());
            log.info("url: " + data.getUrl());
            log.info("address: " + data.getAddress());
            Map<String, List<String>> params = data.getUrlParams();
            log.info("params: ");
            for (String key : params.keySet()) {
                log.info(key + ": " + params.get(key));
            } 

            if (token != null) {
                Claims claims = jwtUtil.parseJwtClaims(token); // token validation happens here
                if (claims != null) {
                    String userFullName = claims.getSubject();
                    data.getHttpHeaders().add("User", userFullName);
                    return new AuthorizationResult(true);
                }
            }
            return new AuthorizationResult(false);
        });

        // Configuration publicSocketConfig = new Configuration();
        // publicSocketConfig.setHostname(socketHost);
        // publicSocketConfig.setPort(socketPort);
        // publicSocketConfig.setOrigin("http://localhost:5000");
        // publicSocketConfig.setAllowHeaders("content-type");

        server = new SocketIOServer(privateSocketConfig);
        server.start();

        server.addConnectListener(client -> log.info("Client connected: {}", client.getSessionId()));
        server.addDisconnectListener(client -> log.info("Client disconnected: {}", client.getSessionId()));

        return server;
    }

    /*
     * Whenever there is a call to shut down the Spring Container,
     * 
     * @PreDestroy annotation calls the method to which it is annotated.
     * As a result, it will ensure to close the SocketIO server properly
     * before shutting down the application, implementing a better clean-up
     * mechanism.
     */
    @PreDestroy
    public void stopSocketServer() {
        this.server.stop();
    }

    public static String extractAuthorizationToken(String cookieHeader) {
        // Split the cookie string into individual cookies
        String[] cookies = cookieHeader.split(";");

        // Loop through each cookie and find the 'Authorization' cookie
        for (String cookie : cookies) {
            cookie = cookie.trim(); // Remove leading/trailing spaces
            if (cookie.startsWith("Authorization=")) {
                // Extract the token by splitting on '=' and returning the second part
                return cookie.split("=")[1];
            }
        }
        return null; // Return null if the Authorization cookie is not found
    }

}
