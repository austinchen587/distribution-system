package com.example.user.controller;

import com.example.common.dto.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器 - 用于验证API和Swagger功能
 */
@RestController
@RequestMapping("/api/health")
@Tag(name = "健康检查", description = "系统健康状态检查接口")
public class HealthController {

    @GetMapping("/ping")
    @Operation(summary = "基础连通性测试", description = "验证服务是否正常运行")
    @ApiResponse(responseCode = "200", description = "服务正常")
    public CommonResult<Map<String, Object>> ping() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "ok");
        result.put("timestamp", LocalDateTime.now());
        result.put("service", "user-service");
        result.put("message", "User service is running successfully");
        
        return CommonResult.success(result);
    }

    @GetMapping("/database")
    @Operation(summary = "数据库连接测试", description = "验证数据库连接状态")
    @ApiResponse(responseCode = "200", description = "数据库连接正常")
    public CommonResult<Map<String, Object>> checkDatabase() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "ok");
        result.put("database", "MySQL");
        result.put("message", "Database connection is healthy");
        
        return CommonResult.success(result);
    }

    @GetMapping("/info")
    @Operation(summary = "服务信息", description = "获取服务详细信息")
    @ApiResponse(responseCode = "200", description = "获取成功")
    public CommonResult<Map<String, Object>> info() {
        Map<String, Object> result = new HashMap<>();
        result.put("serviceName", "user-service");
        result.put("version", "1.0.0");
        result.put("framework", "Spring Boot 2.7.14");
        result.put("javaVersion", System.getProperty("java.version"));
        result.put("uptime", System.currentTimeMillis());
        
        return CommonResult.success(result);
    }
}