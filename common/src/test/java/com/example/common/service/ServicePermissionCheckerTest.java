package com.example.common.service;

import com.example.common.dto.DataAccessContext;
import com.example.common.entity.ServiceDataPermission;
import com.example.common.mapper.ServicePermissionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * 服务权限检查器单元测试
 * 
 * <p>测试ServicePermissionChecker的核心功能，包括权限验证逻辑、缓存机制等。
 * 
 * @author Edom
 * @date 2025-08-01
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class ServicePermissionCheckerTest {
    
    @Mock
    private ServicePermissionMapper permissionMapper;
    
    @Mock
    private StringRedisTemplate redisTemplate;
    
    @Mock
    private ValueOperations<String, String> valueOperations;
    
    @InjectMocks
    private ServicePermissionChecker permissionChecker;
    
    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }
    
    @Test
    @DisplayName("正常场景：服务有权限访问数据表")
    void testHasPermission_Success() {
        // Given
        String serviceName = "auth-service";
        String tableName = "users";
        String operationType = "SELECT";
        
        ServiceDataPermission permission = new ServiceDataPermission();
        permission.setServiceName(serviceName);
        permission.setTableName(tableName);
        permission.setOperationType(operationType);
        permission.setPermissionLevel("FULL");
        permission.setIsEnabled(true);
        
        when(valueOperations.get(anyString())).thenReturn(null); // 缓存未命中
        when(permissionMapper.findPermission(serviceName, tableName, operationType))
            .thenReturn(permission);
        
        // When
        boolean result = permissionChecker.hasPermission(serviceName, tableName, operationType);
        
        // Then
        assertTrue(result);
        verify(permissionMapper).findPermission(serviceName, tableName, operationType);
        verify(valueOperations).set(anyString(), eq("FULL"), anyLong(), any());
    }
    
    @Test
    @DisplayName("异常场景：服务无权限访问数据表")
    void testHasPermission_Denied() {
        // Given
        String serviceName = "auth-service";
        String tableName = "deals";
        String operationType = "SELECT";
        
        when(valueOperations.get(anyString())).thenReturn(null); // 缓存未命中
        when(permissionMapper.findPermission(serviceName, tableName, operationType))
            .thenReturn(null); // 权限不存在
        
        // When
        boolean result = permissionChecker.hasPermission(serviceName, tableName, operationType);
        
        // Then
        assertFalse(result);
        verify(permissionMapper).findPermission(serviceName, tableName, operationType);
        verify(valueOperations).set(anyString(), eq("DENIED"), anyLong(), any());
    }
    
    @Test
    @DisplayName("缓存场景：从缓存获取权限结果")
    void testHasPermission_CacheHit() {
        // Given
        String serviceName = "auth-service";
        String tableName = "users";
        String operationType = "SELECT";
        
        when(valueOperations.get(anyString())).thenReturn("FULL"); // 缓存命中
        
        // When
        boolean result = permissionChecker.hasPermission(serviceName, tableName, operationType);
        
        // Then
        assertTrue(result);
        verify(valueOperations).get(anyString());
        verify(permissionMapper, never()).findPermission(anyString(), anyString(), anyString());
    }
    
    @Test
    @DisplayName("异常场景：权限配置被禁用")
    void testHasPermission_Disabled() {
        // Given
        String serviceName = "auth-service";
        String tableName = "users";
        String operationType = "SELECT";
        
        ServiceDataPermission permission = new ServiceDataPermission();
        permission.setServiceName(serviceName);
        permission.setTableName(tableName);
        permission.setOperationType(operationType);
        permission.setPermissionLevel("FULL");
        permission.setIsEnabled(false); // 权限被禁用
        
        when(valueOperations.get(anyString())).thenReturn(null);
        when(permissionMapper.findPermission(serviceName, tableName, operationType))
            .thenReturn(permission);
        
        // When
        boolean result = permissionChecker.hasPermission(serviceName, tableName, operationType);
        
        // Then
        assertFalse(result);
        verify(valueOperations).set(anyString(), eq("DENIED"), anyLong(), any());
    }
    
    @Test
    @DisplayName("异常场景：参数无效")
    void testHasPermission_InvalidParameters() {
        // When & Then
        assertFalse(permissionChecker.hasPermission(null, "users", "SELECT"));
        assertFalse(permissionChecker.hasPermission("auth-service", null, "SELECT"));
        assertFalse(permissionChecker.hasPermission("auth-service", "users", null));
        assertFalse(permissionChecker.hasPermission("", "users", "SELECT"));
        
        // 验证没有调用数据库查询
        verify(permissionMapper, never()).findPermission(anyString(), anyString(), anyString());
    }
    
    @Test
    @DisplayName("上下文场景：使用DataAccessContext检查权限")
    void testHasPermission_WithContext() {
        // Given
        DataAccessContext context = DataAccessContext.builder()
            .serviceName("auth-service")
            .tableName("users")
            .operationType("SELECT")
            .build();
        
        when(valueOperations.get(anyString())).thenReturn("FULL");
        
        // When
        boolean result = permissionChecker.hasPermission(context);
        
        // Then
        assertTrue(result);
        verify(valueOperations).get(anyString());
    }
    
    @Test
    @DisplayName("异常场景：上下文为空")
    void testHasPermission_NullContext() {
        // When
        boolean result = permissionChecker.hasPermission((DataAccessContext) null);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    @DisplayName("功能测试：获取权限详细信息")
    void testGetPermissionDetail() {
        // Given
        String serviceName = "auth-service";
        String tableName = "users";
        String operationType = "SELECT";
        
        ServiceDataPermission permission = new ServiceDataPermission();
        permission.setServiceName(serviceName);
        permission.setTableName(tableName);
        permission.setOperationType(operationType);
        permission.setPermissionLevel("FULL");
        
        when(permissionMapper.findPermission(serviceName, tableName, operationType))
            .thenReturn(permission);
        
        // When
        ServiceDataPermission result = permissionChecker.getPermissionDetail(serviceName, tableName, operationType);
        
        // Then
        assertNotNull(result);
        assertEquals(serviceName, result.getServiceName());
        assertEquals(tableName, result.getTableName());
        assertEquals(operationType, result.getOperationType());
        assertEquals("FULL", result.getPermissionLevel());
    }
    
    @Test
    @DisplayName("异常处理：数据库查询异常")
    void testHasPermission_DatabaseException() {
        // Given
        String serviceName = "auth-service";
        String tableName = "users";
        String operationType = "SELECT";
        
        when(valueOperations.get(anyString())).thenReturn(null);
        when(permissionMapper.findPermission(serviceName, tableName, operationType))
            .thenThrow(new RuntimeException("数据库连接失败"));
        
        // When
        boolean result = permissionChecker.hasPermission(serviceName, tableName, operationType);
        
        // Then
        assertFalse(result); // 异常情况下拒绝访问
    }
}
