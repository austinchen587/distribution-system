package com.example.lead.security;

import com.example.common.utils.UserContextHolder;
import com.example.common.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * JWT 认证过滤器（lead-service）
 * - 从 Authorization: Bearer <token> 解析 userId/role
 * - 注入 Spring Security 与线程级用户上下文
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private LeadJwtService leadJwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            if (shouldSkip(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 若已有认证，直接放行
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            String authHeader = request.getHeader(AUTHORIZATION_HEADER);
            if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
                String token = authHeader.substring(BEARER_PREFIX.length());
                tryInjectAuthenticationFromToken(token);
            }

            // 兼容网关透传（如有）
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String userId = request.getHeader("X-User-Id");
                String role = request.getHeader("X-User-Role");
                tryInjectAuthenticationFromHeaders(userId, role);
            }

            filterChain.doFilter(request, response);
        } finally {
            // 清理 ThreadLocal，避免复用线程造成脏数据
            UserContextHolder.clear();
        }
    }

    private boolean shouldSkip(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) return true;
        return uri.startsWith("/swagger-ui/")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/actuator/health")
                || uri.equals("/favicon.ico");
    }

    private void tryInjectAuthenticationFromToken(String token) {
        try {
            String userId;
            String role;
            if (leadJwtService != null && leadJwtService.validate(token)) {
                userId = leadJwtService.getUserId(token);
                role = leadJwtService.getRole(token);
            } else if (JwtUtils.validateToken(token)) {
                userId = JwtUtils.getUserIdFromToken(token);
                role = JwtUtils.getRoleFromToken(token);
            } else {
                return;
            }
            if (userId == null || role == null) return;
            injectAuthentication(userId, role);
        } catch (Exception e) {
            log.warn("JWT 解析失败: {}", e.getMessage());
        }
    }

    private void tryInjectAuthenticationFromHeaders(String userId, String role) {
        if (!StringUtils.hasText(userId) || !StringUtils.hasText(role)) return;
        try {
            injectAuthentication(userId, role);
        } catch (Exception e) {
            log.warn("Header 用户上下文注入失败: {}", e.getMessage());
        }
    }

    private void injectAuthentication(String userId, String role) {
        String normalizedRole = role == null ? "" : role.trim().toUpperCase().replace('-', '_');
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + normalizedRole);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                Collections.singletonList(authority)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 保持 ThreadLocal 中的 role 为原始code（如 super_admin），兼容业务 fromCode 解析
        UserContextHolder.setContext(new UserContextHolder.UserContext(userId, role));
    }
}

