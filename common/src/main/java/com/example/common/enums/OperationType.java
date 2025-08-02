package com.example.common.enums;

/**
 * 数据操作类型枚举
 * 
 * <p>定义数据库操作的类型，用于权限控制和审计日志记录。
 * 
 * <p>操作类型说明：
 * <ul>
 *   <li>SELECT: 查询操作，读取数据</li>
 *   <li>INSERT: 插入操作，新增数据</li>
 *   <li>UPDATE: 更新操作，修改数据</li>
 *   <li>DELETE: 删除操作，删除数据</li>
 *   <li>ALL: 所有操作，包含上述所有类型</li>
 * </ul>
 * 
 * @author Edom
 * @date 2025-08-01
 * @since 1.0.0
 */
public enum OperationType {
    
    /**
     * 查询操作
     * 对应SQL的SELECT语句
     */
    SELECT("SELECT", "查询操作"),
    
    /**
     * 插入操作
     * 对应SQL的INSERT语句
     */
    INSERT("INSERT", "插入操作"),
    
    /**
     * 更新操作
     * 对应SQL的UPDATE语句
     */
    UPDATE("UPDATE", "更新操作"),
    
    /**
     * 删除操作
     * 对应SQL的DELETE语句
     */
    DELETE("DELETE", "删除操作"),
    
    /**
     * 所有操作
     * 包含SELECT、INSERT、UPDATE、DELETE所有操作类型
     */
    ALL("ALL", "所有操作");
    
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
    
    /**
     * 根据代码获取操作类型
     * 
     * @param code 操作类型代码
     * @return 对应的操作类型枚举
     * @throws IllegalArgumentException 如果代码无效
     */
    public static OperationType fromCode(String code) {
        for (OperationType type : OperationType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知操作类型代码: " + code);
    }
    
    /**
     * 检查是否为读操作
     * 
     * @return 如果是SELECT操作，返回true
     */
    public boolean isReadOperation() {
        return this == SELECT;
    }
    
    /**
     * 检查是否为写操作
     * 
     * @return 如果是INSERT、UPDATE、DELETE操作，返回true
     */
    public boolean isWriteOperation() {
        return this == INSERT || this == UPDATE || this == DELETE;
    }
    
    /**
     * 检查是否包含指定的操作类型
     * 
     * @param operation 要检查的操作类型
     * @return 如果当前类型是ALL或与指定操作类型相同，返回true
     */
    public boolean includes(OperationType operation) {
        return this == ALL || this == operation;
    }
    
    /**
     * 检查是否包含指定的操作类型代码
     * 
     * @param operationCode 要检查的操作类型代码
     * @return 如果当前类型是ALL或与指定操作类型代码相同，返回true
     */
    public boolean includes(String operationCode) {
        return "ALL".equals(this.code) || this.code.equals(operationCode);
    }
}
