package com.example.auth.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 更新用户请求DTO
 * 所有字段都是可选的，支持部分更新
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
@Schema(description = "更新用户请求")
public class UpdateUserRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户名（可选更新）
     */
    @Schema(description = "用户名", example = "zhangsan")
    @Size(min = 3, max = 64, message = "用户名长度必须在3-64个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$", message = "用户名只能包含字母、数字、下划线和中文")
    private String username;
    
    /**
     * 用户邮箱（可选更新）
     */
    @Schema(description = "用户邮箱", example = "zhangsan@example.com")
    @Size(max = 255, message = "邮箱长度不能超过255个字符")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 手机号码（可选更新）
     */
    @Schema(description = "手机号码", example = "13800138000")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;
    
    /**
     * 用户角色（可选更新）
     */
    @Schema(description = "用户角色", example = "agent", allowableValues = {"super_admin", "director", "leader", "sales", "agent"})
    @Pattern(regexp = "^(super_admin|director|leader|sales|agent)$", message = "用户角色必须是: super_admin, director, leader, sales, agent")
    private String role;
    
    /**
     * 用户状态（可选更新）
     */
    @Schema(description = "用户状态", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "SUSPENDED"})
    @Pattern(regexp = "^(ACTIVE|INACTIVE|SUSPENDED)$", message = "用户状态必须是: ACTIVE, INACTIVE, SUSPENDED")
    private String status;
    
    /**
     * 真实姓名（可选更新）
     */
    @Schema(description = "真实姓名", example = "张三")
    @Size(max = 100, message = "真实姓名长度不能超过100个字符")
    private String realName;
    
    /**
     * 微信ID（可选更新）
     */
    @Schema(description = "微信ID", example = "wechat_id_123")
    @Size(max = 64, message = "微信ID长度不能超过64个字符")
    private String wechatId;
    
    // Constructors
    public UpdateUserRequest() {}
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public String getWechatId() {
        return wechatId;
    }
    
    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }
    
    /**
     * 检查是否有任何字段需要更新
     * 
     * @return 是否有字段需要更新
     */
    public boolean hasAnyFieldToUpdate() {
        return username != null || email != null || phone != null || 
               role != null || status != null || realName != null || wechatId != null;
    }
    
    @Override
    public String toString() {
        return "UpdateUserRequest{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                ", realName='" + realName + '\'' +
                ", wechatId='" + wechatId + '\'' +
                '}';
    }
}