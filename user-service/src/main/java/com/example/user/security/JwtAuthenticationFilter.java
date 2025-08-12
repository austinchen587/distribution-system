package com.example.user.security;

import com.example.common.utils.JwtUtils;
import com.example.common.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * JWT 认证过滤器（user-service）
 *
 * 优先从 Authorization: Bearer <token> 解析并注入认证；
 * 如无 Authorization，则兼容从网关透传的 X-User-Id/X-User-Role 注入认证。
 *
 * 不直接写入响应，由 Spring Security 统一处理未认证/无权限场景。
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = HttpHeaders.AUTHORIZATION;
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_ROLE = "X-User-Role";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 跳过白名单端点
            if (shouldSkip(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 已存在认证则直接放行
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            // 1) 尝试从 Authorization: Bearer <token> 注入
            String authHeader = request.getHeader(AUTHORIZATION_HEADER);
            if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
                String token = authHeader.substring(BEARER_PREFIX.length());
                tryInjectAuthenticationFromToken(token);
            }

            // 2) 如无 Authorization，则尝试从网关透传头注入
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String userId = request.getHeader(HEADER_USER_ID);
                String role = request.getHeader(HEADER_USER_ROLE);
                tryInjectAuthenticationFromHeaders(userId, role);
            }

            filterChain.doFilter(request, response);
        } finally {
            // 清理 ThreadLocal，避免线程复用造成脏数据
            UserContextHolder.clear();
        }
    }

    private boolean shouldSkip(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) return true;
        return uri.startsWith("/swagger-ui/")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/api/health/")
                || uri.startsWith("/api/auth/")
                || uri.startsWith("/actuator/health")
                || "/favicon.ico".equals(uri);
    }

    private void tryInjectAuthenticationFromToken(String token) {
        try {
            if (!JwtUtils.validateToken(token)) {
                return;
            }
            String userId = JwtUtils.getUserIdFromToken(token);
            String role = JwtUtils.getRoleFromToken(token);
            if (userId == null || role == null) {
                return;
            }
            injectAuthentication(userId, role);
        } catch (Exception e) {
            log.warn("JWT 解析失败: {}", e.getMessage());
        }
    }

    private void tryInjectAuthenticationFromHeaders(String userId, String role) {
        if (userId == null || role == null) return;
        try {
            injectAuthentication(userId, role);
        } catch (Exception e) {
            log.warn("Header 用户上下文注入失败: {}", e.getMessage());
        }
    }

    private void injectAuthentication(String userId, String role) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                Collections.singletonList(authority)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 同步 ThreadLocal 用户上下文，供业务层数据权限使用
        UserContextHolder.setContext(new UserContextHolder.UserContext(userId, role));
    }
}

