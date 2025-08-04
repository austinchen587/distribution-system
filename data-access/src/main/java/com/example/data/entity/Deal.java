package com.example.data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 成交记录实体类
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-03
 */
public class Deal {
    
    /**
     * 成交记录ID
     */
    private Long id;
    
    /**
     * 关联的客户资源ID
     */
    @NotNull(message = "客户资源ID不能为空")
    private Long customerLeadId;
    
    /**
     * 关联的商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    /**
     * 完成交易的销售员ID
     */
    @NotNull(message = "销售员ID不能为空")
    private Long salesId;
    
    /**
     * 业绩归属的销售员ID
     */
    private Long salesOwnerId;
    
    /**
     * 成交金额
     */
    @NotNull(message = "成交金额不能为空")
    @DecimalMin(value = "0.00", message = "成交金额不能为负数")
    private BigDecimal dealAmount;
    
    /**
     * 交易状态
     */
    @NotNull(message = "交易状态不能为空")
    private DealStatus status = DealStatus.COMPLETED;
    
    /**
     * 业务上的成交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dealAt;
    
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
    
    // 交易状态枚举
    public enum DealStatus {
        PENDING("pending", "待处理"),
        COMPLETED("completed", "已完成"),
        REFUNDED("refunded", "已退款");
        
        private final String code;
        private final String description;
        
        DealStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        public static DealStatus fromCode(String code) {
            for (DealStatus status : values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown deal status code: " + code);
        }
    }
    
    // Constructors
    public Deal() {}
    
    public Deal(Long customerLeadId, Long productId, Long salesId, BigDecimal dealAmount) {
        this.customerLeadId = customerLeadId;
        this.productId = productId;
        this.salesId = salesId;
        this.dealAmount = dealAmount;
        this.status = DealStatus.COMPLETED;
        this.dealAt = LocalDateTime.now();
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
    
    public Long getCustomerLeadId() {
        return customerLeadId;
    }
    
    public void setCustomerLeadId(Long customerLeadId) {
        this.customerLeadId = customerLeadId;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public Long getSalesId() {
        return salesId;
    }
    
    public void setSalesId(Long salesId) {
        this.salesId = salesId;
    }
    
    public Long getSalesOwnerId() {
        return salesOwnerId;
    }
    
    public void setSalesOwnerId(Long salesOwnerId) {
        this.salesOwnerId = salesOwnerId;
    }
    
    public BigDecimal getDealAmount() {
        return dealAmount;
    }
    
    public void setDealAmount(BigDecimal dealAmount) {
        this.dealAmount = dealAmount;
    }
    
    public DealStatus getStatus() {
        return status;
    }
    
    public void setStatus(DealStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getDealAt() {
        return dealAt;
    }
    
    public void setDealAt(LocalDateTime dealAt) {
        this.dealAt = dealAt;
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
     * 检查交易是否已完成
     * 
     * @return 是否已完成
     */
    public boolean isCompleted() {
        return DealStatus.COMPLETED.equals(this.status);
    }
    
    /**
     * 检查交易是否待处理
     * 
     * @return 是否待处理
     */
    public boolean isPending() {
        return DealStatus.PENDING.equals(this.status);
    }
    
    /**
     * 检查交易是否已退款
     * 
     * @return 是否已退款
     */
    public boolean isRefunded() {
        return DealStatus.REFUNDED.equals(this.status);
    }
    
    /**
     * 完成交易
     */
    public void complete() {
        this.status = DealStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 退款交易
     */
    public void refund() {
        this.status = DealStatus.REFUNDED;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 获取实际业绩归属者ID
     * 
     * @return 业绩归属者ID
     */
    public Long getActualOwnerId() {
        return salesOwnerId != null ? salesOwnerId : salesId;
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
        Deal deal = (Deal) o;
        return Objects.equals(id, deal.id) &&
               Objects.equals(customerLeadId, deal.customerLeadId) &&
               Objects.equals(productId, deal.productId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, customerLeadId, productId);
    }
    
    @Override
    public String toString() {
        return "Deal{" +
                "id=" + id +
                ", customerLeadId=" + customerLeadId +
                ", productId=" + productId +
                ", salesId=" + salesId +
                ", salesOwnerId=" + salesOwnerId +
                ", dealAmount=" + dealAmount +
                ", status=" + status +
                ", dealAt=" + dealAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}