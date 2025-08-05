package com.example.lead.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户线索实体类
 * 
 * @author System
 * @version 1.0
 * @since 2025-08-05
 */
public class CustomerLead implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 客资ID
     */
    private Long id;
    
    /**
     * 客户姓名
     */
    private String name;
    
    /**
     * 客户手机号
     */
    private String phone;
    
    /**
     * 客资跟进状态
     */
    private String status;
    
    /**
     * 审核状态
     */
    private String auditStatus;
    
    /**
     * 来源渠道
     */
    private String source;
    
    /**
     * 归属销售ID
     */
    private Long salespersonId;
    
    /**
     * 归属销售姓名
     */
    private String salespersonName;
    
    /**
     * 最后跟进时间
     */
    private Date lastFollowUpAt;
    
    /**
     * 创建时间
     */
    private Date createdAt;
    
    /**
     * 更新时间
     */
    private Date updatedAt;
    
    // Constructors
    public CustomerLead() {}
    
    public CustomerLead(String name, String phone, String source, Long salespersonId) {
        this.name = name;
        this.phone = phone;
        this.source = source;
        this.salespersonId = salespersonId;
        this.status = "PENDING";
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
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
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public Long getSalespersonId() {
        return salespersonId;
    }
    
    public void setSalespersonId(Long salespersonId) {
        this.salespersonId = salespersonId;
    }
    
    public String getSalespersonName() {
        return salespersonName;
    }
    
    public void setSalespersonName(String salespersonName) {
        this.salespersonName = salespersonName;
    }
    
    public Date getLastFollowUpAt() {
        return lastFollowUpAt;
    }
    
    public void setLastFollowUpAt(Date lastFollowUpAt) {
        this.lastFollowUpAt = lastFollowUpAt;
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
    
    @Override
    public String toString() {
        return "CustomerLead{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", status='" + status + '\'' +
                ", auditStatus='" + auditStatus + '\'' +
                ", source='" + source + '\'' +
                ", salespersonId=" + salespersonId +
                ", salespersonName='" + salespersonName + '\'' +
                ", lastFollowUpAt=" + lastFollowUpAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}