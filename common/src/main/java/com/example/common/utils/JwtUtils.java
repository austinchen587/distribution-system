package com.example.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 提供JWT令牌的生成、解析和验证功能
 */
public class JwtUtils {
    
    private static final String SECRET_KEY = "mySecretKeyForDistributionSystemJWT2024";
    private static final Key SIGNING_KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 24小时
    private static final long REFRESH_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000; // 7天
    
    private JwtUtils() {
        // 私有构造方法，防止实例化
    }
    
    /**
     * 生成访问令牌
     * 
     * @param userId 用户ID
     * @param role 用户角色
     * @return JWT令牌
     */
    public static String generateToken(String userId, String role) {
        return generateToken(userId, role, EXPIRATION_TIME);
    }
    
    /**
     * 生成刷新令牌
     * 
     * @param userId 用户ID
     * @param role 用户角色
     * @return JWT刷新令牌
     */
    public static String generateRefreshToken(String userId, String role) {
        return generateToken(userId, role, REFRESH_EXPIRATION_TIME);
    }
    
    /**
     * 生成JWT令牌
     * 
     * @param userId 用户ID
     * @param role 用户角色
     * @param expirationTime 过期时间（毫秒）
     * @return JWT令牌
     */
    private static String generateToken(String userId, String role, long expirationTime) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * 解析JWT令牌
     * 
     * @param token JWT令牌
     * @return 令牌声明
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SIGNING_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 从令牌中获取用户ID
     * 
     * @param token JWT令牌
     * @return 用户ID
     */
    public static String getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", String.class);
    }
    
    /**
     * 从令牌中获取用户角色
     * 
     * @param token JWT令牌
     * @return 用户角色
     */
    public static String getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }
    
    /**
     * 获取令牌过期时间
     * 
     * @param token JWT令牌
     * @return 过期时间戳
     */
    public static long getExpirationFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().getTime();
    }
    
    /**
     * 检查令牌是否已过期
     * 
     * @param token JWT令牌
     * @return 是否已过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
    
    /**
     * 验证令牌是否有效
     * 
     * @param token JWT令牌
     * @return 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取令牌剩余有效时间（秒）
     * 
     * @param token JWT令牌
     * @return 剩余有效时间（秒）
     */
    public static long getRemainingTime(String token) {
        try {
            Claims claims = parseToken(token);
            long expiration = claims.getExpiration().getTime();
            long now = System.currentTimeMillis();
            return Math.max(0, (expiration - now) / 1000);
        } catch (Exception e) {
            return 0;
        }
    }
}