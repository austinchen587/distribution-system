package com.example.reward;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.mybatis.spring.annotation.MapperScan;

/**
 * 奖励结算服务启动类
 * 
 * @author System
 * @version 1.0
 * @since 2025-08-06
 */
@SpringBootApplication(scanBasePackages = {"com.example.reward", "com.example.common"})
@EnableDiscoveryClient
@MapperScan("com.example.reward.mapper")
public class RewardServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(RewardServiceApplication.class, args);
    }
}