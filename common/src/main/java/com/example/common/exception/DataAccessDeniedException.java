package com.example.common.exception;

import com.example.common.dto.DataAccessContext;

/**
 * 数据访问权限拒绝异常
 * 
 * <p>当微服务尝试访问没有权限的数据表或执行未授权的操作时抛出此异常。
 * 该异常包含了详细的上下文信息，便于问题排查和审计记录。
 * 
 * <p>异常场景：
 * <ul>
 *   <li>服务访问未授权的数据表</li>
 *   <li>服务执行未授权的操作类型</li>
 *   <li>权限配置被禁用或不存在</li>
 *   <li>权限级别为DENIED</li>
 * </ul>
 * 
 * <p>异常处理：
 * <ul>
 *   <li>记录详细的审计日志</li>
 *   <li>返回统一的错误响应</li>
 *   <li>触发安全监控告警</li>
 * </ul>
 * 
 * @author Edom
 * @date 2025-08-01
 * @since 1.0.0
 */
public class DataAccessDeniedException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private final String serviceName;
    private final String tableName;
    private final String operationType;
    private final String errorCode;
    
    /**
     * 构造函数
     * 
     * @param serviceName 微服务名称
     * @param tableName 数据表名称
     * @param operationType 操作类型
     */
    public DataAccessDeniedException(String serviceName, String tableName, String operationType) {
        super(String.format("服务 [%s] 无权限对表 [%s] 执行 [%s] 操作", serviceName, tableName, operationType));
        this.serviceName = serviceName;
        this.tableName = tableName;
        this.operationType = operationType;
        this.errorCode = "DATA_ACCESS_DENIED";
    }
    
    /**
     * 构造函数（带自定义消息）
     * 
     * @param serviceName 微服务名称
     * @param tableName 数据表名称
     * @param operationType 操作类型
     * @param message 自定义错误消息
     */
    public DataAccessDeniedException(String serviceName, String tableName, String operationType, String message) {
        super(message);
        this.serviceName = serviceName;
        this.tableName = tableName;
        this.operationType = operationType;
        this.errorCode = "DATA_ACCESS_DENIED";
    }
    
    /**
     * 构造函数（基于数据访问上下文）
     * 
     * @param context 数据访问上下文
     */
    public DataAccessDeniedException(DataAccessContext context) {
        this(context.getServiceName(), context.getTableName(), context.getOperationType());
    }
    
    /**
     * 构造函数（基于数据访问上下文和自定义消息）
     * 
     * @param context 数据访问上下文
     * @param message 自定义错误消息
     */
    public DataAccessDeniedException(DataAccessContext context, String message) {
        this(context.getServiceName(), context.getTableName(), context.getOperationType(), message);
    }
    
    /**
     * 构造函数（带原因异常）
     * 
     * @param serviceName 微服务名称
     * @param tableName 数据表名称
     * @param operationType 操作类型
     * @param cause 原因异常
     */
    public DataAccessDeniedException(String serviceName, String tableName, String operationType, Throwable cause) {
        super(String.format("服务 [%s] 无权限对表 [%s] 执行 [%s] 操作", serviceName, tableName, operationType), cause);
        this.serviceName = serviceName;
        this.tableName = tableName;
        this.operationType = operationType;
        this.errorCode = "DATA_ACCESS_DENIED";
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public String getOperationType() {
        return operationType;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * 获取操作标识
     * 
     * @return 格式为 "serviceName.operationType.tableName" 的字符串
     */
    public String getOperationKey() {
        return String.format("%s.%s.%s", serviceName, operationType, tableName);
    }
    
    /**
     * 获取详细的错误信息
     * 
     * @return 包含所有上下文信息的错误描述
     */
    public String getDetailedMessage() {
        return String.format("数据访问权限拒绝 - 服务: %s, 表: %s, 操作: %s, 错误码: %s, 消息: %s",
                serviceName, tableName, operationType, errorCode, getMessage());
    }
}
