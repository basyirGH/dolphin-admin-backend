package com.dolphin.adminbackend.utility;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Slf4j
public class IPHasher {

    @Value("${iphasher.secret}")
    private String SECRET_KEY; 

    @PostConstruct
    public void init() { // Use @PostConstruct for initialization
        if (SECRET_KEY == null || SECRET_KEY.isEmpty()) {
            log.error("iphasher.secret environment variable is not set!");
            throw new RuntimeException("iphasher.secret must be set."); 
        }  
    }

    public String hashIp(String ip) {
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSha256.init(secretKeySpec);

            byte[] hashBytes = hmacSha256.doFinal(ip.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing IP", e);
        }
    }
}
