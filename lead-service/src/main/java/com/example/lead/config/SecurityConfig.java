package com.example.lead.config;

import com.example.lead.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置（lead-service）
 * - 接入 JWT 过滤器
 * - 禁用 CSRF（JWT 无状态）
 * - 放行 Swagger 文档
 * - 其他路径需要认证
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                .antMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/actuator/health"
                ).permitAll()
                .antMatchers(
                    "/api/leads/check-duplicate",
                    "/api/leads/source-suggestions",
                    "/api/leads/detect-source",
                    "/api/leads/validate-source"
                ).hasAnyRole("SUPER_ADMIN","DIRECTOR","LEADER","SALES","AGENT")
                .antMatchers(
                    "/api/leads/create",
                    "/api/leads/*/status",
                    "/api/leads/*"
                ).hasAnyRole("SUPER_ADMIN","DIRECTOR","LEADER","SALES")
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

