package com.example.common.interceptor;

import com.example.common.utils.JwtUtils;
import com.example.common.utils.UserContextHolder;
import com.example.common.constants.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtInterceptor.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        // 跳过认证的路径
        if (shouldSkipAuthentication(requestURI, method)) {
            return true;
        }
        
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            logger.warn("请求缺少Authorization头: {}", requestURI);
            writeErrorResponse(response, ErrorCode.UNAUTHORIZED);
            return false;
        }
        
        String token = authHeader.substring(BEARER_PREFIX.length());
        
        try {
            if (JwtUtils.validateToken(token)) {
                String userId = JwtUtils.getUserIdFromToken(token);
                String role = JwtUtils.getRoleFromToken(token);
                
                UserContextHolder.setContext(new UserContextHolder.UserContext(userId, role));
                logger.debug("用户认证成功: userId={}, role={}", userId, role);
                return true;
            } else {
                logger.warn("Token验证失败: {}", requestURI);
                writeErrorResponse(response, ErrorCode.AUTH_005);
                return false;
            }
        } catch (Exception e) {
            logger.error("Token解析异常: {}", e.getMessage());
            writeErrorResponse(response, ErrorCode.AUTH_005);
            return false;
        }
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContextHolder.clear();
    }
    
    private boolean shouldSkipAuthentication(String requestURI, String method) {
        // 跳过认证的路径
        return requestURI.startsWith("/api/auth/") ||
               requestURI.startsWith("/swagger-ui/") ||
               requestURI.startsWith("/v3/api-docs") ||
               requestURI.startsWith("/actuator/health") ||
               requestURI.equals("/favicon.ico") ||
               "OPTIONS".equals(method);
    }
    
    private void writeErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpCode());
        response.setContentType("application/json;charset=UTF-8");
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", errorCode.getHttpCode());
        result.put("success", false);
        result.put("message", errorCode.getMessage());
        result.put("data", Map.of("error_code", errorCode.getErrorCode()));
        result.put("timestamp", System.currentTimeMillis());
        
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}