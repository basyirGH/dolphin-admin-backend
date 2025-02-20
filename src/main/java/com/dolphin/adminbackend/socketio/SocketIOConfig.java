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
        log.info("****************attempting socket conn");
        Configuration privateSocketConfig = new Configuration();
        privateSocketConfig.setHostname(socketHost);
        log.info("******************socket host name:" + privateSocketConfig.getHostname());
        privateSocketConfig.setPort(socketPort);
        privateSocketConfig.setOrigin("*");
        privateSocketConfig.setAllowHeaders("authorization,content-type");

        // Authorization listener
        privateSocketConfig.setAuthorizationListener(data -> {
            String token = null;
            String headerToken = data.getHttpHeaders().get("Authorization");
            String cookieToken = null;
            List<Entry<String, String>> headers = data.getHttpHeaders().entries();
            if (headerToken == null) { // somehow the Authorization header is undetected from browser origins
                for (Entry<String, String> entry : headers) {
                    // System.out.println("(header) " + entry.getKey() + ": " + entry.getValue());
                    if (entry.getKey().equals("Cookie")) { // get Authorization from Cookie header instead
                        cookieToken = extractAuthorizationToken(entry.getValue());
                        // System.out.println("cookie token: " + cookieToken);
                        token = cookieToken;
                        break;
                    }
                }
            } else {
                token = headerToken; // postman authorization header works
            }
            // System.out.println("token: " + data.getHttpHeaders().get("Authorization"));
            // log.info("data.authtoken: ", data.getAuthToken().toString()); // null
            if (token != null) {
                Claims claims = jwtUtil.parseJwtClaims(token); // token validation happens here
                String userFullName = claims.getSubject();
                data.getHttpHeaders().add("User", userFullName);
                return new AuthorizationResult(true);
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
