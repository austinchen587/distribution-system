package com.example.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 数据操作审计日志实体类
 * 
 * <p>对应数据库表 data_operation_logs，用于记录所有微服务的数据操作审计信息。
 * 该实体记录了每次数据访问的详细信息，包括操作类型、执行结果、性能指标等。
 * 
 * <p>主要功能：
 * <ul>
 *   <li>记录数据操作的完整审计信息</li>
 *   <li>跟踪操作的执行状态和性能指标</li>
 *   <li>支持数据变更前后的对比记录</li>
 *   <li>提供安全审计和合规检查依据</li>
 * </ul>
 * 
 * <p>使用场景：
 * <ul>
 *   <li>数据访问安全审计</li>
 *   <li>系统性能监控和分析</li>
 *   <li>数据变更历史追踪</li>
 *   <li>合规性检查和报告</li>
 * </ul>
 * 
 * <p>操作状态说明：
 * <ul>
 *   <li>SUCCESS: 操作成功执行</li>
 *   <li>FAILED: 操作执行失败</li>
 *   <li>DENIED: 操作被权限控制拒绝</li>
 * </ul>
 * 
 * @author Edom
 * @date 2025-08-01
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataOperationLog {
    
    /**
     * 日志ID
     * 对应数据库字段：BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY
     */
    private Long id;
    
    /**
     * 请求唯一标识
     * 用于关联同一次请求中的多个数据操作
     */
    private String requestId;
    
    /**
     * 微服务名称
     * 执行数据操作的微服务标识
     * 对应数据库字段：VARCHAR(50) NOT NULL
     */
    private String serviceName;
    
    /**
     * 操作的数据表名
     */
    private String tableName;
    
    /**
     * 操作类型
     * 可选值：SELECT, INSERT, UPDATE, DELETE
     */
    private String operationType;
    
    /**
     * 操作用户ID
     * 执行该操作的用户标识，可能为空（系统操作）
     * 对应数据库字段：BIGINT UNSIGNED DEFAULT NULL，外键关联users表
     */
    private Long userId;
    
    /**
     * 影响的行数
     * 对于INSERT/UPDATE/DELETE操作，记录实际影响的数据行数
     * 对应数据库字段：INT UNSIGNED DEFAULT NULL
     */
    private Integer affectedRows;

    /**
     * 执行时间（毫秒）
     * 数据操作的实际执行耗时
     * 对应数据库字段：INT UNSIGNED DEFAULT NULL
     */
    private Integer executionTime;
    
    /**
     * 操作状态
     * 可选值：SUCCESS, FAILED, DENIED
     * 对应数据库字段：ENUM('SUCCESS', 'FAILED', 'DENIED') NOT NULL
     */
    private String status;
    
    /**
     * 错误信息
     * 当操作失败时，记录具体的错误信息
     * 对应数据库字段：TEXT DEFAULT NULL
     */
    private String errorMessage;

    /**
     * SQL语句（脱敏后）
     * 记录执行的SQL语句，敏感参数已脱敏处理
     * 对应数据库字段：TEXT DEFAULT NULL
     */
    private String sqlStatement;
    
    /**
     * 操作前数据（JSON格式）
     * 对于UPDATE/DELETE操作，记录变更前的数据状态
     * 对应数据库字段：JSON DEFAULT NULL
     */
    private String beforeData;

    /**
     * 操作后数据（JSON格式）
     * 对于INSERT/UPDATE操作，记录变更后的数据状态
     * 对应数据库字段：JSON DEFAULT NULL
     */
    private String afterData;
    
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
     * 操作时间
     * 数据操作发生的时间戳
     */
    private LocalDateTime createdAt;
    
    /**
     * 检查操作是否成功
     * 
     * @return 如果操作状态为SUCCESS，返回true
     */
    public boolean isSuccess() {
        return "SUCCESS".equals(status);
    }
    
    /**
     * 检查操作是否失败
     * 
     * @return 如果操作状态为FAILED，返回true
     */
    public boolean isFailed() {
        return "FAILED".equals(status);
    }
    
    /**
     * 检查操作是否被拒绝
     * 
     * @return 如果操作状态为DENIED，返回true
     */
    public boolean isDenied() {
        return "DENIED".equals(status);
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
     * 检查是否为慢查询
     * 
     * @param threshold 慢查询阈值（毫秒）
     * @return 如果执行时间超过阈值，返回true
     */
    public boolean isSlowQuery(int threshold) {
        return executionTime != null && executionTime > threshold;
    }
    
    /**
     * 获取操作的简要描述
     * 
     * @return 格式为 "serviceName.operationType.tableName" 的字符串
     */
    public String getOperationSummary() {
        return String.format("%s.%s.%s", serviceName, operationType, tableName);
    }
    
    /**
     * 获取操作的详细描述
     *
     * @return 包含状态、耗时、影响行数的详细描述
     */
    public String getOperationDetail() {
        StringBuilder detail = new StringBuilder();
        detail.append(getOperationSummary());
        detail.append(" - ").append(status);

        if (executionTime != null) {
            detail.append(" (").append(executionTime).append("ms)");
        }

        if (affectedRows != null && affectedRows > 0) {
            detail.append(" [").append(affectedRows).append(" rows]");
        }

        return detail.toString();
    }

    // Builder pattern and setters (in case Lombok doesn't work)
    public static DataOperationLogBuilder builder() {
        return new DataOperationLogBuilder();
    }

    public void setStatus(String status) { this.status = status; }
    public void setSqlStatement(String sqlStatement) { this.sqlStatement = sqlStatement; }
    public void setAffectedRows(Integer affectedRows) { this.affectedRows = affectedRows; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public void setBeforeData(String beforeData) { this.beforeData = beforeData; }
    public void setAfterData(String afterData) { this.afterData = afterData; }

    // Getter methods (in case Lombok @Data doesn't work)
    public Long getId() { return id; }
    public String getRequestId() { return requestId; }
    public String getServiceName() { return serviceName; }
    public String getTableName() { return tableName; }
    public String getOperationType() { return operationType; }
    public Long getUserId() { return userId; }
    public Integer getAffectedRows() { return affectedRows; }
    public Integer getExecutionTime() { return executionTime; }
    public String getStatus() { return status; }
    public String getErrorMessage() { return errorMessage; }
    public String getSqlStatement() { return sqlStatement; }
    public String getBeforeData() { return beforeData; }
    public String getAfterData() { return afterData; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static class DataOperationLogBuilder {
        private DataOperationLog log = new DataOperationLog();

        public DataOperationLogBuilder requestId(String requestId) { log.requestId = requestId; return this; }
        public DataOperationLogBuilder serviceName(String serviceName) { log.serviceName = serviceName; return this; }
        public DataOperationLogBuilder tableName(String tableName) { log.tableName = tableName; return this; }
        public DataOperationLogBuilder operationType(String operationType) { log.operationType = operationType; return this; }
        public DataOperationLogBuilder userId(Long userId) { log.userId = userId; return this; }
        public DataOperationLogBuilder executionTime(Integer executionTime) { log.executionTime = executionTime; return this; }
        public DataOperationLogBuilder ipAddress(String ipAddress) { log.ipAddress = ipAddress; return this; }
        public DataOperationLogBuilder userAgent(String userAgent) { log.userAgent = userAgent; return this; }
        public DataOperationLogBuilder createdAt(LocalDateTime createdAt) { log.createdAt = createdAt; return this; }

        public DataOperationLog build() { return log; }
    }
}
