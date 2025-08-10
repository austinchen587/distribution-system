package com.example.user.controller;

import com.example.common.dto.CommonResult;
import com.example.common.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证测试控制器
 * 用于演示JWT认证流程，便于Swagger API测试
 * 
 * @author User Service Team
 * @version 1.0
 * @since 2025-08-08
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证测试", description = "用于演示JWT认证流程的测试接口")
public class AuthTestController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthTestController.class);
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 登录请求DTO
     */
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        
        @NotBlank(message = "密码不能为空")
        private String password;
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    /**
     * 登录响应DTO
     */
    public static class LoginResponse {
        private String token;
        private String tokenType;
        private Long expiresIn;
        private String userId;
        private String username;
        private String role;
        
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        
        public String getTokenType() { return tokenType; }
        public void setTokenType(String tokenType) { this.tokenType = tokenType; }
        
        public Long getExpiresIn() { return expiresIn; }
        public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
    
    @Operation(
        summary = "测试登录", 
        description = "测试登录接口，使用固定的测试账号生成JWT token。测试账号：admin/123456"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "登录成功",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "用户名或密码错误"),
        @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    @PostMapping("/login")
    public CommonResult<LoginResponse> login(
            @Parameter(description = "登录请求参数", required = true)
            @Valid @RequestBody LoginRequest request) {
        
        logger.info("测试登录请求，用户名: {}", request.getUsername());
        
        // 简单的测试账号验证
        if (!"admin".equals(request.getUsername()) || !"123456".equals(request.getPassword())) {
            logger.warn("登录失败，用户名或密码错误: {}", request.getUsername());
            return CommonResult.error(401, "用户名或密码错误");
        }
        
        try {
            // 生成JWT token - 使用JwtUtils的实际方法
            String token = JwtUtils.generateToken("1", "SUPER_ADMIN");
            
            // 构造响应
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setTokenType("Bearer");
            response.setExpiresIn(86400L); // 秒
            response.setUserId("1");
            response.setUsername(request.getUsername());
            response.setRole("SUPER_ADMIN");
            
            logger.info("测试登录成功，用户: {}, token已生成", request.getUsername());
            return CommonResult.success(response);
            
        } catch (Exception e) {
            logger.error("生成JWT token失败", e);
            return CommonResult.error(500, "登录失败，请重试");
        }
    }
    
    @Operation(
        summary = "获取当前用户信息", 
        description = "根据JWT token获取当前登录用户信息，需要Bearer认证"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未登录或token已过期")
    })
    @GetMapping("/current")
    @SecurityRequirement(name = "bearerAuth")
    public CommonResult<Map<String, Object>> getCurrentUser(
            @Parameter(description = "Authorization header, 格式: Bearer {token}")
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return CommonResult.error(401, "缺少Authorization header或格式错误");
        }
        
        String token = authHeader.substring(7);
        
        try {
            // 验证并解析token
            if (!JwtUtils.validateToken(token)) {
                return CommonResult.error(401, "Token已过期或无效");
            }
            
            String userId = JwtUtils.getUserIdFromToken(token);
            String role = JwtUtils.getRoleFromToken(token);
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", userId);
            userInfo.put("username", "admin"); // 测试用户
            userInfo.put("role", role);
            userInfo.put("tokenValid", true);
            
            logger.info("获取用户信息成功: admin");
            return CommonResult.success(userInfo);
            
        } catch (Exception e) {
            logger.error("解析token失败", e);
            return CommonResult.error(401, "Token解析失败");
        }
    }
    
    @Operation(
        summary = "Token验证测试", 
        description = "测试JWT token的有效性"
    )
    @GetMapping("/verify")
    @SecurityRequirement(name = "bearerAuth")
    public CommonResult<Map<String, Object>> verifyToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return CommonResult.error(401, "缺少Authorization header");
        }
        
        String token = authHeader.substring(7);
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean isValid = JwtUtils.validateToken(token);
            result.put("valid", isValid);
            
            if (isValid) {
                String userId = JwtUtils.getUserIdFromToken(token);
                String role = JwtUtils.getRoleFromToken(token);
                
                Map<String, Object> claims = new HashMap<>();
                claims.put("userId", userId);
                claims.put("role", role);
                
                result.put("claims", claims);
                result.put("userId", userId);
                result.put("role", role);
            }
            
            return CommonResult.success(result);
            
        } catch (Exception e) {
            logger.error("Token验证失败", e);
            result.put("valid", false);
            result.put("error", e.getMessage());
            return CommonResult.success(result);
        }
    }
}