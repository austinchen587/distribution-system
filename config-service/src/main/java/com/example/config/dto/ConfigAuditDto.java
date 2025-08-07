package com.example.config.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 配置审计DTO - 用于配置变更审计和历史记录
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
@Schema(description = "配置审计记录")
public class ConfigAuditDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 审计记录ID
     */
    @Schema(description = "审计记录ID", example = "1")
    @NotNull
    private Long id;
    
    /**
     * 配置项ID
     */
    @Schema(description = "配置项ID", example = "10")
    @NotNull
    private Long configId;
    
    /**
     * 配置项键名
     */
    @Schema(description = "配置项键名", example = "system.reward.default_amount")
    @NotBlank
    private String configKey;
    
    /**
     * 操作类型
     */
    @Schema(description = "操作类型", example = "UPDATE", 
            allowableValues = {"CREATE", "UPDATE", "DELETE", "ENABLE", "DISABLE"})
    @NotBlank
    private String operationType;
    
    /**
     * 操作前的值
     */
    @Schema(description = "操作前的值", example = "50.00")
    private String oldValue;
    
    /**
     * 操作后的值
     */
    @Schema(description = "操作后的值", example = "100.00")
    private String newValue;
    
    /**
     * 变更原因
     */
    @Schema(description = "变更原因", example = "业务需求调整，提高默认奖励金额")
    private String changeReason;
    
    /**
     * 操作人ID
     */
    @Schema(description = "操作人ID", example = "5")
    @NotNull
    private Long operatorId;
    
    /**
     * 操作人用户名
     */
    @Schema(description = "操作人用户名", example = "admin")
    private String operatorUsername;
    
    /**
     * 操作人真实姓名
     */
    @Schema(description = "操作人真实姓名", example = "系统管理员")
    private String operatorRealName;
    
    /**
     * 操作IP地址
     */
    @Schema(description = "操作IP地址", example = "192.168.1.100")
    private String operatorIp;
    
    /**
     * 用户代理信息
     */
    @Schema(description = "用户代理信息", example = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
    private String userAgent;
    
    /**
     * 是否需要审批
     */
    @Schema(description = "是否需要审批", example = "true")
    private Boolean requiresApproval;
    
    /**
     * 审批状态
     */
    @Schema(description = "审批状态", example = "APPROVED", 
            allowableValues = {"PENDING", "APPROVED", "REJECTED", "NOT_REQUIRED"})
    private String approvalStatus;
    
    /**
     * 审批人ID
     */
    @Schema(description = "审批人ID", example = "1")
    private Long approverId;
    
    /**
     * 审批人姓名
     */
    @Schema(description = "审批人姓名", example = "超级管理员")
    private String approverName;
    
    /**
     * 审批意见
     */
    @Schema(description = "审批意见", example = "同意调整奖励金额")
    private String approvalComment;
    
    /**
     * 审批时间
     */
    @Schema(description = "审批时间", example = "2025-08-04 16:00:00")
    private String approvedAt;
    
    /**
     * 配置生效时间
     */
    @Schema(description = "配置生效时间", example = "2025-08-04 16:30:00")
    private String effectiveAt;
    
    /**
     * 操作时间
     */
    @Schema(description = "操作时间", example = "2025-08-04 15:30:00")
    private String createdAt;
    
    // Constructors
    public ConfigAuditDto() {}
    
    public ConfigAuditDto(Long id, Long configId, String configKey, String operationType, Long operatorId) {
        this.id = id;
        this.configId = configId;
        this.configKey = configKey;
        this.operationType = operationType;
        this.operatorId = operatorId;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getConfigId() {
        return configId;
    }
    
    public void setConfigId(Long configId) {
        this.configId = configId;
    }
    
    public String getConfigKey() {
        return configKey;
    }
    
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }
    
    public String getOperationType() {
        return operationType;
    }
    
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
    
    public String getOldValue() {
        return oldValue;
    }
    
    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }
    
    public String getNewValue() {
        return newValue;
    }
    
    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
    
    public String getChangeReason() {
        return changeReason;
    }
    
    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }
    
    public Long getOperatorId() {
        return operatorId;
    }
    
    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }
    
    public String getOperatorUsername() {
        return operatorUsername;
    }
    
    public void setOperatorUsername(String operatorUsername) {
        this.operatorUsername = operatorUsername;
    }
    
    public String getOperatorRealName() {
        return operatorRealName;
    }
    
    public void setOperatorRealName(String operatorRealName) {
        this.operatorRealName = operatorRealName;
    }
    
    public String getOperatorIp() {
        return operatorIp;
    }
    
    public void setOperatorIp(String operatorIp) {
        this.operatorIp = operatorIp;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public Boolean getRequiresApproval() {
        return requiresApproval;
    }
    
    public void setRequiresApproval(Boolean requiresApproval) {
        this.requiresApproval = requiresApproval;
    }
    
    public String getApprovalStatus() {
        return approvalStatus;
    }
    
    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
    
    public Long getApproverId() {
        return approverId;
    }
    
    public void setApproverId(Long approverId) {
        this.approverId = approverId;
    }
    
    public String getApproverName() {
        return approverName;
    }
    
    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }
    
    public String getApprovalComment() {
        return approvalComment;
    }
    
    public void setApprovalComment(String approvalComment) {
        this.approvalComment = approvalComment;
    }
    
    public String getApprovedAt() {
        return approvedAt;
    }
    
    public void setApprovedAt(String approvedAt) {
        this.approvedAt = approvedAt;
    }
    
    public String getEffectiveAt() {
        return effectiveAt;
    }
    
    public void setEffectiveAt(String effectiveAt) {
        this.effectiveAt = effectiveAt;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * 检查是否为创建操作  
     * 
     * @return 是否为创建操作
     */
    public boolean isCreateOperation() {
        return "CREATE".equals(operationType);
    }
    
    /**
     * 检查是否为更新操作
     * 
     * @return 是否为更新操作
     */
    public boolean isUpdateOperation() {
        return "UPDATE".equals(operationType);
    }
    
    /**
     * 检查是否为删除操作
     * 
     * @return 是否为删除操作
     */
    public boolean isDeleteOperation() {
        return "DELETE".equals(operationType);
    }
    
    /**
     * 检查是否需要审批
     * 
     * @return 是否需要审批
     */
    public boolean needsApproval() {
        return Boolean.TRUE.equals(requiresApproval);
    }
    
    /**
     * 检查是否待审批
     * 
     * @return 是否待审批
     */
    public boolean isPendingApproval() {
        return "PENDING".equals(approvalStatus);
    }
    
    /**
     * 检查是否已审批通过
     * 
     * @return 是否已审批通过
     */
    public boolean isApproved() {
        return "APPROVED".equals(approvalStatus);
    }
    
    /**
     * 检查是否被拒绝
     * 
     * @return 是否被拒绝
     */
    public boolean isRejected() {
        return "REJECTED".equals(approvalStatus);
    }
    
    /**
     * 检查配置是否已生效
     * 
     * @return 配置是否已生效
     */
    public boolean isEffective() {
        return effectiveAt != null;
    }
    
    /**
     * 获取变更摘要
     * 
     * @return 变更摘要
     */
    public String getChangeSummary() {
        if (isCreateOperation()) {
            return "创建配置: " + configKey + " = " + newValue;
        } else if (isUpdateOperation()) {
            return "更新配置: " + configKey + " 从 [" + oldValue + "] 变更为 [" + newValue + "]";
        } else if (isDeleteOperation()) {
            return "删除配置: " + configKey + " (原值: " + oldValue + ")";
        } else if ("ENABLE".equals(operationType)) {
            return "启用配置: " + configKey;
        } else if ("DISABLE".equals(operationType)) {
            return "禁用配置: " + configKey;
        }
        return "操作配置: " + configKey;
    }
    
    @Override
    public String toString() {
        return "ConfigAuditDto{" +
                "id=" + id +
                ", configId=" + configId +
                ", configKey='" + configKey + '\'' +
                ", operationType='" + operationType + '\'' +
                ", oldValue='" + oldValue + '\'' +
                ", newValue='" + newValue + '\'' +
                ", operatorUsername='" + operatorUsername + '\'' +
                ", operatorRealName='" + operatorRealName + '\'' +
                ", approvalStatus='" + approvalStatus + '\'' +
                ", approverName='" + approverName + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", effectiveAt='" + effectiveAt + '\'' +
                '}';
    }
}