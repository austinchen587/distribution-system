package com.example.common.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 数据访问上下文类
 * 
 * <p>封装数据访问操作的上下文信息，用于权限检查和审计日志记录。
 * 该类包含了执行权限验证和日志记录所需的所有关键信息。
 * 
 * <p>主要功能：
 * <ul>
 *   <li>封装数据访问的基本信息（服务、表、操作类型）</li>
 *   <li>提供用户和请求的上下文信息</li>
 *   <li>支持权限检查和审计日志的数据传递</li>
 *   <li>记录操作的执行时间和性能指标</li>
 * </ul>
 * 
 * <p>使用场景：
 * <ul>
 *   <li>AOP拦截器中传递上下文信息</li>
 *   <li>权限检查器的输入参数</li>
 *   <li>审计日志记录的数据源</li>
 *   <li>跨组件的信息传递载体</li>
 * </ul>
 * 
 * @author Edom
 * @date 2025-08-01
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataAccessContext {
    
    /**
     * 请求唯一标识
     * 用于关联同一次请求中的多个数据操作
     */
    private String requestId;
    
    /**
     * 微服务名称
     * 执行数据操作的微服务标识
     */
    private String serviceName;
    
    /**
     * 数据表名称
     * 被访问的数据表名
     */
    private String tableName;
    
    /**
     * 操作类型
     * 数据操作的类型：SELECT, INSERT, UPDATE, DELETE
     */
    private String operationType;
    
    /**
     * 操作用户ID
     * 执行该操作的用户标识，可能为空（系统操作）
     */
    private Long userId;
    
    /**
     * 客户端IP地址
     * 发起操作的客户端IP地址
     */
    private String ipAddress;
    
    /**
     * 用户代理信息
     * 客户端的User-Agent信息
     */
    private String userAgent;
    
    /**
     * 方法名称
     * 被拦截的Mapper方法名
     */
    private String methodName;
    
    /**
     * 方法参数
     * 被拦截方法的参数信息（JSON格式）
     */
    private String methodArgs;
    
    /**
     * 操作开始时间
     * 用于计算操作执行耗时
     */
    private LocalDateTime startTime;
    
    /**
     * 操作结束时间
     * 用于计算操作执行耗时
     */
    private LocalDateTime endTime;
    
    /**
     * 执行时间（毫秒）
     * 数据操作的实际执行耗时
     */
    private Integer executionTime;
    
    /**
     * 影响的行数
     * 对于INSERT/UPDATE/DELETE操作，记录实际影响的数据行数
     */
    private Integer affectedRows;
    
    /**
     * 操作结果
     * 操作执行的结果对象
     */
    private Object result;
    
    /**
     * 异常信息
     * 如果操作失败，记录异常信息
     */
    private Exception exception;
    
    /**
     * 获取权限检查的缓存键
     * 
     * @return 格式为 "perm:serviceName:tableName:operationType" 的字符串
     */
    public String getPermissionCacheKey() {
        return String.format("perm:%s:%s:%s", serviceName, tableName, operationType);
    }
    
    /**
     * 获取操作的唯一标识
     * 
     * @return 格式为 "serviceName.operationType.tableName" 的字符串
     */
    public String getOperationKey() {
        return String.format("%s.%s.%s", serviceName, operationType, tableName);
    }
    
    /**
     * 检查是否为查询操作
     * 
     * @return 如果操作类型为SELECT，返回true
     */
    public boolean isSelectOperation() {
        return "SELECT".equals(operationType);
    }
    
    /**
     * 检查是否为修改操作
     * 
     * @return 如果操作类型为INSERT/UPDATE/DELETE，返回true
     */
    public boolean isModifyOperation() {
        return "INSERT".equals(operationType) || 
               "UPDATE".equals(operationType) || 
               "DELETE".equals(operationType);
    }
    
    /**
     * 检查是否有用户上下文
     * 
     * @return 如果userId不为空，返回true
     */
    public boolean hasUserContext() {
        return userId != null;
    }
    
    /**
     * 检查是否有异常
     * 
     * @return 如果exception不为空，返回true
     */
    public boolean hasException() {
        return exception != null;
    }
    
    /**
     * 计算执行时间
     * 
     * @return 如果开始和结束时间都不为空，返回执行耗时（毫秒）
     */
    public long calculateExecutionTime() {
        if (startTime != null && endTime != null) {
            return java.time.Duration.between(startTime, endTime).toMillis();
        }
        return 0;
    }
    
    /**
     * 设置操作完成时间并计算执行耗时
     */
    public void markCompleted() {
        this.endTime = LocalDateTime.now();
        this.executionTime = (int) calculateExecutionTime();
    }
    
    /**
     * 设置操作异常并标记完成
     * 
     * @param ex 操作过程中发生的异常
     */
    public void markFailed(Exception ex) {
        this.exception = ex;
        markCompleted();
    }
    
    /**
     * 获取操作的简要描述
     *
     * @return 包含服务、操作、表名的描述字符串
     */
    public String getOperationSummary() {
        return String.format("Service[%s] %s on table[%s]", serviceName, operationType, tableName);
    }

    // Getter methods (in case Lombok @Data doesn't work)
    public String getRequestId() { return requestId; }
    public String getServiceName() { return serviceName; }
    public String getTableName() { return tableName; }
    public String getOperationType() { return operationType; }
    public Long getUserId() { return userId; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public String getMethodName() { return methodName; }
    public String getMethodArgs() { return methodArgs; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public Integer getExecutionTime() { return executionTime; }
    public Integer getAffectedRows() { return affectedRows; }
    public Object getResult() { return result; }
    public Exception getException() { return exception; }

    // Setter methods
    public void setExecutionTime(Integer executionTime) { this.executionTime = executionTime; }
    public void setAffectedRows(Integer affectedRows) { this.affectedRows = affectedRows; }
    public void setResult(Object result) { this.result = result; }

    // Builder pattern (in case Lombok doesn't work)
    public static DataAccessContextBuilder builder() {
        return new DataAccessContextBuilder();
    }

    public static class DataAccessContextBuilder {
        private DataAccessContext context = new DataAccessContext();

        public DataAccessContextBuilder requestId(String requestId) { context.requestId = requestId; return this; }
        public DataAccessContextBuilder serviceName(String serviceName) { context.serviceName = serviceName; return this; }
        public DataAccessContextBuilder tableName(String tableName) { context.tableName = tableName; return this; }
        public DataAccessContextBuilder operationType(String operationType) { context.operationType = operationType; return this; }
        public DataAccessContextBuilder userId(Long userId) { context.userId = userId; return this; }
        public DataAccessContextBuilder executionTime(Integer executionTime) { context.executionTime = executionTime; return this; }
        public DataAccessContextBuilder ipAddress(String ipAddress) { context.ipAddress = ipAddress; return this; }
        public DataAccessContextBuilder userAgent(String userAgent) { context.userAgent = userAgent; return this; }
        public DataAccessContextBuilder methodName(String methodName) { context.methodName = methodName; return this; }
        public DataAccessContextBuilder startTime(LocalDateTime startTime) { context.startTime = startTime; return this; }

        public DataAccessContext build() { return context; }
    }
}
