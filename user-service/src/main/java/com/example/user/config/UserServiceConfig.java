package com.example.user.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

/**
 * 用户服务配置类
 * 
 * <p>配置用户服务的相关Bean和设置，包括密码编码器、Swagger文档等。
 * 该配置类确保用户服务的各项功能正常运行。
 * 
 * <p>主要配置：
 * <ul>
 *   <li>密码编码器：BCrypt加密算法</li>
 *   <li>Swagger文档：API文档配置</li>
 *   <li>安全配置：JWT认证设置</li>
 * </ul>
 * 
 * @author User Service Team
 * @version 1.0.0
 * @since 2025-08-07
 */
@Configuration
public class UserServiceConfig {
    
    /**
     * 密码编码器Bean
     * 
     * <p>使用BCrypt算法对用户密码进行加密，确保密码安全存储。
     * BCrypt是一种自适应哈希函数，具有良好的安全性和性能。
     * 
     * @return BCrypt密码编码器实例
     */
    // 注意：密码编码器已在 SecurityConfig 中定义，避免重复Bean导致冲突

    /**
     * OpenAPI文档配置
     * 
     * <p>配置Swagger/OpenAPI文档，提供完整的API文档和测试界面。
     * 包含服务信息、认证配置、服务器地址等。
     * 
     * @return OpenAPI配置实例
     */
    @Bean
    public OpenAPI userServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("用户管理服务API")
                .description("分销系统用户管理相关接口文档")
                .version("1.0.0"))
            .servers(List.of(
                new Server().url("http://localhost:8086").description("本地开发环境"),
                new Server().url("https://api-staging.yourdomain.com").description("测试环境"),
                new Server().url("https://api.yourdomain.com").description("生产环境")
            ))
            .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
            .components(new Components()
                .addSecuritySchemes("BearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT认证，格式：Bearer <token>")));
    }
}
