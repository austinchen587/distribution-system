package com.example.config.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 系统配置DTO - 用于系统配置管理
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
@Schema(description = "系统配置")
public class ConfigDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 配置ID
     */
    @Schema(description = "配置ID", example = "1")
    @NotNull
    private Long id;
    
    /**
     * 配置项键名
     */
    @Schema(description = "配置项键名", example = "system.reward.default_amount")
    @NotBlank
    private String configKey;
    
    /**
     * 配置项值
     */
    @Schema(description = "配置项值", example = "100.00")
    @NotBlank
    private String configValue;
    
    /**
     * 配置项类型
     */
    @Schema(description = "配置项类型", example = "DECIMAL", 
            allowableValues = {"STRING", "INTEGER", "DECIMAL", "BOOLEAN", "JSON", "URL"})
    private String valueType;
    
    /**
     * 配置分类
     */
    @Schema(description = "配置分类", example = "REWARD", 
            allowableValues = {"SYSTEM", "REWARD", "PROMOTION", "INVITATION", "NOTIFICATION", "SECURITY"})
    private String category;
    
    /**
     * 配置名称
     */
    @Schema(description = "配置名称", example = "默认奖励金额")
    private String name;
    
    /**
     * 配置描述
     */
    @Schema(description = "配置描述", example = "系统默认的奖励金额设置")
    private String description;
    
    /**
     * 是否系统内置配置
     */
    @Schema(description = "是否系统内置配置", example = "true")
    private Boolean isSystem;
    
    /**
     * 是否启用
     */
    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;
    
    /**
     * 配置项顺序
     */
    @Schema(description = "配置项顺序", example = "1")
    private Integer sortOrder;
    
    /**
     * 验证规则
     */
    @Schema(description = "验证规则", example = "^\\d+(\\.\\d{1,2})?$")
    private String validationRule;
    
    /**
     * 默认值
     */
    @Schema(description = "默认值", example = "50.00")
    private String defaultValue;
    
    /**
     * 配置创建时间
     */
    @Schema(description = "配置创建时间", example = "2025-08-01 10:00:00")
    private String createdAt;
    
    /**
     * 配置更新时间
     */
    @Schema(description = "配置更新时间", example = "2025-08-04 15:30:00")
    private String updatedAt;
    
    /**
     * 最后修改人ID
     */
    @Schema(description = "最后修改人ID", example = "5")
    private Long updatedBy;
    
    /**
     * 最后修改人姓名
     */
    @Schema(description = "最后修改人姓名", example = "管理员")
    private String updatedByName;
    
    // Constructors
    public ConfigDto() {}
    
    public ConfigDto(Long id, String configKey, String configValue, String valueType) {
        this.id = id;
        this.configKey = configKey;
        this.configValue = configValue;
        this.valueType = valueType;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getConfigKey() {
        return configKey;
    }
    
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }
    
    public String getConfigValue() {
        return configValue;
    }
    
    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }
    
    public String getValueType() {
        return valueType;
    }
    
    public void setValueType(String valueType) {
        this.valueType = valueType;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getIsSystem() {
        return isSystem;
    }
    
    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public String getValidationRule() {
        return validationRule;
    }
    
    public void setValidationRule(String validationRule) {
        this.validationRule = validationRule;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Long getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    public String getUpdatedByName() {
        return updatedByName;
    }
    
    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
    }
    
    /**
     * 检查是否为系统内置配置
     * 
     * @return 是否为系统内置配置
     */
    public boolean isSystemConfig() {
        return Boolean.TRUE.equals(isSystem);
    }
    
    /**
     * 检查配置是否启用
     * 
     * @return 配置是否启用
     */
    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }
    
    /**
     * 获取布尔值
     * 
     * @return 布尔值，如果不是布尔类型返回null
     */
    public Boolean getBooleanValue() {
        if (!"BOOLEAN".equals(valueType)) {
            return null;
        }
        return Boolean.parseBoolean(configValue);
    }
    
    /**
     * 获取整数值
     * 
     * @return 整数值，如果不是整数类型或格式错误返回null
     */
    public Integer getIntegerValue() {
        if (!"INTEGER".equals(valueType)) {
            return null;
        }
        try {
            return Integer.parseInt(configValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * 获取小数值
     * 
     * @return 小数值，如果不是小数类型或格式错误返回null
     */
    public java.math.BigDecimal getDecimalValue() {
        if (!"DECIMAL".equals(valueType)) {
            return null;
        }
        try {
            return new java.math.BigDecimal(configValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * 验证配置值是否符合验证规则
     * 
     * @return 是否符合验证规则
     */
    public boolean isValidValue() {
        if (validationRule == null || validationRule.trim().isEmpty()) {
            return true;
        }
        if (configValue == null) {
            return false;
        }
        return configValue.matches(validationRule);
    }
    
    @Override
    public String toString() {
        return "ConfigDto{" +
                "id=" + id +
                ", configKey='" + configKey + '\'' +
                ", configValue='" + configValue + '\'' +
                ", valueType='" + valueType + '\'' +
                ", category='" + category + '\'' +
                ", name='" + name + '\'' +
                ", isSystem=" + isSystem +
                ", enabled=" + enabled +
                ", updatedAt='" + updatedAt + '\'' +
                ", updatedByName='" + updatedByName + '\'' +
                '}';
    }
}