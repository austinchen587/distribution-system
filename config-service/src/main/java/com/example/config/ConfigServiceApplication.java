package com.example.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.mybatis.spring.annotation.MapperScan;

/**
 * 系统配置服务启动类
 * 
 * @author System
 * @version 1.0
 * @since 2025-08-06
 */
@SpringBootApplication(scanBasePackages = {"com.example.config", "com.example.common"})
@EnableDiscoveryClient
@MapperScan("com.example.config.mapper")
public class ConfigServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ConfigServiceApplication.class, args);
    }
}
