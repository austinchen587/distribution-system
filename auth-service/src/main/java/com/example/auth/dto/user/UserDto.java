package com.example.auth.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户基础信息DTO - 用于API响应
 * 轻量级设计，优化网络传输性能
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
@Schema(description = "用户基础信息")
public class UserDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1")
    @NotNull
    private Long id;
    
    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "张三")
    @NotBlank
    private String username;
    
    /**
     * 用户角色
     */
    @Schema(description = "用户角色", example = "agent", allowableValues = {"super_admin", "director", "leader", "sales", "agent"})
    private String role;
    
    /**
     * 用户状态
     */
    @Schema(description = "用户状态", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "SUSPENDED"})
    private String status;
    
    /**
     * 用户邮箱
     */
    @Schema(description = "用户邮箱", example = "zhangsan@example.com")
    private String email;
    
    /**
     * 手机号码
     */
    @Schema(description = "手机号码", example = "13800138000")
    private String phone;
    
    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名", example = "张三")
    private String realName;
    
    /**
     * 是否活跃
     */
    @Schema(description = "是否活跃", example = "true")
    private Boolean isActive;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2025-08-04 10:30:00")
    private String createdAt;
    
    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2025-08-04 15:20:00")
    private String updatedAt;
    
    // Constructors
    public UserDto() {}
    
    public UserDto(Long id, String username, String role, String status) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
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
    
    public String getRealName() {
        return realName;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}