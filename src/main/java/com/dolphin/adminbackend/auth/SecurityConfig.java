package com.dolphin.adminbackend.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.dolphin.adminbackend.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
            JwtAuthorizationFilter jwtAuthorizationFilter) {
        this.userDetailsService = customUserDetailsService;
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;

    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, NoOpPasswordEncoder noOpPasswordEncoder)
            throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(noOpPasswordEncoder);
        return authenticationManagerBuilder.build();
    }

    // we want every request to be authenticated before going through spring
    // security filter.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .cors() // Enable CORS
                .and()
                .authorizeRequests()
                .requestMatchers("/rest/auth/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
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
        configuration.addAllowedOrigin("http://localhost:5000"); // Allow React app
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
