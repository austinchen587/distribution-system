package com.example.common.aspect;

import com.example.common.dto.DataAccessContext;
import com.example.common.exception.DataAccessDeniedException;
import com.example.common.service.DataOperationLogger;
import com.example.common.service.ServicePermissionChecker;
import com.example.common.util.DataAccessContextExtractor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 数据访问拦截器单元测试
 * 
 * <p>测试DataAccessInterceptor的核心功能，包括权限检查、日志记录等。
 * 
 * @author Edom
 * @date 2025-08-01
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class DataAccessInterceptorTest {
    
    @Mock
    private ServicePermissionChecker permissionChecker;
    
    @Mock
    private DataOperationLogger operationLogger;
    
    @Mock
    private DataAccessContextExtractor contextExtractor;
    
    @Mock
    private ProceedingJoinPoint joinPoint;
    
    @InjectMocks
    private DataAccessInterceptor interceptor;
    
    private DataAccessContext context;
    
    @BeforeEach
    void setUp() {
        context = DataAccessContext.builder()
            .requestId("test-request-123")
            .serviceName("auth-service")
            .tableName("users")
            .operationType("SELECT")
            .userId(1L)
            .startTime(LocalDateTime.now())
            .build();
    }
    
    @Test
    @DisplayName("正常场景：有权限的数据访问")
    void testInterceptDataAccess_Success() throws Throwable {
        // Given
        Object expectedResult = "query result";
        
        when(contextExtractor.extractContext(joinPoint)).thenReturn(context);
        when(permissionChecker.hasPermission(context)).thenReturn(true);
        when(joinPoint.proceed()).thenReturn(expectedResult);
        
        // When
        Object result = interceptor.interceptDataAccess(joinPoint);
        
        // Then
        assertEquals(expectedResult, result);
        
        verify(contextExtractor).extractContext(joinPoint);
        verify(permissionChecker).hasPermission(context);
        verify(joinPoint).proceed();
        verify(operationLogger).logSuccess(eq(context), eq(expectedResult));
        verify(operationLogger, never()).logDenied(any(), any());
    }
    
    @Test
    @DisplayName("权限拒绝场景：无权限的数据访问")
    void testInterceptDataAccess_PermissionDenied() throws Throwable {
        // Given
        when(contextExtractor.extractContext(joinPoint)).thenReturn(context);
        when(permissionChecker.hasPermission(context)).thenReturn(false);
        
        // When & Then
        DataAccessDeniedException exception = assertThrows(
            DataAccessDeniedException.class,
            () -> interceptor.interceptDataAccess(joinPoint)
        );
        
        assertNotNull(exception);
        assertEquals("auth-service", exception.getServiceName());
        assertEquals("users", exception.getTableName());
        assertEquals("SELECT", exception.getOperationType());
        
        verify(contextExtractor).extractContext(joinPoint);
        verify(permissionChecker).hasPermission(context);
        verify(joinPoint, never()).proceed();
        verify(operationLogger).logDenied(eq(context), anyString());
        verify(operationLogger, never()).logSuccess(any(), any());
    }
    
    @Test
    @DisplayName("异常场景：方法执行失败")
    void testInterceptDataAccess_ExecutionFailure() throws Throwable {
        // Given
        RuntimeException executionException = new RuntimeException("数据库连接失败");
        
        when(contextExtractor.extractContext(joinPoint)).thenReturn(context);
        when(permissionChecker.hasPermission(context)).thenReturn(true);
        when(joinPoint.proceed()).thenThrow(executionException);
        
        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> interceptor.interceptDataAccess(joinPoint)
        );
        
        assertEquals("数据库连接失败", exception.getMessage());
        
        verify(contextExtractor).extractContext(joinPoint);
        verify(permissionChecker).hasPermission(context);
        verify(joinPoint).proceed();
        verify(operationLogger).logFailure(eq(context), eq(executionException));
        verify(operationLogger, never()).logSuccess(any(), any());
    }
    
    @Test
    @DisplayName("查询方法拦截：正常执行")
    void testInterceptQueryMethods_Success() throws Throwable {
        // Given
        Object expectedResult = "query result";
        
        when(contextExtractor.extractContext(joinPoint)).thenReturn(context);
        when(joinPoint.proceed()).thenReturn(expectedResult);
        
        // When
        Object result = interceptor.interceptQueryMethods(joinPoint);
        
        // Then
        assertEquals(expectedResult, result);
        verify(contextExtractor).extractContext(joinPoint);
        verify(joinPoint).proceed();
    }
    
    @Test
    @DisplayName("查询方法拦截：慢查询检测")
    void testInterceptQueryMethods_SlowQuery() throws Throwable {
        // Given
        Object expectedResult = "query result";
        
        when(contextExtractor.extractContext(joinPoint)).thenReturn(context);
        when(joinPoint.proceed()).thenAnswer(invocation -> {
            // 模拟慢查询，延迟1.5秒
            Thread.sleep(1500);
            return expectedResult;
        });
        
        // When
        Object result = interceptor.interceptQueryMethods(joinPoint);
        
        // Then
        assertEquals(expectedResult, result);
        assertTrue(context.getExecutionTime() > 1000); // 应该检测到慢查询
    }
    
    @Test
    @DisplayName("修改方法拦截：正常执行")
    void testInterceptModifyMethods_Success() throws Throwable {
        // Given
        Integer affectedRows = 1;
        context = DataAccessContext.builder()
            .serviceName("auth-service")
            .tableName("users")
            .operationType("UPDATE")
            .build();
        
        when(contextExtractor.extractContext(joinPoint)).thenReturn(context);
        when(joinPoint.proceed()).thenReturn(affectedRows);
        
        // When
        Object result = interceptor.interceptModifyMethods(joinPoint);
        
        // Then
        assertEquals(affectedRows, result);
        assertEquals(1, context.getAffectedRows());
        verify(contextExtractor).extractContext(joinPoint);
        verify(joinPoint).proceed();
    }
    
    @Test
    @DisplayName("修改方法拦截：执行失败")
    void testInterceptModifyMethods_Failure() throws Throwable {
        // Given
        RuntimeException executionException = new RuntimeException("更新失败");
        
        when(contextExtractor.extractContext(joinPoint)).thenReturn(context);
        when(joinPoint.proceed()).thenThrow(executionException);
        
        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> interceptor.interceptModifyMethods(joinPoint)
        );
        
        assertEquals("更新失败", exception.getMessage());
        verify(contextExtractor).extractContext(joinPoint);
        verify(joinPoint).proceed();
    }
    
    @Test
    @DisplayName("拦截器状态检查")
    void testInterceptorStatus() {
        // When
        boolean enabled = interceptor.isInterceptorEnabled();
        String stats = interceptor.getInterceptorStats();
        
        // Then
        assertTrue(enabled);
        assertNotNull(stats);
        assertTrue(stats.contains("DataAccessInterceptor"));
        assertTrue(stats.contains("enabled=true"));
    }
    
    @Test
    @DisplayName("上下文提取失败场景")
    void testInterceptDataAccess_ContextExtractionFailure() throws Throwable {
        // Given
        when(contextExtractor.extractContext(joinPoint))
            .thenThrow(new RuntimeException("上下文提取失败"));
        
        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> interceptor.interceptDataAccess(joinPoint)
        );
        
        assertEquals("上下文提取失败", exception.getMessage());
        verify(contextExtractor).extractContext(joinPoint);
        verify(permissionChecker, never()).hasPermission(any());
        verify(joinPoint, never()).proceed();
    }
}
