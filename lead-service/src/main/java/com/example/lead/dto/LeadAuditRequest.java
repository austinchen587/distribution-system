package com.example.lead.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 客资审核请求DTO
 * 用于客资审核流程
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
@Schema(description = "客资审核请求")
public class LeadAuditRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 客资ID
     */
    @Schema(description = "客资ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "客资ID不能为空")
    private Long leadId;
    
    /**
     * 审核结果
     */
    @Schema(description = "审核结果", example = "APPROVED", 
            allowableValues = {"APPROVED", "REJECTED"}, 
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "审核结果不能为空")
    @Pattern(regexp = "^(APPROVED|REJECTED)$", message = "审核结果必须是: APPROVED 或 REJECTED")
    private String auditResult;
    
    /**
     * 审核意见
     */
    @Schema(description = "审核意见", example = "客资质量良好，符合审核标准")
    @Size(max = 500, message = "审核意见长度不能超过500字符")
    private String auditComment;
    
    /**
     * 拒绝原因（当审核结果为REJECTED时必填）
     */
    @Schema(description = "拒绝原因", example = "客资信息不完整")
    @Size(max = 255, message = "拒绝原因长度不能超过255字符")
    private String rejectReason;
    
    /**
     * 审核人ID（通常从JWT token中获取）
     */
    @Schema(description = "审核人ID", example = "5")
    private Long auditorId;
    
    // Constructors
    public LeadAuditRequest() {}
    
    public LeadAuditRequest(Long leadId, String auditResult) {
        this.leadId = leadId;
        this.auditResult = auditResult;
    }
    
    // Getters and Setters
    public Long getLeadId() {
        return leadId;
    }
    
    public void setLeadId(Long leadId) {
        this.leadId = leadId;
    }
    
    public String getAuditResult() {
        return auditResult;
    }
    
    public void setAuditResult(String auditResult) {
        this.auditResult = auditResult;
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
    
    public Long getAuditorId() {
        return auditorId;
    }
    
    public void setAuditorId(Long auditorId) {
        this.auditorId = auditorId;
    }
    
    /**
     * 验证审核请求的业务逻辑
     * 
     * @return 验证错误信息，null表示验证通过
     */
    public String validateBusinessLogic() {
        if ("REJECTED".equals(auditResult)) {
            if (rejectReason == null || rejectReason.trim().isEmpty()) {
                return "审核结果为拒绝时，必须填写拒绝原因";
            }
        }
        return null;
    }
    
    /**
     * 检查是否为通过审核
     * 
     * @return 是否通过审核
     */
    public boolean isApproved() {
        return "APPROVED".equals(auditResult);
    }
    
    /**
     * 检查是否为拒绝审核
     * 
     * @return 是否拒绝审核
     */
    public boolean isRejected() {
        return "REJECTED".equals(auditResult);
    }
    
    @Override
    public String toString() {
        return "LeadAuditRequest{" +
                "leadId=" + leadId +
                ", auditResult='" + auditResult + '\'' +
                ", auditComment='" + auditComment + '\'' +
                ", rejectReason='" + rejectReason + '\'' +
                ", auditorId=" + auditorId +
                '}';
    }
}