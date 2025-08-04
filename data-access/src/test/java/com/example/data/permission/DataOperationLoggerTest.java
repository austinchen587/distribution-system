package com.example.data.permission;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * DataOperationLogger单元测试
 * 
 * @author Data Access Test Generator
 * @version 1.0
 * @since 2025-08-04
 */
@ExtendWith(MockitoExtension.class)
class DataOperationLoggerTest {

    @Mock
    private DataSource dataSource;
    
    @Mock
    private Connection connection;
    
    @Mock
    private PreparedStatement preparedStatement;

    private DataOperationLogger operationLogger;

    @BeforeEach
    void setUp() {
        operationLogger = new DataOperationLogger();
        ReflectionTestUtils.setField(operationLogger, "dataSource", dataSource);
    }

    @Test
    @DisplayName("记录成功操作日志")
    void testLogSuccess() throws SQLException {
        // Given
        String serviceName = "user-service";
        String tableName = "users";
        OperationType operation = OperationType.CREATE;
        String description = "创建新用户";
        String methodName = "insert";
        long executeTime = 150L;
        
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        
        // When
        operationLogger.logSuccess(serviceName, tableName, operation, description, methodName, executeTime);
        
        // Then
        verify(dataSource).getConnection();
        verify(connection).prepareStatement(contains("INSERT INTO data_operation_logs"));
        verify(preparedStatement).executeUpdate();
        verify(connection).close();
        verify(preparedStatement).close();
    }

    @Test
    @DisplayName("记录权限拒绝日志")
    void testLogPermissionDenied() throws SQLException {
        // Given
        String serviceName = "auth-service";
        String tableName = "users";
        OperationType operation = OperationType.DELETE;
        String description = "删除用户";
        String methodName = "deleteById";
        String errorMessage = "Permission denied";
        long executeTime = 50L;
        
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        
        // When
        operationLogger.logPermissionDenied(serviceName, tableName, operation, description, methodName, errorMessage, executeTime);
        
        // Then
        verify(dataSource).getConnection();
        verify(connection).prepareStatement(contains("INSERT INTO data_operation_logs"));
        verify(preparedStatement).executeUpdate();
        verify(connection).close();
        verify(preparedStatement).close();
    }

    @Test
    @DisplayName("记录失败操作日志")
    void testLogFailure() throws SQLException {
        // Given
        String serviceName = "user-service";
        String tableName = "users";
        OperationType operation = OperationType.UPDATE;
        String description = "更新用户";
        String methodName = "update";
        String errorMessage = "Database connection failed";
        long executeTime = 200L;
        
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        
        // When
        operationLogger.logFailure(serviceName, tableName, operation, description, methodName, errorMessage, executeTime);
        
        // Then
        verify(dataSource).getConnection();
        verify(connection).prepareStatement(contains("INSERT INTO data_operation_logs"));
        verify(preparedStatement).executeUpdate();
        verify(connection).close();
        verify(preparedStatement).close();
    }

    @Test
    @DisplayName("数据库连接异常处理")
    void testLogOperation_SQLException_DoesNotThrow() throws SQLException {
        // Given
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection failed"));
        
        // When & Then
        assertDoesNotThrow(() -> {
            operationLogger.logSuccess("test-service", "test_table", OperationType.READ, "test", "testMethod", 100L);
        });
        
        verify(dataSource).getConnection();
    }

    @Test
    @DisplayName("PreparedStatement异常处理")  
    void testLogOperation_PreparedStatementException_DoesNotThrow() throws SQLException {
        // Given
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Execute failed"));
        
        // When & Then
        assertDoesNotThrow(() -> {
            operationLogger.logSuccess("test-service", "test_table", OperationType.READ, "test", "testMethod", 100L);
        });
        
        verify(dataSource).getConnection();
        verify(connection).prepareStatement(anyString());
        verify(preparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("资源关闭异常处理")
    void testLogOperation_CloseException_DoesNotThrow() throws SQLException {
        // Given
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        doThrow(new SQLException("Close failed")).when(connection).close();
        
        // When & Then
        assertDoesNotThrow(() -> {
            operationLogger.logSuccess("test-service", "test_table", OperationType.READ, "test", "testMethod", 100L);
        });
        
        verify(connection).close();
    }
}