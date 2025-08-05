package com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 登录响应
 */
@Schema(description = "登录响应")
public class LoginResponse {
    @Schema(description = "JWT令牌")
    private String token;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "角色")
    private String role;
    
    @Schema(description = "昵称")
    private String nickname;
    
    // Getters and setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}