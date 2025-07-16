package com.example.gateway.filter;

import com.example.common.dto.ApiResponse;
import com.example.common.utils.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * JWT认证过滤器
 */
@Slf4j
@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {
    
    @Value("${auth.whitelist:}")
    private String[] whitelistArray;
    
    private List<String> whitelist;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    public AuthFilter() {
        super(Config.class);
    }
    
    @PostConstruct
    public void init() {
        if (whitelistArray != null) {
            whitelist = Arrays.asList(whitelistArray);
        } else {
            whitelist = Arrays.asList(
                "/api/auth/register",
                "/api/auth/login", 
                "/api/auth/send-code",
                "/api/auth/test-login",
                "/api/auth/user-info"
            );
        }
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            
            // 检查是否在白名单中
            if (isWhitelisted(path)) {
                return chain.filter(exchange);
            }
            
            // 获取token
            String token = extractToken(request);
            
            if (!StringUtils.hasText(token)) {
                return onError(exchange, "未提供认证token", HttpStatus.UNAUTHORIZED);
            }
            
            // 验证token
            try {
                if (!jwtUtils.validateToken(token)) {
                    return onError(exchange, "token无效或已过期", HttpStatus.UNAUTHORIZED);
                }
                
                // 解析token获取用户信息  
                String userId = jwtUtils.getUserIdFromToken(token);
                String role = jwtUtils.getRoleFromToken(token);
                
                // 将用户信息添加到请求头中，传递给下游服务
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Id", userId)
                        .header("X-User-Role", role)
                        .build();
                
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
                
            } catch (Exception e) {
                log.error("Token验证失败", e);
                return onError(exchange, "token验证失败", HttpStatus.UNAUTHORIZED);
            }
        };
    }
    
    /**
     * 提取token
     */
    private String extractToken(ServerHttpRequest request) {
        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }
    
    /**
     * 检查是否在白名单中
     */
    private boolean isWhitelisted(String path) {
        return whitelist.stream().anyMatch(path::startsWith);
    }
    
    /**
     * 处理认证错误
     */
    private Mono<Void> onError(org.springframework.web.server.ServerWebExchange exchange, 
                               String message, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        ApiResponse<?> apiResponse = ApiResponse.error(httpStatus.value(), message);
        
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(apiResponse);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("响应序列化失败", e);
            return response.setComplete();
        }
    }
    
    public static class Config {
        // 配置类，可以添加自定义配置
    }
}