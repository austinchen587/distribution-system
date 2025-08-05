package com.example.data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 奖励实体类
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-04
 */
public class Reward {
    
    /**
     * 奖励记录ID
     */
    private Long id;
    
    /**
     * 代理用户ID
     */
    @NotNull(message = "代理用户ID不能为空")
    private Long agentId;
    
    /**
     * 代理用户名
     */
    private String agentUsername;
    
    /**
     * 代理真实姓名
     */
    private String agentRealName;
    
    /**
     * 奖励类型
     */
    @NotBlank(message = "奖励类型不能为空")
    private String type;
    
    /**
     * 奖励金额
     */
    @NotNull(message = "奖励金额不能为空")
    @DecimalMin(value = "0", message = "奖励金额不能为负数")
    private BigDecimal amount;
    
    /**
     * 奖励状态
     */
    @NotBlank(message = "奖励状态不能为空")
    private String status;
    
    /**
     * 奖励来源类型
     */
    private String source;
    
    /**
     * 奖励来源ID
     */
    private Long sourceId;
    
    /**
     * 奖励描述
     */
    private String description;
    
    /**
     * 计算规则
     */
    private String calculationRule;
    
    /**
     * 结算批次ID
     */
    private Long settlementId;
    
    /**
     * 发放时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String paidAt;
    
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
    public Reward() {}
    
    public Reward(Long agentId, String type, BigDecimal amount, String status) {
        this.agentId = agentId;
        this.type = type;
        this.amount = amount;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getAgentId() {
        return agentId;
    }
    
    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }
    
    public String getAgentUsername() {
        return agentUsername;
    }
    
    public void setAgentUsername(String agentUsername) {
        this.agentUsername = agentUsername;
    }
    
    public String getAgentRealName() {
        return agentRealName;
    }
    
    public void setAgentRealName(String agentRealName) {
        this.agentRealName = agentRealName;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public Long getSourceId() {
        return sourceId;
    }
    
    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCalculationRule() {
        return calculationRule;
    }
    
    public void setCalculationRule(String calculationRule) {
        this.calculationRule = calculationRule;
    }
    
    public Long getSettlementId() {
        return settlementId;
    }
    
    public void setSettlementId(Long settlementId) {
        this.settlementId = settlementId;
    }
    
    public String getPaidAt() {
        return paidAt;
    }
    
    public void setPaidAt(String paidAt) {
        this.paidAt = paidAt;
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
        Reward reward = (Reward) o;
        return Objects.equals(id, reward.id) &&
               Objects.equals(agentId, reward.agentId) &&
               Objects.equals(type, reward.type);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, agentId, type);
    }
    
    @Override
    public String toString() {
        return "Reward{" +
                "id=" + id +
                ", agentId=" + agentId +
                ", agentUsername='" + agentUsername + '\'' +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", source='" + source + '\'' +
                ", description='" + description + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}