package com.example.lead.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * 客资详细信息DTO - 包含完整的跟进历史和审核信息
 * 用于需要完整客资信息的场景
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
@Schema(description = "客资详细信息")
public class LeadDetailsDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 客资基础信息
     */
    @Schema(description = "客资基础信息")
    private CustomerLeadDto leadInfo;
    
    /**
     * 客户微信号
     */
    @Schema(description = "客户微信号", example = "wechat_lisi")
    private String wechatId;
    
    /**
     * 来源详情
     */
    @Schema(description = "来源详情", example = "XX理财群推荐")
    private String sourceDetail;
    
    /**
     * 跟进备注
     */
    @Schema(description = "跟进备注", example = "客户对理财产品比较感兴趣，计划下周电话沟通")
    private String notes;
    
    /**
     * 推荐码
     */
    @Schema(description = "推荐码", example = "REF123456")
    private String referralCode;
    
    /**
     * 审核意见
     */
    @Schema(description = "审核意见", example = "客资质量良好，符合审核标准")
    private String auditComment;
    
    /**
     * 拒绝原因
     */
    @Schema(description = "拒绝原因", example = "客资信息不完整")
    private String rejectReason;
    
    /**
     * 审核人姓名
     */
    @Schema(description = "审核人姓名", example = "审核员A")
    private String auditorName;
    
    /**
     * 审核时间
     */
    @Schema(description = "审核时间", example = "2025-08-02 15:30:00")
    private String auditedAt;
    
    /**
     * 记录更新时间
     */
    @Schema(description = "记录更新时间", example = "2025-08-04 10:00:00")
    private String updatedAt;
    
    // Constructors
    public LeadDetailsDto() {}
    
    public LeadDetailsDto(CustomerLeadDto leadInfo) {
        this.leadInfo = leadInfo;
    }
    
    // Getters and Setters
    public CustomerLeadDto getLeadInfo() {
        return leadInfo;
    }
    
    public void setLeadInfo(CustomerLeadDto leadInfo) {
        this.leadInfo = leadInfo;
    }
    
    public String getWechatId() {
        return wechatId;
    }
    
    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }
    
    public String getSourceDetail() {
        return sourceDetail;
    }
    
    public void setSourceDetail(String sourceDetail) {
        this.sourceDetail = sourceDetail;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getReferralCode() {
        return referralCode;
    }
    
    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }
    
    public String getAuditComment() {
        return auditComment;
    }
    
    public void setAuditComment(String auditComment) {
        this.auditComment = auditComment;
    }
    
    public String getRejectReason() {
        return rejectReason;
    }
    
    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
    
    public String getAuditorName() {
        return auditorName;
    }
    
    public void setAuditorName(String auditorName) {
        this.auditorName = auditorName;
    }
    
    public String getAuditedAt() {
        return auditedAt;
    }
    
    public void setAuditedAt(String auditedAt) {
        this.auditedAt = auditedAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * 检查是否需要跟进
     * 
     * @return 是否需要跟进
     */
    public boolean needsFollowUp() {
        if (leadInfo == null) {
            return false;
        }
        return "PENDING".equals(leadInfo.getStatus()) || "FOLLOWING".equals(leadInfo.getStatus());
    }
    
    /**
     * 检查是否已通过审核
     * 
     * @return 是否已通过审核
     */
    public boolean isApproved() {
        if (leadInfo == null) {
            return false;
        }
        return "APPROVED".equals(leadInfo.getAuditStatus());
    }
    
    /**
     * 检查是否已转化
     * 
     * @return 是否已转化
     */
    public boolean isConverted() {
        if (leadInfo == null) {
            return false;
        }
        return "CONVERTED".equals(leadInfo.getStatus());
    }
    
    @Override
    public String toString() {
        return "LeadDetailsDto{" +
                "leadInfo=" + leadInfo +
                ", wechatId='" + wechatId + '\'' +
                ", sourceDetail='" + sourceDetail + '\'' +
                ", notes='" + notes + '\'' +
                ", referralCode='" + referralCode + '\'' +
                ", auditComment='" + auditComment + '\'' +
                ", rejectReason='" + rejectReason + '\'' +
                ", auditorName='" + auditorName + '\'' +
                ", auditedAt='" + auditedAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}