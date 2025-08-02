package com.example.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 服务数据访问权限配置实体类
 * 
 * <p>对应数据库表 service_data_permissions，用于管理微服务对数据表的访问权限配置。
 * 该实体定义了每个微服务可以对哪些数据表执行哪些操作，以及相应的权限级别。
 * 
 * <p>主要功能：
 * <ul>
 *   <li>定义服务对数据表的访问权限（SELECT/INSERT/UPDATE/DELETE/ALL）</li>
 *   <li>设置权限级别（FULL/RESTRICTED/DENIED）</li>
 *   <li>支持基于条件的行级权限控制</li>
 *   <li>提供权限配置的启用/禁用控制</li>
 * </ul>
 * 
 * <p>使用场景：
 * <ul>
 *   <li>微服务数据访问权限控制</li>
 *   <li>数据安全审计和合规检查</li>
 *   <li>权限配置管理和动态调整</li>
 *   <li>服务间数据访问边界定义</li>
 * </ul>
 * 
 * <p>权限级别说明：
 * <ul>
 *   <li>FULL: 完全访问权限，无任何限制</li>
 *   <li>RESTRICTED: 受限访问权限，基于conditions字段的条件限制</li>
 *   <li>DENIED: 拒绝访问，禁止任何操作</li>
 * </ul>
 * 
 * @author Edom
 * @date 2025-08-01
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ServiceDataPermission {
    
    /**
     * 权限配置ID
     * 对应数据库字段：BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY
     */
    private Long id;
    
    /**
     * 微服务名称
     * 例如：auth-service, lead-service, deal-service
     * 对应数据库字段：VARCHAR(50) NOT NULL
     */
    private String serviceName;
    
    /**
     * 数据表名称
     * 例如：users, customer_leads, deals
     */
    private String tableName;
    
    /**
     * 操作类型
     * 可选值：SELECT, INSERT, UPDATE, DELETE, ALL
     */
    private String operationType;
    
    /**
     * 权限级别
     * 可选值：FULL, RESTRICTED, DENIED
     * FULL: 完全权限
     * RESTRICTED: 受限权限（基于conditions条件）
     * DENIED: 拒绝访问
     */
    private String permissionLevel;
    
    /**
     * 访问条件（JSON格式）
     * 用于行级权限控制，定义数据访问的具体条件
     * 例如：{"user_id": "${current_user_id}", "status": "active"}
     * 对应数据库字段：JSON DEFAULT NULL
     */
    private String conditions;
    
    /**
     * 是否启用
     * true: 启用该权限配置
     * false: 禁用该权限配置
     */
    private Boolean isEnabled;
    
    /**
     * 权限描述
     * 对该权限配置的详细说明
     * 对应数据库字段：VARCHAR(255) DEFAULT NULL
     */
    private String description;
    
    /**
     * 创建人ID
     * 创建该权限配置的用户ID
     * 对应数据库字段：BIGINT UNSIGNED NOT NULL，外键关联users表
     */
    private Long createdBy;

    /**
     * 更新人ID
     * 最后更新该权限配置的用户ID
     * 对应数据库字段：BIGINT UNSIGNED DEFAULT NULL，外键关联users表
     */
    private Long updatedBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 检查是否为完全权限
     * 
     * @return 如果权限级别为FULL且已启用，返回true
     */
    public boolean isFullPermission() {
        return "FULL".equals(permissionLevel) && Boolean.TRUE.equals(isEnabled);
    }
    
    /**
     * 检查是否为受限权限
     * 
     * @return 如果权限级别为RESTRICTED且已启用，返回true
     */
    public boolean isRestrictedPermission() {
        return "RESTRICTED".equals(permissionLevel) && Boolean.TRUE.equals(isEnabled);
    }
    
    /**
     * 检查是否被拒绝访问
     * 
     * @return 如果权限级别为DENIED或未启用，返回true
     */
    public boolean isDenied() {
        return "DENIED".equals(permissionLevel) || !Boolean.TRUE.equals(isEnabled);
    }
    
    /**
     * 检查是否支持指定的操作类型
     * 
     * @param operation 要检查的操作类型
     * @return 如果支持该操作类型，返回true
     */
    public boolean supportsOperation(String operation) {
        return "ALL".equals(operationType) || operationType.equals(operation);
    }
    
    /**
     * 获取权限配置的唯一标识
     *
     * @return 格式为 "serviceName:tableName:operationType" 的字符串
     */
    public String getPermissionKey() {
        return String.format("%s:%s:%s", serviceName, tableName, operationType);
    }

    // Getter methods (in case Lombok @Data doesn't work)
    public Long getId() { return id; }
    public String getServiceName() { return serviceName; }
    public String getTableName() { return tableName; }
    public String getOperationType() { return operationType; }
    public String getPermissionLevel() { return permissionLevel; }
    public String getConditions() { return conditions; }
    public Boolean getIsEnabled() { return isEnabled; }
    public String getDescription() { return description; }
    public Long getCreatedBy() { return createdBy; }
    public Long getUpdatedBy() { return updatedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
