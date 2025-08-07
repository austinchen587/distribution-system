package com.example.invitation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.mybatis.spring.annotation.MapperScan;

/**
 * 邀请系统服务启动类
 * 
 * @author System
 * @version 1.0
 * @since 2025-08-06
 */
@SpringBootApplication(scanBasePackages = {"com.example.invitation", "com.example.common"})
@EnableDiscoveryClient
@MapperScan("com.example.invitation.mapper")
public class InvitationServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(InvitationServiceApplication.class, args);
    }
}