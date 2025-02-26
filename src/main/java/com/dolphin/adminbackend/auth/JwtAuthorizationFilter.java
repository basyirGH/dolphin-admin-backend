package com.dolphin.adminbackend.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dolphin.adminbackend.enums.CustomAPICode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
//to make sure every api request is authenticated
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper mapper;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, ObjectMapper mapper) {
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;
    }

    // This method will be called for every request to the application. This method
    // reads Bearer token from request headers and resolves claims
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Map<String, Object> errorDetails = new HashMap<>();

        // skip token check for public requests
        boolean isHealthRequest = "/".equals(request.getRequestURI());
        boolean isLoginRequest = "/api/v1/auth/login".equals(request.getRequestURI());
        boolean isGuestRequest = "/api/v1/auth/guest".equals(request.getRequestURI());
        if (isHealthRequest || isLoginRequest || isGuestRequest) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // token check other than login requests
            String accessToken = jwtUtil.resolveToken(request);

            if (accessToken == null) {
                errorDetails.put("code", CustomAPICode.MISSING_TOKEN);
                errorDetails.put("message", "You must authenticate/login first to retrieve the access token.");
                response.setStatus(HttpStatus.UNAUTHORIZED.value()); // HTTP 401 Unauthorized
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                mapper.writeValue(response.getWriter(), errorDetails); // Write the error response
                return; // Stop further processing
            } 
            // As soon as this passes, it's kinda like token validation is already done here
            // validateClaims is just checking expiry date
            Claims claims = jwtUtil.resolveClaims(request);

            if (claims != null & jwtUtil.validateClaims(claims)) {
                String email = claims.getSubject(); 
                //System.out.println("email : " + email);
                Authentication authentication = new UsernamePasswordAuthenticationToken(email, "", new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch(ExpiredJwtException e) {
            errorDetails.put("code", CustomAPICode.TOKEN_EXPIRED);
            errorDetails.put("message", e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            mapper.writeValue(response.getWriter(), errorDetails);

        } catch (Exception e) {
            errorDetails.put("message", e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            mapper.writeValue(response.getWriter(), errorDetails);

        }
        filterChain.doFilter(request, response);
    }
}
