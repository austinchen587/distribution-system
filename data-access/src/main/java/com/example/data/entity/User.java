package com.example.data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 用户实体类 - 统一管理所有系统角色
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-03
 */
public class User {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 用户登录名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 64, message = "用户名长度必须在3-64字符之间")
    private String username;
    
    /**
     * 电子邮箱，用于登录和接收通知
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 128, message = "邮箱长度不能超过128字符")
    private String email;
    
    /**
     * 手机号码，用于登录和接收通知
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 哈希加密后的登录密码
     */
    @JsonIgnore
    @NotBlank(message = "密码不能为空")
    private String password;
    
    /**
     * 关联的角色标识符，决定用户权限
     */
    @NotBlank(message = "角色不能为空")
    @Size(max = 50, message = "角色标识长度不能超过50字符")
    private String role;
    
    /**
     * 用户账户状态 - 临时使用String类型避免MyBatis枚举转换问题
     */
    @NotNull(message = "用户状态不能为空")
    private String status = "active";
    
    /**
     * 个人专属佣金比例，优先级高于等级佣金
     */
    @DecimalMin(value = "0.0000", message = "佣金比例不能为负数")
    @DecimalMax(value = "1.0000", message = "佣金比例不能超过100%")
    private BigDecimal commissionRate;
    
    /**
     * 上级用户ID (通常是邀请人)
     */
    private Long parentId;
    
    /**
     * 最后一次成功登录的时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginAt;
    
    /**
     * 记录创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    /**
     * 记录最后更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 用户状态枚举
    public enum UserStatus {
        ACTIVE("active", "正常"),
        INACTIVE("inactive", "未激活"),
        BANNED("banned", "已封禁"),
        PENDING("pending", "待审核");
        
        private final String code;
        private final String description;
        
        UserStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        public static UserStatus fromCode(String code) {
            for (UserStatus status : values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown status code: " + code);
        }
    }
    
    // Constructors
    public User() {}
    
    public User(String username, String email, String phone, String password, String role) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.status = "active";
        this.commissionRate = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
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
    
    public BigDecimal getCommissionRate() {
        return commissionRate;
    }
    
    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }
    
    public Long getParentId() {
        return parentId;
    }
    
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    
    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }
    
    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Business Methods
    
    /**
     * 检查用户是否活跃
     */
    public boolean isActive() {
        return "active".equals(this.status);
    }
    
    /**
     * 检查用户是否被封禁
     */
    public boolean isBanned() {
        return "banned".equals(this.status);
    }
    
    /**
     * 更新最后登录时间
     */
    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 设置更新时间为当前时间
     */
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
               Objects.equals(username, user.username) &&
               Objects.equals(email, user.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", status=" + status +
                ", commissionRate=" + commissionRate +
                ", parentId=" + parentId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}