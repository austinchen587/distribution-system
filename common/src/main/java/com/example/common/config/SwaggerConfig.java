package com.example.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 配置类
 * 配置API文档的基本信息和安全认证
 */
@Configuration
public class SwaggerConfig {
    
    /**
     * 配置OpenAPI基本信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .components(new Components()
                        .addSecuritySchemes("JWT", createJWTScheme()));
    }
    
    /**
     * API文档基本信息
     */
    private Info apiInfo() {
        return new Info()
                .title("分销系统 API 文档")
                .description("基于Spring Boot + Spring Cloud的多级分销系统API文档")
                .version("1.0.0")
                .contact(new Contact()
                        .name("开发团队")
                        .email("dev@example.com")
                        .url("https://www.example.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0.html"));
    }
    
    /**
     * 创建JWT认证方案
     */
    private SecurityScheme createJWTScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("在这里输入JWT Token，格式为：Bearer {token}");
    }
}