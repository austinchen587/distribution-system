package com.example.promotion.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 推广任务实体类
 * 
 * @author System
 * @version 1.0
 * @since 2025-08-05
 */
public class Promotion implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 推广任务ID
     */
    private Long id;
    
    /**
     * 推广标题
     */
    private String title;
    
    /**
     * 推广内容描述
     */
    private String description;
    
    /**
     * 推广平台
     */
    private String platform;
    
    /**
     * 推广类型
     */
    private String type;
    
    /**
     * 任务状态
     */
    private String status;
    
    /**
     * 审核状态
     */
    private String auditStatus;
    
    /**
     * 创建者ID
     */
    private Long creatorId;
    
    /**
     * 创建者姓名
     */
    private String creatorName;
    
    /**
     * 审核人ID
     */
    private Long auditorId;
    
    /**
     * 审核人姓名
     */
    private String auditorName;
    
    /**
     * 审核意见
     */
    private String auditComment;
    
    /**
     * 拒绝原因
     */
    private String rejectReason;
    
    /**
     * 创建时间
     */
    private Date createdAt;
    
    /**
     * 更新时间
     */
    private Date updatedAt;
    
    /**
     * 审核时间
     */
    private Date auditedAt;
    
    // Constructors
    public Promotion() {}
    
    public Promotion(String title, String description, String platform, String type, Long creatorId) {
        this.title = title;
        this.description = description;
        this.platform = platform;
        this.type = type;
        this.creatorId = creatorId;
        this.status = "DRAFT";
        this.auditStatus = "PENDING_AUDIT";
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getAuditStatus() {
        return auditStatus;
    }
    
    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }
    
    public Long getCreatorId() {
        return creatorId;
    }
    
    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }
    
    public String getCreatorName() {
        return creatorName;
    }
    
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
    
    public Long getAuditorId() {
        return auditorId;
    }
    
    public void setAuditorId(Long auditorId) {
        this.auditorId = auditorId;
    }
    
    public String getAuditorName() {
        return auditorName;
    }
    
    public void setAuditorName(String auditorName) {
        this.auditorName = auditorName;
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
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Date getAuditedAt() {
        return auditedAt;
    }
    
    public void setAuditedAt(Date auditedAt) {
        this.auditedAt = auditedAt;
    }
    
    @Override
    public String toString() {
        return "Promotion{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", platform='" + platform + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", auditStatus='" + auditStatus + '\'' +
                ", creatorId=" + creatorId +
                ", creatorName='" + creatorName + '\'' +
                ", auditorId=" + auditorId +
                ", auditorName='" + auditorName + '\'' +
                ", auditComment='" + auditComment + '\'' +
                ", rejectReason='" + rejectReason + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", auditedAt=" + auditedAt +
                '}';
    }
}