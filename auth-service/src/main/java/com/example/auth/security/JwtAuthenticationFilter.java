package com.example.auth.security;

import com.example.common.utils.JwtUtils;
import com.example.common.utils.UserContextHolder;
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
 * Spring Security JWT 认证过滤器
 *
 * 将有效的 JWT 注入到 Spring Security 的 SecurityContext 中，
 * 以配合 .anyRequest().authenticated() 的访问控制策略。
 * 同时兼容现有的 UserContextHolder，以避免对现有业务代码的影响。
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        try {
            if (shouldSkip(uri, method)) {
                filterChain.doFilter(request, response);
                return;
            }

            String authHeader = request.getHeader(AUTHORIZATION_HEADER);
            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
                // 不注入认证，交由 Spring Security 的授权机制处理（可能返回 401/403）
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(BEARER_PREFIX.length());
            if (!JwtUtils.validateToken(token)) {
                // 非法或过期 token，不注入认证
                filterChain.doFilter(request, response);
                return;
            }

            // 解析 token，构造认证并注入上下文
            String userId = JwtUtils.getUserIdFromToken(token);
            String role = JwtUtils.getRoleFromToken(token);

            // 注入 Spring Security 上下文
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    Collections.singletonList(authority)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 兼容现有基于 ThreadLocal 的用户上下文
            UserContextHolder.setContext(new UserContextHolder.UserContext(userId, role));

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("JWT 过滤器处理异常: {}", e.getMessage(), e);
            filterChain.doFilter(request, response);
        } finally {
            // 清理自定义用户上下文，避免线程复用导致的脏数据
            UserContextHolder.clear();
        }
    }

    private boolean shouldSkip(String uri, String method) {
        // 仅跳过真正的公开端点与文档/健康检查
        boolean publicAuth = "/api/auth/login".equals(uri)
                || "/api/auth/register".equals(uri)
                || "/api/auth/send-code".equals(uri);
        return publicAuth
                || uri.startsWith("/swagger-ui/")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/actuator/health")
                || "/favicon.ico".equals(uri)
                || "OPTIONS".equalsIgnoreCase(method);
    }
}

