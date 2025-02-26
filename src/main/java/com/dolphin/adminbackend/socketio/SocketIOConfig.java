package com.dolphin.adminbackend.socketio;

import com.corundumstudio.socketio.AuthorizationResult;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.dolphin.adminbackend.auth.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Objects;

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
    @Value("${allowed.origin}")
    private String allowedOrigin;

    // create a socket server
    private SocketIOServer server;

    @PostConstruct
    public void init() {
        log.info("Socket Host: " + socketHost);
        log.info("Socket Port: " + socketPort);
    }

    @Bean
    public SocketIOServer socketIOServer() {
        log.info("Attempting socket conn");
        Configuration privateSocketConfig = new Configuration();
        privateSocketConfig.setHostname(socketHost);
        log.info("Socket host name:" + privateSocketConfig.getHostname());
        privateSocketConfig.setPort(socketPort);
        privateSocketConfig.setOrigin(allowedOrigin);
        privateSocketConfig.setTransports(com.corundumstudio.socketio.Transport.POLLING,
                com.corundumstudio.socketio.Transport.WEBSOCKET);
        /*
         * Initializes and starts the Socket.IO server to accept connections.
         * Without this, the server won’t run, and clients cannot connect.
         */
        server = new SocketIOServer(privateSocketConfig);
        server.start();
        /*
         * This is an event listener that triggers when a client successfully connects.
         */
        server.addConnectListener(client -> {
            log.info("onConnectListener called");
            HandshakeData handshakeData = client.getHandshakeData();

            // Why use query param to send jwt ?
            // Postman use polling, which has http headers.
            // Due to CORS, polling from browsers has to be disabled
            // client side code "transports: [websocket]" disables polling, which inherently
            // ignores extraHeaders on browsers
            // disable polling on browsers to avoid CORS error because this git branch only
            // listens to port 8081 (google cloud 1 service 1 port),
            // which does not recognize http requests
            // client side code "auth: {token: "..."}" also does not work with
            // netty-socketio
            // because handshakeData.getAuthToken() returns null.

            String queryJwt = handshakeData.getSingleUrlParam("token"); // browsers
            String headerJwt = handshakeData.getHttpHeaders().get("token"); // postman
            String origin = handshakeData.getHttpHeaders().get("Origin");

            if (!Objects.equals(origin, allowedOrigin) || origin == null) {
                log.info("Authentication FAILED, unknown origin: " + origin);
                client.disconnect();
                return;
            }

            if (queryJwt == null && headerJwt == null) {
                log.info("Authentication FAILED, missing token");
                client.disconnect();
                return;
            } 

            Claims claims = jwtUtil.parseJwtClaims(queryJwt != null ? queryJwt : headerJwt);
            if (claims != null) {
                String userFullName = claims.getSubject();
                client.set("User", userFullName);
                log.info("Authenticated PASSED");
            } else {
                log.info("Authentication FAILED, claims is null");
                client.disconnect();
            }
        });

        server.addDisconnectListener(client -> log.info("Client disconnected: {}", client.getSessionId()));

        return server;


        // privateSocketConfig.setAllowHeaders("auth,content-type");
        // privateSocketConfig.setAuthorizationListener(data -> {
        // log.info("********** Authorization Listener CALLED **********"); // Crucial
        // check
        // String headerToken = data.getHttpHeaders().get("Authorization");
        // String paramToken = data.getSingleUrlParam("token");
        // String cookieToken = null;

        // List<Entry<String, String>> headers = data.getHttpHeaders().entries();
        // if (headerToken == null) { // somehow sometimes the Authorization header is
        // undetected from browser origins
        // for (Entry<String, String> entry : headers) {
        // // get Authorization from Cookie header instead
        // if (entry.getKey().equals("Cookie")) {
        // cookieToken = extractAuthorizationToken(entry.getValue());
        // // System.out.println("cookie token: " + cookieToken);
        // token = cookieToken;
        // break;
        // }
        // }
        // } else {
        // token = headerToken; // postman authorization header works (but only locally)
        // }

        // // prioritize query param
        // if (paramToken != null) {
        // token = paramToken;
        // }

        // //debug
        // log.info("headerToken: " + headerToken);
        // log.info("paramToken: " + paramToken);
        // log.info("cookieToken: " + cookieToken);
        // // log.info("objectToken: " + data.getAuthToken().toString());
        // log.info("url: " + data.getUrl());
        // log.info("address: " + data.getAddress());
        // Map<String, List<String>> params = data.getUrlParams();
        // log.info("params: ");
        // for (String key : params.keySet()) {
        // log.info(key + ": " + params.get(key));
        // }

        // if (token != null) {
        // Claims claims = jwtUtil.parseJwtClaims(token); // token validation happens
        // here
        // if (claims != null) {
        // String userFullName = claims.getSubject();
        // data.getHttpHeaders().add("User", userFullName);
        // return new AuthorizationResult(true);
        // }
        // }
        // return new AuthorizationResult(false);
        // });

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
