package com.example.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 推广任务基础信息DTO - 用于列表展示
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
@Schema(description = "推广任务基础信息")
public class PromotionDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 推广任务ID
     */
    @Schema(description = "推广任务ID", example = "1")
    @NotNull
    private Long id;
    
    /**
     * 提交任务的代理ID
     */
    @Schema(description = "提交任务的代理ID", example = "10")
    @NotNull
    private Long agentId;
    
    /**
     * 代理用户名
     */
    @Schema(description = "代理用户名", example = "agent001")
    private String agentUsername;
    
    /**
     * 推广任务标题
     */
    @Schema(description = "推广任务标题", example = "微信朋友圈理财产品推广")
    @NotBlank
    private String title;
    
    /**
     * 推广平台
     */
    @Schema(description = "推广平台", example = "微信朋友圈")
    @NotBlank
    private String platform;
    
    /**
     * 推广内容URL
     */
    @Schema(description = "推广内容URL", example = "https://example.com/promotion/123")
    @NotBlank
    private String contentUrl;
    
    /**
     * 内容标签
     */
    @Schema(description = "内容标签", example = "[\"理财\", \"投资\", \"稳健\"]")
    private List<String> tags;
    
    /**
     * 代理期望奖励
     */
    @Schema(description = "代理期望奖励", example = "100.00")
    private BigDecimal expectedReward;
    
    /**
     * 实际发放奖励
     */
    @Schema(description = "实际发放奖励", example = "80.00")
    private BigDecimal actualReward;
    
    /**
     * 审核状态
     */
    @Schema(description = "审核状态", example = "PENDING_MACHINE_AUDIT", 
            allowableValues = {"PENDING_MACHINE_AUDIT", "PENDING_MANUAL_AUDIT", "APPROVED", "REJECTED"})
    private String auditStatus;
    
    /**
     * 任务提交时间
     */
    @Schema(description = "任务提交时间", example = "2025-08-04 10:00:00")
    private String submittedAt;
    
    /**
     * 最后更新时间
     */
    @Schema(description = "最后更新时间", example = "2025-08-04 15:30:00")
    private String updatedAt;
    
    // Constructors
    public PromotionDto() {}
    
    public PromotionDto(Long id, Long agentId, String title, String platform, String auditStatus) {
        this.id = id;
        this.agentId = agentId;
        this.title = title;
        this.platform = platform;
        this.auditStatus = auditStatus;
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
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
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
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
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
    
    public String getAuditStatus() {
        return auditStatus;
    }
    
    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }
    
    public String getSubmittedAt() {
        return submittedAt;
    }
    
    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * 检查是否待机器审核
     * 
     * @return 是否待机器审核
     */
    public boolean isPendingMachineAudit() {
        return "PENDING_MACHINE_AUDIT".equals(auditStatus);
    }
    
    /**
     * 检查是否待人工审核
     * 
     * @return 是否待人工审核
     */
    public boolean isPendingManualAudit() {
        return "PENDING_MANUAL_AUDIT".equals(auditStatus);
    }
    
    /**
     * 检查是否已通过审核
     * 
     * @return 是否已通过审核
     */
    public boolean isApproved() {
        return "APPROVED".equals(auditStatus);
    }
    
    /**
     * 检查是否被驳回
     * 
     * @return 是否被驳回
     */
    public boolean isRejected() {
        return "REJECTED".equals(auditStatus);
    }
    
    /**
     * 检查是否需要审核
     * 
     * @return 是否需要审核
     */
    public boolean needsAudit() {
        return isPendingMachineAudit() || isPendingManualAudit();
    }
    
    @Override
    public String toString() {
        return "PromotionDto{" +
                "id=" + id +
                ", agentId=" + agentId +
                ", agentUsername='" + agentUsername + '\'' +
                ", title='" + title + '\'' +
                ", platform='" + platform + '\'' +
                ", contentUrl='" + contentUrl + '\'' +
                ", expectedReward=" + expectedReward +
                ", actualReward=" + actualReward +
                ", auditStatus='" + auditStatus + '\'' +
                ", submittedAt='" + submittedAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}