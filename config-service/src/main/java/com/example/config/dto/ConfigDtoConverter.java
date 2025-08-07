package com.example.config.dto;

// Removed direct entity dependency to avoid circular dependency
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 配置DTO转换工具类
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
public class ConfigDtoConverter {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigDtoConverter.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Note: Direct entity conversion methods removed to avoid circular dependency
    // These methods should be implemented in the service layer where both
    // common and data-access modules are available
    
    /**
     * 创建配置审计记录
     *
     * @param configId 配置ID
     * @param configKey 配置键名
     * @param operationType 操作类型
     * @param oldValue 旧值
     * @param newValue 新值
     * @param changeReason 变更原因
     * @param operatorId 操作人ID
     * @param operatorUsername 操作人用户名
     * @param operatorRealName 操作人姓名
     * @param operatorIp 操作IP
     * @param userAgent 用户代理
     * @return ConfigAuditDto
     */
    public static ConfigAuditDto createAuditRecord(Long configId, String configKey, String operationType,
            String oldValue, String newValue, String changeReason, Long operatorId, String operatorUsername,
            String operatorRealName, String operatorIp, String userAgent) {
        try {
            ConfigAuditDto dto = new ConfigAuditDto();
            dto.setConfigId(configId);
            dto.setConfigKey(configKey);
            dto.setOperationType(operationType);
            dto.setOldValue(oldValue);
            dto.setNewValue(newValue);
            dto.setChangeReason(changeReason);
            dto.setOperatorId(operatorId);
            dto.setOperatorUsername(operatorUsername);
            dto.setOperatorRealName(operatorRealName);
            dto.setOperatorIp(operatorIp);
            dto.setUserAgent(userAgent);
            dto.setCreatedAt(LocalDateTime.now().format(DATE_TIME_FORMATTER));
            
            // 根据操作类型判断是否需要审批
            boolean requiresApproval = isOperationRequiresApproval(operationType, configKey);
            dto.setRequiresApproval(requiresApproval);
            dto.setApprovalStatus(requiresApproval ? "PENDING" : "NOT_REQUIRED");
            
            return dto;
        } catch (Exception e) {
            logger.error("创建配置审计记录时发生错误: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 更新配置审计记录的审批信息
     *
     * @param audit 审计记录
     * @param approvalStatus 审批状态
     * @param approverId 审批人ID
     * @param approverName 审批人姓名
     * @param approvalComment 审批意见
     * @return 更新后的ConfigAuditDto
     */
    public static ConfigAuditDto updateAuditApproval(ConfigAuditDto audit, String approvalStatus,
            Long approverId, String approverName, String approvalComment) {
        if (audit == null) {
            return null;
        }
        
        try {
            audit.setApprovalStatus(approvalStatus);
            audit.setApproverId(approverId);
            audit.setApproverName(approverName);
            audit.setApprovalComment(approvalComment);
            audit.setApprovedAt(LocalDateTime.now().format(DATE_TIME_FORMATTER));
            
            // 如果审批通过，设置配置生效时间
            if ("APPROVED".equals(approvalStatus)) {
                audit.setEffectiveAt(LocalDateTime.now().format(DATE_TIME_FORMATTER));
            }
            
            return audit;
        } catch (Exception e) {
            logger.error("更新配置审计审批信息时发生错误: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 创建新配置
     *
     * @param configKey 配置键名
     * @param configValue 配置值
     * @param valueType 值类型
     * @param category 配置分类
     * @param name 配置名称
     * @param description 配置描述
     * @param isSystem 是否系统配置
     * @param enabled 是否启用
     * @param validationRule 验证规则
     * @param defaultValue 默认值
     * @param sortOrder 排序
     * @param updatedBy 更新人ID
     * @param updatedByName 更新人姓名
     * @return ConfigDto
     */
    public static ConfigDto createConfig(String configKey, String configValue, String valueType, String category,
            String name, String description, Boolean isSystem, Boolean enabled, String validationRule,
            String defaultValue, Integer sortOrder, Long updatedBy, String updatedByName) {
        try {
            ConfigDto dto = new ConfigDto();
            dto.setConfigKey(configKey);
            dto.setConfigValue(configValue);
            dto.setValueType(valueType);
            dto.setCategory(category);
            dto.setName(name);
            dto.setDescription(description);
            dto.setIsSystem(isSystem != null ? isSystem : false);
            dto.setEnabled(enabled != null ? enabled : true);
            dto.setValidationRule(validationRule);
            dto.setDefaultValue(defaultValue);
            dto.setSortOrder(sortOrder != null ? sortOrder : 0);
            dto.setUpdatedBy(updatedBy);
            dto.setUpdatedByName(updatedByName);
            
            dto.setCreatedAt(LocalDateTime.now().format(DATE_TIME_FORMATTER));
            dto.setUpdatedAt(LocalDateTime.now().format(DATE_TIME_FORMATTER));
            
            return dto;
        } catch (Exception e) {
            logger.error("创建配置时发生错误: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 更新配置值
     *
     * @param originalConfig 原配置
     * @param newValue 新值
     * @param changeReason 变更原因
     * @param updatedBy 更新人ID
     * @param updatedByName 更新人姓名
     * @return 更新后的ConfigDto
     */
    public static ConfigDto updateConfigValue(ConfigDto originalConfig, String newValue, String changeReason,
            Long updatedBy, String updatedByName) {
        if (originalConfig == null) {
            return null;
        }
        
        try {
            // 创建副本以避免修改原对象
            ConfigDto updatedConfig = new ConfigDto();
            updatedConfig.setId(originalConfig.getId());
            updatedConfig.setConfigKey(originalConfig.getConfigKey());
            updatedConfig.setConfigValue(newValue);
            updatedConfig.setValueType(originalConfig.getValueType());
            updatedConfig.setCategory(originalConfig.getCategory());
            updatedConfig.setName(originalConfig.getName());
            updatedConfig.setDescription(originalConfig.getDescription());
            updatedConfig.setIsSystem(originalConfig.getIsSystem());
            updatedConfig.setEnabled(originalConfig.getEnabled());
            updatedConfig.setSortOrder(originalConfig.getSortOrder());
            updatedConfig.setValidationRule(originalConfig.getValidationRule());
            updatedConfig.setDefaultValue(originalConfig.getDefaultValue());
            updatedConfig.setCreatedAt(originalConfig.getCreatedAt());
            updatedConfig.setUpdatedAt(LocalDateTime.now().format(DATE_TIME_FORMATTER));
            updatedConfig.setUpdatedBy(updatedBy);
            updatedConfig.setUpdatedByName(updatedByName);
            
            return updatedConfig;
        } catch (Exception e) {
            logger.error("更新配置值时发生错误: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 检查是否可以查看配置详情
     *
     * @param currentUserRole 当前用户角色
     * @param currentUserId 当前用户ID
     * @return 是否可以查看
     */
    public static boolean canViewConfigDetails(String currentUserRole, Long currentUserId) {
        if (currentUserRole == null || currentUserId == null) {
            return false;
        }
        
        // 超级管理员、总监和主管可以查看配置详情
        return "super_admin".equals(currentUserRole) || "director".equals(currentUserRole) || "leader".equals(currentUserRole);
    }
    
    /**
     * 检查是否可以修改配置
     *
     * @param currentUserRole 当前用户角色
     * @param configKey 配置键名
     * @param isSystemConfig 是否系统配置
     * @return 是否可以修改
     */
    public static boolean canModifyConfig(String currentUserRole, String configKey, Boolean isSystemConfig) {
        if (currentUserRole == null) {
            return false;
        }
        
        // 系统内置配置只有超级管理员可以修改
        if (Boolean.TRUE.equals(isSystemConfig)) {
            return "super_admin".equals(currentUserRole);
        }
        
        // 超级管理员和总监可以修改普通配置
        return "super_admin".equals(currentUserRole) || "director".equals(currentUserRole);
    }
    
    /**
     * 检查是否可以审批配置变更
     *
     * @param currentUserRole 当前用户角色
     * @return 是否可以审批
     */
    public static boolean canApproveConfigChange(String currentUserRole) {
        if (currentUserRole == null) {
            return false;
        }
        
        // 只有超级管理员可以审批配置变更
        return "super_admin".equals(currentUserRole);
    }
    
    /**
     * 判断操作是否需要审批
     *
     * @param operationType 操作类型
     * @param configKey 配置键名
     * @return 是否需要审批
     */
    private static boolean isOperationRequiresApproval(String operationType, String configKey) {
        // 删除操作和系统关键配置的修改需要审批
        if ("DELETE".equals(operationType)) {
            return true;
        }
        
        // 系统关键配置需要审批
        if (configKey != null && (configKey.startsWith("system.") || configKey.contains("security") || configKey.contains("payment"))) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 验证配置值类型
     *
     * @param valueType 值类型
     * @return 是否有效
     */
    public static boolean isValidValueType(String valueType) {
        if (valueType == null || valueType.trim().isEmpty()) {
            return false;
        }
        
        String[] validTypes = {"STRING", "INTEGER", "DECIMAL", "BOOLEAN", "JSON", "URL"};
        for (String validType : validTypes) {
            if (validType.equals(valueType.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 验证配置分类
     *
     * @param category 配置分类
     * @return 是否有效
     */
    public static boolean isValidCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return false;
        }
        
        String[] validCategories = {"SYSTEM", "REWARD", "PROMOTION", "INVITATION", "NOTIFICATION", "SECURITY"};
        for (String validCategory : validCategories) {
            if (validCategory.equals(category.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 验证操作类型
     *
     * @param operationType 操作类型
     * @return 是否有效
     */
    public static boolean isValidOperationType(String operationType) {
        if (operationType == null || operationType.trim().isEmpty()) {
            return false;
        }
        
        String[] validTypes = {"CREATE", "UPDATE", "DELETE", "ENABLE", "DISABLE"};
        for (String validType : validTypes) {
            if (validType.equals(operationType.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 验证审批状态
     *
     * @param approvalStatus 审批状态
     * @return 是否有效
     */
    public static boolean isValidApprovalStatus(String approvalStatus) {
        if (approvalStatus == null || approvalStatus.trim().isEmpty()) {
            return false;
        }
        
        String[] validStatuses = {"PENDING", "APPROVED", "REJECTED", "NOT_REQUIRED"};
        for (String validStatus : validStatuses) {
            if (validStatus.equals(approvalStatus.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}