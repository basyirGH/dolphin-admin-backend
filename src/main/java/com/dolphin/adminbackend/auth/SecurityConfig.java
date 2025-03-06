package com.dolphin.adminbackend.auth;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.dolphin.adminbackend.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    ObjectMapper objectMapper = new ObjectMapper();

    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
            JwtUtil jwtUtil) {
        this.userDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil;

    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, NoOpPasswordEncoder noOpPasswordEncoder)
            throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(noOpPasswordEncoder);
        return authenticationManagerBuilder.build();
    }

    // Why not just anyRequest().permitAll() and rely on the filter?

    // You might be tempted to just use .anyRequest().permitAll() and rely solely on
    // the JwtAuthorizationFilter to handle the guest and login paths. While this
    // might technically work, it's strongly discouraged.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Cross-Site Request Forgery (CSRF) protection is disabled. This is often done
                // for stateless APIs where authentication is handled via tokens (e.g., JWT)
                // instead of session cookies.
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/guest", "/socket.io/**")
                        .permitAll()
                        .anyRequest().authenticated()) // any other requests from permitAll above must be authenticated
                .addFilterBefore(new JwtAuthorizationFilter(jwtUtil, objectMapper),
                        UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> {
                    ex.authenticationEntryPoint(
                            (request, response, authException) -> {
                                response.sendError(401, "Unauthorized");

                                // Get the stack trace as a String
                                StringWriter sw = new StringWriter();
                                PrintWriter pw = new PrintWriter(sw);
                                authException.printStackTrace(pw);
                                String stackTrace = sw.toString();

                                // Log the stack trace
                                log.error("Authentication Exception: " + authException.getMessage()); // Log the message
                                                                                                      // too
                                log.error(stackTrace); // Log the full stack trace

                                // Or, for more structured logging (recommended):
                                log.error("Authentication Exception", authException); // This logs the exception *and*
                                                                                      // its stack trace

                            });
                    ex.accessDeniedHandler((request, response, authException) -> {
                        response.sendError(403, "Forbidden");

                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        authException.printStackTrace(pw);
                        String stackTrace = sw.toString();

                        log.error("Access Denied Exception: " + authException.getMessage());
                        log.error(stackTrace);

                        log.error("Access Denied Exception", authException);
                    });

                })
                .build();
    }

    /*
     * Below ensures that requests made from one origin (e.g., your React app at
     * http://localhost:5000) to another origin (e.g., your Spring Boot backend at
     * http://localhost:8080) are allowed only if the server explicitly permits it.
     * 
     * Postman is not a web browser and does not enforce CORS (Cross-Origin Resource
     * Sharing) policies. CORS is a browser security feature designed to prevent
     * unauthorized requests from a different origin.
     * 
     * Here's a breakdown of why this happens:
     * 
     * 1. CORS is Browser-Specific
     * When a browser sends a request to a server from a different origin (e.g.,
     * from http://localhost:5000 to http://localhost:8080), it performs a CORS
     * check.
     * If the server does not include the appropriate CORS headers in its response
     * (like Access-Control-Allow-Origin), the browser blocks the response.
     * Postman bypasses this check because it is not bound by browser security
     * features. It directly sends the request and receives the response without any
     * CORS-related restrictions.
     * 
     * 2. Preflight Requests
     * For certain HTTP methods (like POST, PUT, or DELETE) or when custom headers
     * (e.g., Authorization) are included, the browser performs a preflight request.
     * A preflight request is an OPTIONS request sent before the actual request to
     * check if the server allows the requested origin, method, and headers.
     * If the server doesn’t handle this OPTIONS request or doesn’t include the
     * correct CORS headers, the browser blocks the actual request.
     * 
     * 3. Server-Side Responsibility
     * It’s the responsibility of the server (your Spring Boot app) to respond with
     * the correct CORS headers (Access-Control-Allow-Origin, etc.) for the browser
     * to accept the response.
     * Since Postman doesn’t enforce CORS, it works without the need for these
     * headers.
     */

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*"); // Allow React app
        configuration.addAllowedMethod("*"); // Allow all HTTP methods
        configuration.addAllowedHeader("*"); // Allow all headers

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @SuppressWarnings("deprecation")
    @Bean
    public NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }

}
