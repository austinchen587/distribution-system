package com.example.data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 系统配置实体类
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-04
 */
public class Config {
    
    /**
     * 配置ID
     */
    private Long id;
    
    /**
     * 配置项键名
     */
    @NotBlank(message = "配置项键名不能为空")
    @Size(max = 128, message = "配置项键名长度不能超过128字符")
    private String configKey;
    
    /**
     * 配置项值
     */
    @NotBlank(message = "配置项值不能为空")
    private String configValue;
    
    /**
     * 配置项类型
     */
    private String valueType;
    
    /**
     * 配置分类
     */
    private String category;
    
    /**
     * 配置名称
     */
    private String name;
    
    /**
     * 配置描述
     */
    private String description;
    
    /**
     * 是否系统内置配置
     */
    private Boolean isSystem;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 配置项顺序
     */
    private Integer sortOrder;
    
    /**
     * 验证规则
     */
    private String validationRule;
    
    /**
     * 默认值
     */
    private String defaultValue;
    
    /**
     * 配置创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdAt;
    
    /**
     * 配置更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updatedAt;
    
    /**
     * 最后修改人ID
     */
    private Long updatedBy;
    
    /**
     * 最后修改人姓名
     */
    private String updatedByName;
    
    // Constructors
    public Config() {}
    
    public Config(String configKey, String configValue, String valueType) {
        this.configKey = configKey;
        this.configValue = configValue;
        this.valueType = valueType;
        this.isSystem = false;
        this.enabled = true;
        this.sortOrder = 0;
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return Objects.equals(id, config.id) &&
               Objects.equals(configKey, config.configKey);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, configKey);
    }
    
    @Override
    public String toString() {
        return "Config{" +
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