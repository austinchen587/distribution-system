package com.example.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 推广任务审核DTO - 包含完整审核信息
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
@Schema(description = "推广任务审核信息")
public class PromotionAuditDto implements Serializable {
    
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
     * 代理真实姓名
     */
    @Schema(description = "代理真实姓名", example = "张三")
    private String agentRealName;
    
    /**
     * 推广任务标题
     */
    @Schema(description = "推广任务标题", example = "微信朋友圈理财产品推广")
    @NotBlank
    private String title;
    
    /**
     * 推广内容描述
     */
    @Schema(description = "推广内容描述", example = "通过朋友圈分享理财产品信息，吸引潜在客户")
    private String description;
    
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
     * 机器审核结果
     */
    @Schema(description = "机器审核结果", example = "PASSED")
    private String machineAuditResult;
    
    /**
     * 机器审核原因
     */
    @Schema(description = "机器审核原因", example = "内容合规，链接有效")
    private String machineAuditReason;
    
    /**
     * 机器审核时间
     */
    @Schema(description = "机器审核时间", example = "2025-08-04 10:05:00")
    private String machineAuditTime;
    
    /**
     * 人工审核员ID
     */
    @Schema(description = "人工审核员ID", example = "5")
    private Long manualAuditorId;
    
    /**
     * 人工审核员用户名
     */
    @Schema(description = "人工审核员用户名", example = "admin001")
    private String manualAuditorUsername;
    
    /**
     * 人工审核结果
     */
    @Schema(description = "人工审核结果", example = "APPROVED")
    private String manualAuditResult;
    
    /**
     * 人工审核意见
     */
    @Schema(description = "人工审核意见", example = "推广内容质量高，符合要求")
    private String manualAuditComment;
    
    /**
     * 人工审核时间
     */
    @Schema(description = "人工审核时间", example = "2025-08-04 14:30:00")
    private String manualAuditTime;
    
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
    public PromotionAuditDto() {}
    
    public PromotionAuditDto(Long id, Long agentId, String title, String auditStatus) {
        this.id = id;
        this.agentId = agentId;
        this.title = title;
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
    
    public String getAgentRealName() {
        return agentRealName;
    }
    
    public void setAgentRealName(String agentRealName) {
        this.agentRealName = agentRealName;
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
    
    public String getMachineAuditResult() {
        return machineAuditResult;
    }
    
    public void setMachineAuditResult(String machineAuditResult) {
        this.machineAuditResult = machineAuditResult;
    }
    
    public String getMachineAuditReason() {
        return machineAuditReason;
    }
    
    public void setMachineAuditReason(String machineAuditReason) {
        this.machineAuditReason = machineAuditReason;
    }
    
    public String getMachineAuditTime() {
        return machineAuditTime;
    }
    
    public void setMachineAuditTime(String machineAuditTime) {
        this.machineAuditTime = machineAuditTime;
    }
    
    public Long getManualAuditorId() {
        return manualAuditorId;
    }
    
    public void setManualAuditorId(Long manualAuditorId) {
        this.manualAuditorId = manualAuditorId;
    }
    
    public String getManualAuditorUsername() {
        return manualAuditorUsername;
    }
    
    public void setManualAuditorUsername(String manualAuditorUsername) {
        this.manualAuditorUsername = manualAuditorUsername;
    }
    
    public String getManualAuditResult() {
        return manualAuditResult;
    }
    
    public void setManualAuditResult(String manualAuditResult) {
        this.manualAuditResult = manualAuditResult;
    }
    
    public String getManualAuditComment() {
        return manualAuditComment;
    }
    
    public void setManualAuditComment(String manualAuditComment) {
        this.manualAuditComment = manualAuditComment;
    }
    
    public String getManualAuditTime() {
        return manualAuditTime;
    }
    
    public void setManualAuditTime(String manualAuditTime) {
        this.manualAuditTime = manualAuditTime;
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
     * 检查是否已完成机器审核
     * 
     * @return 是否已完成机器审核
     */
    public boolean isMachineAuditCompleted() {
        return machineAuditResult != null && !machineAuditResult.trim().isEmpty();
    }
    
    /**
     * 检查是否已完成人工审核
     * 
     * @return 是否已完成人工审核
     */
    public boolean isManualAuditCompleted() {
        return manualAuditResult != null && !manualAuditResult.trim().isEmpty();
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
     * 获取当前审核阶段
     * 
     * @return 审核阶段描述
     */
    public String getCurrentAuditStage() {
        switch (auditStatus) {
            case "PENDING_MACHINE_AUDIT":
                return "机器审核中";
            case "PENDING_MANUAL_AUDIT":
                return "人工审核中";
            case "APPROVED":
                return "审核通过";
            case "REJECTED":
                return "审核驳回";
            default:
                return "未知状态";
        }
    }
    
    @Override
    public String toString() {
        return "PromotionAuditDto{" +
                "id=" + id +
                ", agentId=" + agentId +
                ", agentUsername='" + agentUsername + '\'' +
                ", title='" + title + '\'' +
                ", platform='" + platform + '\'' +
                ", auditStatus='" + auditStatus + '\'' +
                ", machineAuditResult='" + machineAuditResult + '\'' +
                ", manualAuditResult='" + manualAuditResult + '\'' +
                ", submittedAt='" + submittedAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}