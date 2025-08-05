package com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 注册响应
 */
@Schema(description = "注册响应")
public class RegisterResponse {
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "角色")
    private String role;
    
    @Schema(description = "消息")
    private String message;
    
    // Getters and setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}