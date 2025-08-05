package com.example.data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 代理实体类
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-04
 */
public class Agent {
    
    /**
     * 代理记录ID
     */
    private Long id;
    
    /**
     * 关联用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 手机号码
     */
    private String phone;
    
    /**
     * 电子邮箱
     */
    private String email;
    
    /**
     * 代理编码
     */
    @NotBlank(message = "代理编码不能为空")
    @Size(max = 32, message = "代理编码长度不能超过32字符")
    private String agentCode;
    
    /**
     * 代理等级
     */
    @NotBlank(message = "代理等级不能为空")
    private String level;
    
    /**
     * 代理状态
     */
    @NotBlank(message = "代理状态不能为空")
    private String status;
    
    /**
     * 绩效评分
     */
    @DecimalMin(value = "0", message = "绩效评分不能为负数")
    @DecimalMax(value = "100", message = "绩效评分不能超过100")
    private BigDecimal performanceScore;
    
    /**
     * 管理客户数量
     */
    @Min(value = 0, message = "管理客户数量不能为负数")
    private Integer managedCustomerCount;
    
    /**
     * 总成交笔数
     */
    @Min(value = 0, message = "总成交笔数不能为负数")
    private Integer totalDeals;
    
    /**
     * 总佣金收入
     */
    @DecimalMin(value = "0", message = "总佣金收入不能为负数")
    private BigDecimal totalCommission;
    
    /**
     * 当月佣金收入
     */
    @DecimalMin(value = "0", message = "当月佣金收入不能为负数")
    private BigDecimal currentMonthCommission;
    
    /**
     * 记录创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdAt;
    
    /**
     * 记录更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updatedAt;
    
    // Constructors
    public Agent() {}
    
    public Agent(Long userId, String agentCode, String level, String status) {
        this.userId = userId;
        this.agentCode = agentCode;
        this.level = level;
        this.status = status;
        this.performanceScore = BigDecimal.ZERO;
        this.managedCustomerCount = 0;
        this.totalDeals = 0;
        this.totalCommission = BigDecimal.ZERO;
        this.currentMonthCommission = BigDecimal.ZERO;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public String getAgentCode() {
        return agentCode;
    }
    
    public void setAgentCode(String agentCode) {
        this.agentCode = agentCode;
    }
    
    public String getLevel() {
        return level;
    }
    
    public void setLevel(String level) {
        this.level = level;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public BigDecimal getPerformanceScore() {
        return performanceScore;
    }
    
    public void setPerformanceScore(BigDecimal performanceScore) {
        this.performanceScore = performanceScore;
    }
    
    public Integer getManagedCustomerCount() {
        return managedCustomerCount;
    }
    
    public void setManagedCustomerCount(Integer managedCustomerCount) {
        this.managedCustomerCount = managedCustomerCount;
    }
    
    public Integer getTotalDeals() {
        return totalDeals;
    }
    
    public void setTotalDeals(Integer totalDeals) {
        this.totalDeals = totalDeals;
    }
    
    public BigDecimal getTotalCommission() {
        return totalCommission;
    }
    
    public void setTotalCommission(BigDecimal totalCommission) {
        this.totalCommission = totalCommission;
    }
    
    public BigDecimal getCurrentMonthCommission() {
        return currentMonthCommission;
    }
    
    public void setCurrentMonthCommission(BigDecimal currentMonthCommission) {
        this.currentMonthCommission = currentMonthCommission;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agent agent = (Agent) o;
        return Objects.equals(id, agent.id) &&
               Objects.equals(userId, agent.userId) &&
               Objects.equals(agentCode, agent.agentCode);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, userId, agentCode);
    }
    
    @Override
    public String toString() {
        return "Agent{" +
                "id=" + id +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", agentCode='" + agentCode + '\'' +
                ", level='" + level + '\'' +
                ", status='" + status + '\'' +
                ", performanceScore=" + performanceScore +
                ", managedCustomerCount=" + managedCustomerCount +
                ", totalDeals=" + totalDeals +
                ", totalCommission=" + totalCommission +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}