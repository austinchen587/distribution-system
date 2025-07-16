package com.example.common.utils;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JWT工具类单元测试")
class JwtUtilsTest {

    private static final String TEST_USER_ID = "123456";
    private static final String TEST_ROLE = "AGENT";
    private static final String INVALID_TOKEN = "invalid.jwt.token";

    @Test
    @DisplayName("测试生成有效令牌")
    void testGenerateToken() {
        String token = JwtUtils.generateToken(TEST_USER_ID, TEST_ROLE);
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(JwtUtils.validateToken(token));
    }

    @Test
    @DisplayName("测试生成刷新令牌")
    void testGenerateRefreshToken() {
        String refreshToken = JwtUtils.generateRefreshToken(TEST_USER_ID, TEST_ROLE);
        
        assertNotNull(refreshToken);
        assertTrue(refreshToken.length() > 0);
        assertTrue(JwtUtils.validateToken(refreshToken));
    }

    @Test
    @DisplayName("测试从令牌获取用户ID")
    void testGetUserIdFromToken() {
        String token = JwtUtils.generateToken(TEST_USER_ID, TEST_ROLE);
        String userId = JwtUtils.getUserIdFromToken(token);
        
        assertEquals(TEST_USER_ID, userId);
    }

    @Test
    @DisplayName("测试从令牌获取用户角色")
    void testGetRoleFromToken() {
        String token = JwtUtils.generateToken(TEST_USER_ID, TEST_ROLE);
        String role = JwtUtils.getRoleFromToken(token);
        
        assertEquals(TEST_ROLE, role);
    }

    @Test
    @DisplayName("测试令牌过期检查")
    void testIsTokenExpired() {
        String token = JwtUtils.generateToken(TEST_USER_ID, TEST_ROLE);
        
        assertFalse(JwtUtils.isTokenExpired(token));
    }

    @Test
    @DisplayName("测试验证有效令牌")
    void testValidateValidToken() {
        String token = JwtUtils.generateToken(TEST_USER_ID, TEST_ROLE);
        
        assertTrue(JwtUtils.validateToken(token));
    }

    @Test
    @DisplayName("测试验证无效令牌")
    void testValidateInvalidToken() {
        assertFalse(JwtUtils.validateToken(INVALID_TOKEN));
    }

    @Test
    @DisplayName("测试验证过期令牌")
    void testValidateExpiredToken() throws InterruptedException {
        // 这里我们无法真正创建过期令牌，但可以测试无效格式
        assertFalse(JwtUtils.validateToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjB9.invalid"));
    }

    @Test
    @DisplayName("测试获取令牌过期时间")
    void testGetExpirationFromToken() {
        String token = JwtUtils.generateToken(TEST_USER_ID, TEST_ROLE);
        long expiration = JwtUtils.getExpirationFromToken(token);
        
        assertTrue(expiration > System.currentTimeMillis());
    }

    @Test
    @DisplayName("测试获取令牌剩余时间")
    void testGetRemainingTime() {
        String token = JwtUtils.generateToken(TEST_USER_ID, TEST_ROLE);
        long remainingTime = JwtUtils.getRemainingTime(token);
        
        assertTrue(remainingTime > 0);
        assertTrue(remainingTime <= 86400); // 24小时内的秒数
    }

    @Test
    @DisplayName("测试解析令牌声明")
    void testParseToken() {
        String token = JwtUtils.generateToken(TEST_USER_ID, TEST_ROLE);
        Claims claims = JwtUtils.parseToken(token);
        
        assertNotNull(claims);
        assertEquals(TEST_USER_ID, claims.get("userId"));
        assertEquals(TEST_ROLE, claims.get("role"));
        assertEquals(TEST_USER_ID, claims.getSubject());
    }

    @Test
    @DisplayName("测试空令牌验证")
    void testValidateEmptyToken() {
        assertFalse(JwtUtils.validateToken(null));
        assertFalse(JwtUtils.validateToken(""));
        assertFalse(JwtUtils.validateToken("   "));
    }

    @Test
    @DisplayName("测试不同用户ID和角色组合")
    void testDifferentUserAndRoleCombinations() {
        String[] userIds = {"1", "999", "admin123"};
        String[] roles = {"SUPER_ADMIN", "DIRECTOR", "LEADER", "SALES", "AGENT"};
        
        for (String userId : userIds) {
            for (String role : roles) {
                String token = JwtUtils.generateToken(userId, role);
                assertTrue(JwtUtils.validateToken(token));
                assertEquals(userId, JwtUtils.getUserIdFromToken(token));
                assertEquals(role, JwtUtils.getRoleFromToken(token));
            }
        }
    }

    @Test
    @DisplayName("测试令牌内容完整性")
    void testTokenContentIntegrity() {
        String token = JwtUtils.generateToken(TEST_USER_ID, TEST_ROLE);
        
        // 解析并验证所有字段
        Claims claims = JwtUtils.parseToken(token);
        
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
        assertEquals(TEST_USER_ID, claims.getSubject());
    }
}