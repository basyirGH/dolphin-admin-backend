package com.dolphin.adminbackend.auth;

import com.dolphin.adminbackend.constants.CustomAPICode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//to make sure every api request is authenticated
@Component
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

        // skip token check for login requests
        boolean isLoginRequest = "/rest/auth/login".equals(request.getRequestURI());
        if (isLoginRequest) {
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

                // Write the error response
                mapper.writeValue(response.getWriter(), errorDetails);
                return; // Stop further processing
            }
            System.out.println("token : " + accessToken);
            Claims claims = jwtUtil.resolveClaims(request);

            if (claims != null & jwtUtil.validateClaims(claims)) {
                String email = claims.getSubject(); 
                System.out.println("email : " + email);
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
