package com.example.data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 推广任务实体类
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-03
 */
public class Promotion {
    
    /**
     * 推广任务ID
     */
    private Long id;
    
    /**
     * 提交任务的代理ID
     */
    @NotNull(message = "代理ID不能为空")
    private Long agentId;
    
    /**
     * 推广任务的标题
     */
    @NotBlank(message = "推广标题不能为空")
    @Size(max = 255, message = "推广标题长度不能超过255字符")
    private String title;
    
    /**
     * 推广内容的描述
     */
    private String description;
    
    /**
     * 推广平台
     */
    @NotBlank(message = "推广平台不能为空")
    @Size(max = 50, message = "推广平台长度不能超过50字符")
    private String platform;
    
    /**
     * 推广内容的URL链接
     */
    @NotBlank(message = "推广链接不能为空")
    @Size(max = 512, message = "推广链接长度不能超过512字符")
    private String contentUrl;
    
    /**
     * 内容标签 (逗号分隔字符串)
     */
    private String tags;
    
    /**
     * 代理期望获得的奖励
     */
    @DecimalMin(value = "0.00", message = "期望奖励不能为负数")
    private BigDecimal expectedReward;
    
    /**
     * 审核后实际发放的奖励
     */
    @DecimalMin(value = "0.00", message = "实际奖励不能为负数")
    private BigDecimal actualReward;
    
    /**
     * 审核状态
     */
    @NotNull(message = "审核状态不能为空")
    private PromotionAuditStatus auditStatus = PromotionAuditStatus.PENDING_MACHINE_AUDIT;
    
    /**
     * 任务提交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submittedAt;
    
    /**
     * 记录最后更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 推广审核状态枚举  
    public enum PromotionAuditStatus {
        PENDING_MACHINE_AUDIT("PENDING_MACHINE_AUDIT", "待机器审核"),
        PENDING_MANUAL_AUDIT("PENDING_MANUAL_AUDIT", "待人工审核"),
        APPROVED("APPROVED", "已通过"),
        REJECTED("REJECTED", "已驳回");
        
        private final String code;
        private final String description;
        
        PromotionAuditStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        public static PromotionAuditStatus fromCode(String code) {
            for (PromotionAuditStatus status : values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown promotion audit status code: " + code);
        }
    }
    
    // Constructors
    public Promotion() {}
    
    public Promotion(Long agentId, String title, String platform, String contentUrl) {
        this.agentId = agentId;
        this.title = title;
        this.platform = platform;
        this.contentUrl = contentUrl;
        this.auditStatus = PromotionAuditStatus.PENDING_MACHINE_AUDIT;
        this.submittedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getPlatform() {
        return platform;
    }
    
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    
    public String getContentUrl() {
        return contentUrl;
    }
    
    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public BigDecimal getExpectedReward() {
        return expectedReward;
    }
    
    public void setExpectedReward(BigDecimal expectedReward) {
        this.expectedReward = expectedReward;
    }
    
    public BigDecimal getActualReward() {
        return actualReward;
    }
    
    public void setActualReward(BigDecimal actualReward) {
        this.actualReward = actualReward;
    }
    
    public PromotionAuditStatus getAuditStatus() {
        return auditStatus;
    }
    
    public void setAuditStatus(PromotionAuditStatus auditStatus) {
        this.auditStatus = auditStatus;
    }
    
    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
    
    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Business Methods
    
    /**
     * 检查是否待机器审核
     * 
     * @return 是否待机器审核
     */
    public boolean isPendingMachineAudit() {
        return PromotionAuditStatus.PENDING_MACHINE_AUDIT.equals(this.auditStatus);
    }
    
    /**
     * 检查是否待人工审核
     * 
     * @return 是否待人工审核
     */
    public boolean isPendingManualAudit() {
        return PromotionAuditStatus.PENDING_MANUAL_AUDIT.equals(this.auditStatus);
    }
    
    /**
     * 检查是否已通过审核
     * 
     * @return 是否已通过审核
     */
    public boolean isApproved() {
        return PromotionAuditStatus.APPROVED.equals(this.auditStatus);
    }
    
    /**
     * 检查是否被驳回
     * 
     * @return 是否被驳回
     */
    public boolean isRejected() {
        return PromotionAuditStatus.REJECTED.equals(this.auditStatus);
    }
    
    /**
     * 检查是否需要审核
     * 
     * @return 是否需要审核
     */
    public boolean needsAudit() {
        return isPendingMachineAudit() || isPendingManualAudit();
    }
    
    /**
     * 检查审核是否完成
     * 
     * @return 审核是否完成
     */
    public boolean isAuditCompleted() {
        return isApproved() || isRejected();
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
        Promotion promotion = (Promotion) o;
        return Objects.equals(id, promotion.id) &&
               Objects.equals(contentUrl, promotion.contentUrl);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, contentUrl);
    }
    
    @Override
    public String toString() {
        return "Promotion{" +
                "id=" + id +
                ", agentId=" + agentId +
                ", title='" + title + '\'' +
                ", platform='" + platform + '\'' +
                ", contentUrl='" + contentUrl + '\'' +
                ", auditStatus=" + auditStatus +
                ", expectedReward=" + expectedReward +
                ", actualReward=" + actualReward +
                ", submittedAt=" + submittedAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}