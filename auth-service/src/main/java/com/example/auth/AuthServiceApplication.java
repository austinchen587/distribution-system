package com.example.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;



/**
 * 认证服务启动类
 */
@MapperScan(basePackages = {"com.example.common.mapper", "com.example.auth.mapper"})
@EnableDiscoveryClient


@SpringBootApplication
@ComponentScan(basePackages = {"com.example.auth", "com.example.common"})
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}