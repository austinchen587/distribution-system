package com.example.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * 数据访问层测试应用程序
 * 
 * @author Data Access Test Generator
 * @version 1.0
 * @since 2025-08-03
 */
@SpringBootApplication
public class DataAccessTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataAccessTestApplication.class, args);
    }

    /**
     * 测试配置类
     */
    @TestConfiguration
    @Profile("test")
    public static class TestConfig {
        
        /**
         * 测试环境专用配置
         */
        @Bean
        @Primary
        public String testEnvironment() {
            return "test";
        }
    }
}