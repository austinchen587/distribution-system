package com.example.gateway.filter;

import com.example.common.dto.CommonResult;
import com.example.common.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * 全局认证过滤器
 * 应用于所有路由的统一认证处理
 */
@Component
public class GlobalAuthFilter implements GlobalFilter, Ordered {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalAuthFilter.class);
    
    @Value("${auth.whitelist:}")
    private String[] whitelistArray;
    
    private List<String> whitelist;
    
    private final ObjectMapper objectMapper;
    
    public GlobalAuthFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @PostConstruct
    public void init() {
        if (whitelistArray != null && whitelistArray.length > 0) {
            whitelist = Arrays.asList(whitelistArray);
        } else {
            // 默认白名单配置
            whitelist = Arrays.asList(
                "/api/auth/register",
                "/api/auth/login", 
                "/api/auth/send-code",
                "/api/auth/refresh",
                "/api/auth/logout",
                "/swagger-ui",
                "/v3/api-docs",
                "/actuator",
                "/favicon.ico",
                "/webjars",
                "/swagger-resources"
            );
        }
    }
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        log.debug("处理请求: {}", path);
        
        // 跳过白名单路径的认证
        if (isWhitelisted(path)) {
            log.debug("跳过认证: {}", path);
            return chain.filter(exchange);
        }
        
        // 获取token
        String token = extractToken(request);
        
        if (!StringUtils.hasText(token)) {
            log.warn("请求缺少认证token: {}", path);
            return onError(exchange, "未提供认证token", HttpStatus.UNAUTHORIZED);
        }
        
        // 验证token
        try {
            if (!JwtUtils.validateToken(token)) {
                log.warn("Token无效或已过期: {}", path);
                return onError(exchange, "token无效或已过期", HttpStatus.UNAUTHORIZED);
            }
            
            // 解析token获取用户信息
            String userId = JwtUtils.getUserIdFromToken(token);
            String role = JwtUtils.getRoleFromToken(token);
            
            log.debug("用户认证成功: userId={}, role={}, path={}", userId, role, path);
            
            // 将用户信息添加到请求头中，传递给下游服务
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", role)
                    .build();
            
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
            
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return onError(exchange, "token验证失败", HttpStatus.UNAUTHORIZED);
        }
    }
    
    /**
     * 提取token
     * 
     * @param request HTTP请求
     * @return 提取的token，如果没有则为null
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
     * 
     * @param path 请求路径
     * @return 是否在白名单中
     */
    private boolean isWhitelisted(String path) {
        return whitelist.stream().anyMatch(path::startsWith);
    }
    
    /**
     * 处理认证错误
     * 
     * @param exchange 服务交换对象
     * @param message 错误消息
     * @param httpStatus HTTP状态码
     * @return Mono对象
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        CommonResult<String> result = CommonResult.error(httpStatus.value(), message);
        
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(result);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            log.error("响应序列化失败", e);
            return response.setComplete();
        }
    }
    
    @Override
    public int getOrder() {
        return -1; // 最高优先级
    }
}