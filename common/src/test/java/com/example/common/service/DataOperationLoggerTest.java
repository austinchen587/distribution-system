package com.example.common.service;

import com.example.common.dto.DataAccessContext;
import com.example.common.entity.DataOperationLog;
import com.example.common.enums.OperationStatus;
import com.example.common.mapper.DataOperationLogMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 数据操作日志记录器单元测试
 * 
 * <p>测试DataOperationLogger的核心功能，包括日志记录、数据脱敏等。
 * 
 * @author Edom
 * @date 2025-08-01
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class DataOperationLoggerTest {
    
    @Mock
    private DataOperationLogMapper logMapper;
    
    @Mock
    private ObjectMapper objectMapper;
    
    @InjectMocks
    private DataOperationLogger logger;
    
    private DataAccessContext context;
    
    @BeforeEach
    void setUp() {
        context = DataAccessContext.builder()
            .requestId("test-request-123")
            .serviceName("auth-service")
            .tableName("users")
            .operationType("SELECT")
            .userId(1L)
            .executionTime(50)
            .ipAddress("192.168.1.100")
            .userAgent("Mozilla/5.0")
            .startTime(LocalDateTime.now())
            .build();
    }
    
    @Test
    @DisplayName("正常场景：记录操作成功日志")
    void testLogSuccess() {
        // Given
        Integer result = 1;
        
        // When
        logger.logSuccess(context, result);
        
        // Then
        ArgumentCaptor<DataOperationLog> logCaptor = ArgumentCaptor.forClass(DataOperationLog.class);
        verify(logMapper).insert(logCaptor.capture());
        
        DataOperationLog capturedLog = logCaptor.getValue();
        assertEquals("test-request-123", capturedLog.getRequestId());
        assertEquals("auth-service", capturedLog.getServiceName());
        assertEquals("users", capturedLog.getTableName());
        assertEquals("SELECT", capturedLog.getOperationType());
        assertEquals(1L, capturedLog.getUserId());
        assertEquals(50, capturedLog.getExecutionTime());
        assertEquals("192.168.1.100", capturedLog.getIpAddress());
        assertEquals("Mozilla/5.0", capturedLog.getUserAgent());
        assertEquals(OperationStatus.SUCCESS.getCode(), capturedLog.getStatus());
        assertEquals(1, capturedLog.getAffectedRows());
        assertNotNull(capturedLog.getCreatedAt());
    }
    
    @Test
    @DisplayName("异常场景：记录操作失败日志")
    void testLogFailure() {
        // Given
        Exception exception = new RuntimeException("数据库连接失败");
        
        // When
        logger.logFailure(context, exception);
        
        // Then
        ArgumentCaptor<DataOperationLog> logCaptor = ArgumentCaptor.forClass(DataOperationLog.class);
        verify(logMapper).insert(logCaptor.capture());
        
        DataOperationLog capturedLog = logCaptor.getValue();
        assertEquals(OperationStatus.FAILED.getCode(), capturedLog.getStatus());
        assertEquals("数据库连接失败", capturedLog.getErrorMessage());
        assertNull(capturedLog.getAffectedRows());
    }
    
    @Test
    @DisplayName("权限场景：记录操作被拒绝日志")
    void testLogDenied() {
        // Given
        String reason = "服务无权限访问该表";
        
        // When
        logger.logDenied(context, reason);
        
        // Then
        ArgumentCaptor<DataOperationLog> logCaptor = ArgumentCaptor.forClass(DataOperationLog.class);
        verify(logMapper).insert(logCaptor.capture());
        
        DataOperationLog capturedLog = logCaptor.getValue();
        assertEquals(OperationStatus.DENIED.getCode(), capturedLog.getStatus());
        assertEquals("服务无权限访问该表", capturedLog.getErrorMessage());
    }
    
    @Test
    @DisplayName("数据变化场景：记录带数据变化的操作日志")
    void testLogWithDataChange() throws Exception {
        // Given
        Object beforeData = new TestUser(1L, "张三", "old@example.com");
        Object afterData = new TestUser(1L, "张三", "new@example.com");
        Integer result = 1;
        
        when(objectMapper.writeValueAsString(beforeData))
            .thenReturn("{\"id\":1,\"name\":\"张三\",\"email\":\"old@example.com\"}");
        when(objectMapper.writeValueAsString(afterData))
            .thenReturn("{\"id\":1,\"name\":\"张三\",\"email\":\"new@example.com\"}");
        
        // When
        logger.logWithDataChange(context, beforeData, afterData, result);
        
        // Then
        ArgumentCaptor<DataOperationLog> logCaptor = ArgumentCaptor.forClass(DataOperationLog.class);
        verify(logMapper).insert(logCaptor.capture());
        
        DataOperationLog capturedLog = logCaptor.getValue();
        assertEquals(OperationStatus.SUCCESS.getCode(), capturedLog.getStatus());
        assertEquals(1, capturedLog.getAffectedRows());
        assertNotNull(capturedLog.getBeforeData());
        assertNotNull(capturedLog.getAfterData());
        assertTrue(capturedLog.getBeforeData().contains("old@example.com"));
        assertTrue(capturedLog.getAfterData().contains("new@example.com"));
    }
    
    @Test
    @DisplayName("SQL场景：记录SQL执行日志")
    void testLogSqlExecution() {
        // Given
        String sql = "SELECT * FROM users WHERE id = 1";
        Integer result = 1;

        // When
        logger.logSqlExecution(context, sql, result);

        // Then
        ArgumentCaptor<DataOperationLog> logCaptor = ArgumentCaptor.forClass(DataOperationLog.class);
        verify(logMapper).insert(logCaptor.capture());

        DataOperationLog capturedLog = logCaptor.getValue();
        assertEquals(OperationStatus.SUCCESS.getCode(), capturedLog.getStatus());
        assertEquals(1, capturedLog.getAffectedRows());
        assertNotNull(capturedLog.getSqlStatement());
        // SQL应该被记录
        assertTrue(capturedLog.getSqlStatement().contains("SELECT * FROM users WHERE id = 1"));
    }
    
    @Test
    @DisplayName("数据记录测试：完整记录数据内容")
    void testDataRecording() throws Exception {
        // Given
        Object userData = new TestUser(1L, "张三", "test@example.com", "password123", "18888888888");

        when(objectMapper.writeValueAsString(userData))
            .thenReturn("{\"id\":1,\"name\":\"张三\",\"email\":\"test@example.com\",\"password\":\"password123\",\"phone\":\"18888888888\"}");

        // When
        logger.logWithDataChange(context, null, userData, 1);

        // Then
        ArgumentCaptor<DataOperationLog> logCaptor = ArgumentCaptor.forClass(DataOperationLog.class);
        verify(logMapper).insert(logCaptor.capture());

        DataOperationLog capturedLog = logCaptor.getValue();
        String afterData = capturedLog.getAfterData();

        // 验证数据被完整记录
        assertTrue(afterData.contains("\"password\":\"password123\""));
        assertTrue(afterData.contains("\"phone\":\"18888888888\""));
        assertTrue(afterData.contains("张三"));
        assertTrue(afterData.contains("test@example.com"));
    }
    
    @Test
    @DisplayName("异常处理：数据库插入失败不应影响业务")
    void testLogInsertFailure() {
        // Given
        doThrow(new RuntimeException("数据库插入失败")).when(logMapper).insert(any());
        
        // When & Then - 不应该抛出异常
        assertDoesNotThrow(() -> {
            logger.logSuccess(context, 1);
        });
        
        verify(logMapper).insert(any());
    }
    
    @Test
    @DisplayName("边界测试：长文本应该被截断")
    void testLongTextTruncation() {
        // Given
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longMessage.append("这是一个很长的错误消息");
        }
        Exception exception = new RuntimeException(longMessage.toString());
        
        // When
        logger.logFailure(context, exception);
        
        // Then
        ArgumentCaptor<DataOperationLog> logCaptor = ArgumentCaptor.forClass(DataOperationLog.class);
        verify(logMapper).insert(logCaptor.capture());
        
        DataOperationLog capturedLog = logCaptor.getValue();
        assertTrue(capturedLog.getErrorMessage().length() <= 500);
        assertTrue(capturedLog.getErrorMessage().endsWith("..."));
    }
    
    // 测试用的用户类
    private static class TestUser {
        private Long id;
        private String name;
        private String email;
        private String password;
        private String phone;

        public TestUser(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public TestUser(Long id, String name, String email, String password, String phone) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.password = password;
            this.phone = phone;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getPhone() { return phone; }
    }
}
