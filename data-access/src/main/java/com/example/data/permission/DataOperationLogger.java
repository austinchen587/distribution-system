package com.example.data.permission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * 数据操作日志记录服务
 * 将数据操作记录到data_operation_logs表中
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-03
 */
@Service
public class DataOperationLogger {
    
    private static final Logger logger = LoggerFactory.getLogger(DataOperationLogger.class);
    
    @Autowired
    private DataSource dataSource;
    
    /**
     * 记录操作成功日志
     */
    public void logSuccess(String serviceName, String tableName, OperationType operation, 
                          String description, String methodName, long executeTime) {
        logOperation(serviceName, tableName, operation, description, methodName, 
                    "SUCCESS", null, executeTime);
    }
    
    /**
     * 记录权限拒绝日志
     */
    public void logPermissionDenied(String serviceName, String tableName, OperationType operation, 
                                   String description, String methodName, String errorMessage, long executeTime) {
        logOperation(serviceName, tableName, operation, description, methodName, 
                    "PERMISSION_DENIED", errorMessage, executeTime);
    }
    
    /**
     * 记录操作失败日志
     */
    public void logFailure(String serviceName, String tableName, OperationType operation, 
                          String description, String methodName, String errorMessage, long executeTime) {
        logOperation(serviceName, tableName, operation, description, methodName, 
                    "FAILURE", errorMessage, executeTime);
    }
    
    /**
     * 通用日志记录方法
     */
    private void logOperation(String serviceName, String tableName, OperationType operation, 
                             String description, String methodName, String status, 
                             String errorMessage, long executeTime) {
        
        String sql = "INSERT INTO data_operation_logs " +
                    "(service_name, table_name, operation_type, method_name, status, " +
                    "error_message, execute_time_ms, description, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, serviceName);
            stmt.setString(2, tableName);
            stmt.setString(3, operation.getCode());
            stmt.setString(4, methodName);
            stmt.setString(5, status);
            stmt.setString(6, errorMessage);
            stmt.setLong(7, executeTime);
            stmt.setString(8, description);
            stmt.setObject(9, LocalDateTime.now());
            
            stmt.executeUpdate();
            
            logger.debug("Logged operation: service={}, table={}, operation={}, status={}, time={}ms", 
                        serviceName, tableName, operation, status, executeTime);
            
        } catch (SQLException e) {
            // 日志记录失败不应该影响业务流程，只记录错误日志
            logger.error("Failed to log data operation: service={}, table={}, operation={}", 
                        serviceName, tableName, operation, e);
        }
    }
}