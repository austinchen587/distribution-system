package com.example.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Swagger配置测试
 * 验证Swagger文档是否正确生成
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringJUnitConfig
public class SwaggerConfigTest {
    
    @LocalServerPort
    private int port;
    
    private final TestRestTemplate restTemplate = new TestRestTemplate();
    
    @Test
    public void testSwaggerUIAccess() {
        String url = "http://localhost:" + port + "/auth/swagger-ui.html";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        // Swagger UI可能返回200或301，两种都是正常的
        assertTrue(response.getStatusCode() == HttpStatus.OK || 
                   response.getStatusCode() == HttpStatus.MOVED_PERMANENTLY);
    }
    
    @Test
    public void testOpenAPIJsonAccess() {
        String url = "http://localhost:" + port + "/auth/v3/api-docs";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // 验证返回的是JSON格式的OpenAPI文档
        String body = response.getBody();
        assert body.contains("\"openapi\"");
        assert body.contains("\"info\"");
        assert body.contains("\"paths\"");
    }
}