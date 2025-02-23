// This class will be used for creating & resolving jwt tokens.

package com.dolphin.adminbackend.auth;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.dolphin.adminbackend.model.jpa.User;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;
    private long accessTokenValidity = 1000 * 60 * 60 * 24;

    private JwtParser jwtParser;

    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

    @PostConstruct
    public void init() { // Use @PostConstruct for initialization
        if (secretKey == null || secretKey.isEmpty()) {
            log.error("JWT_SECRET_KEY environment variable is not set!");
            throw new RuntimeException("JWT_SECRET_KEY must be set."); // Or handle differently
        }

        jwtParser = Jwts.parser().setSigningKey(secretKey);
        log.info("JWT Secret Key: LOADED");

        // Log the *encoded* key only in development, NEVER in production
        // log.debug("Encoded JWT Secret Key (DEV ONLY): " + encodedSecretKey);
    }

    // public JwtUtil() {
    //     this.jwtParser = Jwts.parser().setSigningKey(secret_key);
    // }

    public String createToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put("fullName", user.getFullName());
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + accessTokenValidity);
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(tokenValidity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Claims resolveClaims(HttpServletRequest req) {
        try {
            String token = resolveToken(req);
            if (token != null) {
                return parseJwtClaims(token);
            }
            return null;
        } catch (ExpiredJwtException ex) {
            req.setAttribute("expired", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            req.setAttribute("invalid", ex.getMessage());
            throw ex;
        }
    }

    public Claims parseJwtClaims(String token) {
        /*
         * parseClaimsJws inherently performs all the required validations
         * (signature, expiration, format, etc.).
         */
        try {
            // Parse the token and validate its signature and expiration
            return jwtParser.parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            System.out.println("Token has expired: " + e.getMessage());
            throw e; // Optionally, re-throw or handle as needed
        } catch (UnsupportedJwtException e) {
            System.out.println("Unsupported JWT: " + e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            System.out.println("Malformed JWT: " + e.getMessage());
            throw e;
        } catch (SignatureException e) {
            System.out.println("Invalid JWT signature: " + e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            System.out.println("Illegal argument in token: " + e.getMessage());
            throw e;
        }
    }

    public String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    /*
     * validateToken method already validates the token 
     * (which inherently includes checking the expiration date via parseClaimsJws), 
     * then it’s redundant to call validateClaims.
     */
    public boolean validateClaims(Claims claims) throws AuthenticationException {
        try {
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            throw e;
        }
    }

    public String getEmail(Claims claims) {
        return claims.getSubject();
    }

    private List<String> getRoles(Claims claims) {
        return (List<String>) claims.get("roles");
    }

}
