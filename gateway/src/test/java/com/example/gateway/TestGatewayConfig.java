package com.example.gateway;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

/**
 * 测试配置，用于网关测试
 */
@TestConfiguration
public class TestGatewayConfig {

    /**
     * 测试路由配置，使用本地服务进行测试
     */
    @Bean
    @Primary
    public RouteLocator testRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-test", r -> r.path("/api/auth/**")
                        .uri("http://localhost:8080"))
                .route("leads-test", r -> r.path("/api/leads/**")
                        .uri("http://localhost:8081"))
                .route("swagger-test", r -> r.path("/v3/api-docs")
                        .uri("http://localhost:8082"))
                .build();
    }
}