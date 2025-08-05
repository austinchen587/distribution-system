package com.example.level.dto.agent;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 代理基础信息DTO - 用于列表展示和基础信息
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
@Schema(description = "代理基础信息")
public class AgentDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 代理用户ID
     */
    @Schema(description = "代理用户ID", example = "1")
    @NotNull
    private Long userId;
    
    /**
     * 代理用户名
     */
    @Schema(description = "代理用户名", example = "agent001")
    @NotBlank
    private String username;
    
    /**
     * 代理真实姓名
     */
    @Schema(description = "代理真实姓名", example = "张三")
    private String realName;
    
    /**
     * 代理等级名称
     */
    @Schema(description = "代理等级名称", example = "SV1")
    private String agentLevelName;
    
    /**
     * 佣金比例
     */
    @Schema(description = "佣金比例", example = "0.15")
    private BigDecimal commissionRate;
    
    /**
     * 基础底薪
     */
    @Schema(description = "基础底薪", example = "5000.00")
    private BigDecimal baseSalary;
    
    /**
     * 代理状态
     */
    @Schema(description = "代理状态", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "SUSPENDED"})
    private String status;
    
    /**
     * 手机号码
     */
    @Schema(description = "手机号码", example = "13800138000")
    private String phone;
    
    /**
     * 邮箱地址
     */
    @Schema(description = "邮箱地址", example = "agent@example.com")
    private String email;
    
    /**
     * 注册时间
     */
    @Schema(description = "注册时间", example = "2025-01-01 10:00:00")
    private String createdAt;
    
    // Constructors
    public AgentDto() {}
    
    public AgentDto(Long userId, String username, String agentLevelName, String status) {
        this.userId = userId;
        this.username = username;
        this.agentLevelName = agentLevelName;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public String getAgentLevelName() {
        return agentLevelName;
    }
    
    public void setAgentLevelName(String agentLevelName) {
        this.agentLevelName = agentLevelName;
    }
    
    public BigDecimal getCommissionRate() {
        return commissionRate;
    }
    
    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }
    
    public BigDecimal getBaseSalary() {
        return baseSalary;
    }
    
    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "AgentDto{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", realName='" + realName + '\'' +
                ", agentLevelName='" + agentLevelName + '\'' +
                ", commissionRate=" + commissionRate +
                ", baseSalary=" + baseSalary +
                ", status='" + status + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}