package com.example.gateway;

import com.example.gateway.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {GatewayApplication.class, TestConfig.class})
@ActiveProfiles("test")
class GatewayApplicationTests {

    @Test
    void contextLoads() {
        // 测试Spring上下文是否能正确加载
    }

}