package com.example.common.enums;

/**
 * 权限级别枚举
 * 
 * <p>定义服务数据访问权限的级别类型，用于控制微服务对数据表的访问范围。
 * 
 * <p>权限级别说明：
 * <ul>
 *   <li>FULL: 完全访问权限，无任何限制条件</li>
 *   <li>RESTRICTED: 受限访问权限，需要满足特定条件</li>
 *   <li>DENIED: 拒绝访问，禁止任何操作</li>
 * </ul>
 * 
 * @author Edom
 * @date 2025-08-01
 * @since 1.0.0
 */
public enum PermissionLevel {
    
    /**
     * 完全权限
     * 允许无限制地执行指定的操作类型
     */
    FULL("FULL", "完全权限"),
    
    /**
     * 受限权限
     * 允许执行操作，但需要满足特定的条件限制
     */
    RESTRICTED("RESTRICTED", "受限权限"),
    
    /**
     * 拒绝访问
     * 禁止执行任何操作
     */
    DENIED("DENIED", "拒绝访问");
    
    private final String code;
    private final String description;
    
    PermissionLevel(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据代码获取权限级别
     * 
     * @param code 权限级别代码
     * @return 对应的权限级别枚举
     * @throws IllegalArgumentException 如果代码无效
     */
    public static PermissionLevel fromCode(String code) {
        for (PermissionLevel level : PermissionLevel.values()) {
            if (level.getCode().equals(code)) {
                return level;
            }
        }
        throw new IllegalArgumentException("未知权限级别代码: " + code);
    }
    
    /**
     * 检查是否允许访问
     * 
     * @return 如果不是DENIED级别，返回true
     */
    public boolean isAccessAllowed() {
        return this != DENIED;
    }
    
    /**
     * 检查是否需要条件限制
     * 
     * @return 如果是RESTRICTED级别，返回true
     */
    public boolean requiresConditions() {
        return this == RESTRICTED;
    }
}
