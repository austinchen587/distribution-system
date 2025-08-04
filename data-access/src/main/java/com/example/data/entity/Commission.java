package com.example.data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 佣金记录实体类
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-03
 */
public class Commission {
    
    /**
     * 佣金记录ID
     */
    private Long id;
    
    /**
     * 佣金受益人ID
     */
    @NotNull(message = "佣金受益人ID不能为空")
    private Long userId;
    
    /**
     * 关联的成交记录ID
     */
    @NotNull(message = "成交记录ID不能为空")
    private Long dealId;
    
    /**
     * 佣金类型
     */
    @NotNull(message = "佣金类型不能为空")
    private CommissionLevel commissionLevel;
    
    /**
     * 计算时应用的佣金率
     */
    @NotNull(message = "佣金率不能为空")
    @DecimalMin(value = "0.00", message = "佣金率不能为负数")
    @DecimalMax(value = "100.00", message = "佣金率不能超过100%")
    private BigDecimal commissionRate;
    
    /**
     * 计算出的佣金金额
     */
    @NotNull(message = "佣金金额不能为空")
    @DecimalMin(value = "0.00", message = "佣金金额不能为负数")
    private BigDecimal commissionAmount;
    
    /**
     * 佣金结算月份 (格式: YYYY-MM)
     */
    @NotBlank(message = "结算月份不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "结算月份格式必须为YYYY-MM")
    private String settlementMonth;
    
    /**
     * 结算状态
     */
    @NotNull(message = "结算状态不能为空")
    private SettlementStatus status = SettlementStatus.PENDING;
    
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
    
    // 佣金类型枚举
    public enum CommissionLevel {
        DIRECT("direct", "直接佣金"),
        INDIRECT("indirect", "间接佣金");
        
        private final String code;
        private final String description;
        
        CommissionLevel(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        public static CommissionLevel fromCode(String code) {
            for (CommissionLevel level : values()) {
                if (level.code.equals(code)) {
                    return level;
                }
            }
            throw new IllegalArgumentException("Unknown commission level code: " + code);
        }
    }
    
    // 结算状态枚举
    public enum SettlementStatus {
        PENDING("pending", "待结算"),
        PAID("paid", "已支付"),
        CANCELLED("cancelled", "已取消");
        
        private final String code;
        private final String description;
        
        SettlementStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        public static SettlementStatus fromCode(String code) {
            for (SettlementStatus status : values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown settlement status code: " + code);
        }
    }
    
    // Constructors
    public Commission() {}
    
    public Commission(Long userId, Long dealId, CommissionLevel commissionLevel, 
                     BigDecimal commissionRate, BigDecimal commissionAmount, String settlementMonth) {
        this.userId = userId;
        this.dealId = dealId;
        this.commissionLevel = commissionLevel;
        this.commissionRate = commissionRate;
        this.commissionAmount = commissionAmount;
        this.settlementMonth = settlementMonth;
        this.status = SettlementStatus.PENDING;
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
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getDealId() {
        return dealId;
    }
    
    public void setDealId(Long dealId) {
        this.dealId = dealId;
    }
    
    public CommissionLevel getCommissionLevel() {
        return commissionLevel;
    }
    
    public void setCommissionLevel(CommissionLevel commissionLevel) {
        this.commissionLevel = commissionLevel;
    }
    
    public BigDecimal getCommissionRate() {
        return commissionRate;
    }
    
    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }
    
    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }
    
    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }
    
    public String getSettlementMonth() {
        return settlementMonth;
    }
    
    public void setSettlementMonth(String settlementMonth) {
        this.settlementMonth = settlementMonth;
    }
    
    public SettlementStatus getStatus() {
        return status;
    }
    
    public void setStatus(SettlementStatus status) {
        this.status = status;
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
     * 检查是否为直接佣金
     * 
     * @return 是否为直接佣金
     */
    public boolean isDirect() {
        return CommissionLevel.DIRECT.equals(this.commissionLevel);
    }
    
    /**
     * 检查是否为间接佣金
     * 
     * @return 是否为间接佣金
     */
    public boolean isIndirect() {
        return CommissionLevel.INDIRECT.equals(this.commissionLevel);
    }
    
    /**
     * 检查是否待结算
     * 
     * @return 是否待结算
     */
    public boolean isPending() {
        return SettlementStatus.PENDING.equals(this.status);
    }
    
    /**
     * 检查是否已支付
     * 
     * @return 是否已支付
     */
    public boolean isPaid() {
        return SettlementStatus.PAID.equals(this.status);
    }
    
    /**
     * 检查是否已取消
     * 
     * @return 是否已取消
     */
    public boolean isCancelled() {
        return SettlementStatus.CANCELLED.equals(this.status);
    }
    
    /**
     * 标记为已支付
     */
    public void markAsPaid() {
        this.status = SettlementStatus.PAID;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 取消佣金
     */
    public void cancel() {
        this.status = SettlementStatus.CANCELLED;
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
        Commission that = (Commission) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(userId, that.userId) &&
               Objects.equals(dealId, that.dealId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, userId, dealId);
    }
    
    @Override
    public String toString() {
        return "Commission{" +
                "id=" + id +
                ", userId=" + userId +
                ", dealId=" + dealId +
                ", commissionLevel=" + commissionLevel +
                ", commissionRate=" + commissionRate +
                ", commissionAmount=" + commissionAmount +
                ", settlementMonth='" + settlementMonth + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}