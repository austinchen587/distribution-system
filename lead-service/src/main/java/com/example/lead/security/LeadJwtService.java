package com.example.lead.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class LeadJwtService {

    @Value("${jwt.secret:mySecretKeyForDistributionSystemJWT2024}")
    private String secret;

    private Key signingKey;

    @PostConstruct
    public void init() {
        // 确保密钥长度足够（HS256 需要 >= 32 bytes）
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        this.signingKey = Keys.hmacShaKeyFor(bytes);
    }

    public boolean validate(String token) {
        try {
            parse(token); // 会校验签名与过期
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUserId(String token) {
        Claims c = parse(token);
        String userId = c.get("userId", String.class);
        if (userId == null) userId = c.getSubject();
        return userId;
    }

    public String getRole(String token) {
        Claims c = parse(token);
        return c.get("role", String.class);
    }
}

