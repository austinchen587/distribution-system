package com.example.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.cloud.nacos.discovery.enabled=true",
    "spring.cloud.nacos.config.enabled=true",
    "spring.cloud.nacos.discovery.server-addr=localhost:8848",
    "spring.cloud.nacos.config.server-addr=localhost:8848"
})
class GatewayApplicationTests {

    @Test
    void contextLoads() {
        // 测试Spring上下文是否能正确加载，包含Nacos集成
    }

}