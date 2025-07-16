package com.example.gateway.config;

import com.example.common.utils.JwtUtils;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.mockito.Mockito;

@TestConfiguration
public class TestConfig {
    
    @Bean
    @Primary
    public JwtUtils jwtUtils() {
        return Mockito.mock(JwtUtils.class);
    }
}