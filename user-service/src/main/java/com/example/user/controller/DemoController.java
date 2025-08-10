package com.example.user.controller;

import com.example.common.dto.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Swagger API测试演示控制器
 * 展示各种API接口类型和Swagger功能
 */
@RestController
@RequestMapping("/api/demo")
@Tag(name = "Swagger演示", description = "展示Swagger各种功能的演示接口")
public class DemoController {

    /**
     * 获取演示用户列表
     */
    @GetMapping("/users")
    @Operation(summary = "获取演示用户列表", description = "返回模拟的用户数据，展示分页和筛选功能")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "400", description = "参数错误")
    })
    public CommonResult<Map<String, Object>> getDemoUsers(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(name = "page_size", defaultValue = "10") Integer pageSize,
            
            @Parameter(description = "状态筛选", example = "active")
            @RequestParam(required = false) String status,
            
            @Parameter(description = "角色筛选", example = "admin")
            @RequestParam(required = false) String role) {

        // 构造模拟数据
        List<Map<String, Object>> users = new ArrayList<>();
        
        for (int i = 1; i <= pageSize; i++) {
            Map<String, Object> user = new HashMap<>();
            user.put("id", (page - 1) * pageSize + i);
            user.put("username", "user_" + ((page - 1) * pageSize + i));
            user.put("email", "user" + ((page - 1) * pageSize + i) + "@example.com");
            user.put("role", role != null ? role : (i % 2 == 0 ? "admin" : "user"));
            user.put("status", status != null ? status : "active");
            user.put("createdAt", LocalDateTime.now().minusDays(i));
            users.add(user);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("users", users);
        response.put("total", 100); // 模拟总数
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("totalPages", (100 + pageSize - 1) / pageSize);
        
        return CommonResult.success(response);
    }

    /**
     * 获取单个演示用户
     */
    @GetMapping("/users/{id}")
    @Operation(summary = "获取用户详情", description = "根据ID获取单个用户的详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public CommonResult<Map<String, Object>> getDemoUser(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long id) {
        
        if (id <= 0) {
            return CommonResult.error(400, "无效的用户ID");
        }
        
        Map<String, Object> user = new HashMap<>();
        user.put("id", id);
        user.put("username", "demo_user_" + id);
        user.put("email", "demo" + id + "@example.com");
        user.put("phone", "138000000" + String.format("%02d", id % 100));
        user.put("role", id % 3 == 0 ? "admin" : (id % 2 == 0 ? "manager" : "user"));
        user.put("status", "active");
        user.put("createdAt", LocalDateTime.now().minusDays(id));
        user.put("lastLoginAt", LocalDateTime.now().minusHours(id));
        
        return CommonResult.success(user);
    }

    /**
     * 创建演示用户
     */
    @PostMapping("/users")
    @Operation(summary = "创建新用户", description = "创建一个新的用户账户")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "参数验证失败"),
        @ApiResponse(responseCode = "409", description = "用户名已存在")
    })
    public CommonResult<Map<String, Object>> createDemoUser(
            @Parameter(description = "用户创建请求", required = true)
            @Valid @RequestBody CreateUserRequest request) {
        
        // 模拟验证
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return CommonResult.error(400, "用户名不能为空");
        }
        
        if ("admin".equals(request.getUsername())) {
            return CommonResult.error(409, "用户名已存在");
        }
        
        // 模拟创建结果
        Map<String, Object> user = new HashMap<>();
        user.put("id", System.currentTimeMillis() % 10000);
        user.put("username", request.getUsername());
        user.put("email", request.getEmail());
        user.put("phone", request.getPhone());
        user.put("role", request.getRole());
        user.put("status", "active");
        user.put("createdAt", LocalDateTime.now());
        
        return CommonResult.success(user);
    }

    /**
     * 搜索演示用户
     */
    @GetMapping("/users/search")
    @Operation(summary = "搜索用户", description = "根据关键词搜索用户，支持模糊匹配")
    @ApiResponse(responseCode = "200", description = "搜索成功")
    public CommonResult<List<Map<String, Object>>> searchDemoUsers(
            @Parameter(description = "搜索关键词", example = "admin")
            @RequestParam(required = false) String keyword,
            
            @Parameter(description = "排序字段", example = "createdAt")
            @RequestParam(name = "sort_by", defaultValue = "createdAt") String sortBy,
            
            @Parameter(description = "排序方向", example = "DESC")
            @RequestParam(name = "sort_order", defaultValue = "DESC") String sortOrder) {
        
        List<Map<String, Object>> results = new ArrayList<>();
        
        // 模拟搜索结果
        for (int i = 1; i <= 5; i++) {
            Map<String, Object> user = new HashMap<>();
            user.put("id", i);
            user.put("username", keyword != null ? keyword + "_user_" + i : "search_result_" + i);
            user.put("email", "result" + i + "@example.com");
            user.put("role", i % 2 == 0 ? "admin" : "user");
            user.put("status", "active");
            user.put("score", 95 - i * 10); // 搜索匹配度
            results.add(user);
        }
        
        return CommonResult.success(results);
    }

    /**
     * 获取统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取统计信息", description = "获取用户相关的统计数据")
    @ApiResponse(responseCode = "200", description = "获取成功")
    public CommonResult<Map<String, Object>> getDemoStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", 1250);
        stats.put("activeUsers", 1100);
        stats.put("newUsersToday", 25);
        stats.put("onlineUsers", 89);
        
        // 角色分布
        Map<String, Integer> roleDistribution = new HashMap<>();
        roleDistribution.put("admin", 5);
        roleDistribution.put("manager", 45);
        roleDistribution.put("user", 1200);
        stats.put("roleDistribution", roleDistribution);
        
        // 最近7天新增用户
        List<Map<String, Object>> dailyStats = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            Map<String, Object> day = new HashMap<>();
            day.put("date", LocalDateTime.now().minusDays(i).toLocalDate());
            day.put("newUsers", 20 + (int) (Math.random() * 30));
            dailyStats.add(day);
        }
        stats.put("dailyNewUsers", dailyStats);
        
        return CommonResult.success(stats);
    }

    /**
     * 创建用户请求DTO
     */
    public static class CreateUserRequest {
        private String username;
        private String email;
        private String phone;
        private String role;
        
        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}