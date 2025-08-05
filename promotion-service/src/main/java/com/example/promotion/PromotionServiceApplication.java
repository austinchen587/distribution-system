package com.example.promotion;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 推广服务启动类
 * 
 * @author System
 * @version 1.0
 * @since 2025-08-05
 */
@SpringBootApplication(scanBasePackages = {"com.example.promotion", "com.example.common"})
@EnableDiscoveryClient
@MapperScan("com.example.promotion.mapper")
public class PromotionServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PromotionServiceApplication.class, args);
    }
}