package com.example.data.permission;

/**
 * 数据操作类型枚举
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-03
 */
public enum OperationType {
    
    /**
     * 读取操作
     */
    READ("READ", "读取操作"),
    
    /**
     * 创建操作
     */
    CREATE("CREATE", "创建操作"),
    
    /**
     * 更新操作
     */
    UPDATE("UPDATE", "更新操作"),
    
    /**
     * 删除操作
     */
    DELETE("DELETE", "删除操作"),
    
    /**
     * 批量操作
     */
    BATCH("BATCH", "批量操作"),
    
    /**
     * 统计操作
     */
    STATS("STATS", "统计操作");
    
    private final String code;
    private final String description;
    
    OperationType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static OperationType fromCode(String code) {
        for (OperationType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown operation type code: " + code);
    }
}