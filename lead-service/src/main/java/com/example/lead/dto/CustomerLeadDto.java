package com.example.lead.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 客户线索基础信息DTO - 用于列表展示
 * 轻量级设计，优化列表查询性能
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
@Schema(description = "客户线索基础信息")
public class CustomerLeadDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 客资ID
     */
    @Schema(description = "客资ID", example = "1")
    @NotNull
    private Long id;
    
    /**
     * 客户姓名
     */
    @Schema(description = "客户姓名", example = "李四")
    @NotBlank
    private String name;
    
    /**
     * 客户手机号
     */
    @Schema(description = "客户手机号", example = "13900139000")
    @NotBlank
    private String phone;
    
    /**
     * 客资跟进状态
     */
    @Schema(description = "客资跟进状态", example = "PENDING", allowableValues = {"PENDING", "FOLLOWING", "CONVERTED", "INVALID"})
    private String status;
    
    /**
     * 审核状态
     */
    @Schema(description = "审核状态", example = "PENDING_AUDIT", allowableValues = {"PENDING_AUDIT", "APPROVED", "REJECTED"})
    private String auditStatus;
    
    /**
     * 来源渠道
     */
    @Schema(description = "来源渠道", example = "微信群")
    private String source;
    
    /**
     * 归属销售ID
     */
    @Schema(description = "归属销售ID", example = "10")
    private Long salespersonId;
    
    /**
     * 归属销售姓名
     */
    @Schema(description = "归属销售姓名", example = "张三")
    private String salespersonName;
    
    /**
     * 最后跟进时间
     */
    @Schema(description = "最后跟进时间", example = "2025-08-04 10:00:00")
    private String lastFollowUpAt;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2025-08-01 10:00:00")
    private String createdAt;
    
    // Constructors
    public CustomerLeadDto() {}
    
    public CustomerLeadDto(Long id, String name, String phone, String status, String auditStatus) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.status = status;
        this.auditStatus = auditStatus;
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
    
    public String getLastFollowUpAt() {
        return lastFollowUpAt;
    }
    
    public void setLastFollowUpAt(String lastFollowUpAt) {
        this.lastFollowUpAt = lastFollowUpAt;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "CustomerLeadDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", status='" + status + '\'' +
                ", auditStatus='" + auditStatus + '\'' +
                ", source='" + source + '\'' +
                ", salespersonId=" + salespersonId +
                ", salespersonName='" + salespersonName + '\'' +
                ", lastFollowUpAt='" + lastFollowUpAt + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}