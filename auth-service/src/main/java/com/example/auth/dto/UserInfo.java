package com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户信息
 */
@Schema(description = "用户信息")
public class UserInfo {
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "角色")
    private String role;
    
    @Schema(description = "昵称")
    private String nickname;
    
    @Schema(description = "状态")
    private String status;
    
    // Getters and setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}