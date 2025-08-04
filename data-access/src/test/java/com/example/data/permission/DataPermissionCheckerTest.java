package com.example.data.permission;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * DataPermissionChecker单元测试
 * 
 * @author Data Access Test Generator
 * @version 1.0
 * @since 2025-08-03
 */
@ExtendWith(MockitoExtension.class)
class DataPermissionCheckerTest {

    private DataPermissionChecker permissionChecker;

    @BeforeEach
    void setUp() {
        permissionChecker = new DataPermissionChecker();
    }

    @Test
    @DisplayName("检查权限 - auth-service访问users表READ权限 - 有权限")
    void testHasPermission_AuthService_UsersRead_HasPermission() {
        // Given
        ReflectionTestUtils.setField(permissionChecker, "serviceName", "auth-service");
        
        // When
        boolean hasPermission = permissionChecker.hasPermission("users", OperationType.READ);
        
        // Then
        assertTrue(hasPermission);
    }

    @Test
    @DisplayName("检查权限 - auth-service访问users表DELETE权限 - 无权限")
    void testHasPermission_AuthService_UsersDelete_NoPermission() {
        // Given
        ReflectionTestUtils.setField(permissionChecker, "serviceName", "auth-service");
        
        // When
        boolean hasPermission = permissionChecker.hasPermission("users", OperationType.DELETE);
        
        // Then
        assertFalse(hasPermission);
    }

    @Test
    @DisplayName("检查权限 - user-service访问users表DELETE权限 - 有权限")
    void testHasPermission_UserService_UsersDelete_HasPermission() {
        // Given
        ReflectionTestUtils.setField(permissionChecker, "serviceName", "user-service");
        
        // When
        boolean hasPermission = permissionChecker.hasPermission("users", OperationType.DELETE);
        
        // Then
        assertTrue(hasPermission);
    }

    @Test
    @DisplayName("检查权限 - lead-service访问customer_leads表CREATE权限 - 有权限")
    void testHasPermission_LeadService_CustomerLeadsCreate_HasPermission() {
        // Given
        ReflectionTestUtils.setField(permissionChecker, "serviceName", "lead-service");
        
        // When
        boolean hasPermission = permissionChecker.hasPermission("customer_leads", OperationType.CREATE);
        
        // Then
        assertTrue(hasPermission);
    }

    @Test
    @DisplayName("检查权限 - product-service访问customer_leads表READ权限 - 无权限")
    void testHasPermission_ProductService_CustomerLeadsRead_NoPermission() {
        // Given
        ReflectionTestUtils.setField(permissionChecker, "serviceName", "product-service");
        
        // When
        boolean hasPermission = permissionChecker.hasPermission("customer_leads", OperationType.READ);
        
        // Then
        assertFalse(hasPermission);
    }

    @Test
    @DisplayName("检查权限 - 未知服务 - 无权限")
    void testHasPermission_UnknownService_NoPermission() {
        // Given
        ReflectionTestUtils.setField(permissionChecker, "serviceName", "unknown-service");
        
        // When
        boolean hasPermission = permissionChecker.hasPermission("users", OperationType.READ);
        
        // Then
        assertFalse(hasPermission);
    }

    @Test
    @DisplayName("检查权限 - 服务访问未定义表 - 无权限")
    void testHasPermission_UndefinedTable_NoPermission() {
        // Given
        ReflectionTestUtils.setField(permissionChecker, "serviceName", "auth-service");
        
        // When
        boolean hasPermission = permissionChecker.hasPermission("undefined_table", OperationType.READ);
        
        // Then
        assertFalse(hasPermission);
    }

    @Test
    @DisplayName("检查权限 - 所有服务都可以访问data_operation_logs表CREATE权限")
    void testHasPermission_AllServices_DataOperationLogsCreate_HasPermission() {
        // Given - 测试多个服务
        String[] services = {"auth-service", "user-service", "lead-service", "deal-service", 
                           "product-service", "promotion-service", "invitation-service"};
        
        for (String service : services) {
            ReflectionTestUtils.setField(permissionChecker, "serviceName", service);
            
            // When
            boolean hasPermission = permissionChecker.hasPermission("data_operation_logs", OperationType.CREATE);
            
            // Then
            assertTrue(hasPermission, "Service " + service + " should have CREATE permission for data_operation_logs");
        }
    }

    @Test
    @DisplayName("检查并抛出异常 - 有权限 - 不抛异常")
    void testCheckPermission_HasPermission_NoException() {
        // Given
        ReflectionTestUtils.setField(permissionChecker, "serviceName", "user-service");
        
        // When & Then
        assertDoesNotThrow(() -> {
            permissionChecker.checkPermission("users", OperationType.READ);
        });
    }

    @Test
    @DisplayName("检查并抛出异常 - 无权限 - 抛出异常")
    void testCheckPermission_NoPermission_ThrowsException() {
        // Given
        ReflectionTestUtils.setField(permissionChecker, "serviceName", "auth-service");
        
        // When & Then
        DataPermissionException exception = assertThrows(DataPermissionException.class, () -> {
            permissionChecker.checkPermission("users", OperationType.DELETE);
        });
        
        assertTrue(exception.getMessage().contains("auth-service"));
        assertTrue(exception.getMessage().contains("DELETE"));
        assertTrue(exception.getMessage().contains("users"));
    }

    @Test
    @DisplayName("获取服务名称")
    void testGetServiceName() {
        // Given
        String expectedServiceName = "test-service";
        ReflectionTestUtils.setField(permissionChecker, "serviceName", expectedServiceName);
        
        // When
        String serviceName = permissionChecker.getServiceName();
        
        // Then
        assertEquals(expectedServiceName, serviceName);
    }

    @Test
    @DisplayName("获取服务权限配置")
    void testGetServicePermissions() {
        // Given
        ReflectionTestUtils.setField(permissionChecker, "serviceName", "user-service");
        
        // When
        Map<String, List<OperationType>> permissions = permissionChecker.getServicePermissions();
        
        // Then
        assertNotNull(permissions);
        assertTrue(permissions.containsKey("users"));
        assertTrue(permissions.containsKey("agent_levels"));
        assertTrue(permissions.containsKey("data_operation_logs"));
        
        List<OperationType> userPermissions = permissions.get("users");
        assertNotNull(userPermissions);
        assertTrue(userPermissions.contains(OperationType.CREATE));
        assertTrue(userPermissions.contains(OperationType.READ));
        assertTrue(userPermissions.contains(OperationType.UPDATE));
        assertTrue(userPermissions.contains(OperationType.DELETE));
        assertTrue(userPermissions.contains(OperationType.STATS));
    }

    @Test
    @DisplayName("deal-service权限配置验证")
    void testDealServicePermissions() {
        // Given
        ReflectionTestUtils.setField(permissionChecker, "serviceName", "deal-service");
        
        // When & Then
        assertTrue(permissionChecker.hasPermission("deals", OperationType.CREATE));
        assertTrue(permissionChecker.hasPermission("deals", OperationType.READ));
        assertTrue(permissionChecker.hasPermission("deals", OperationType.UPDATE));
        assertTrue(permissionChecker.hasPermission("deals", OperationType.DELETE));
        assertTrue(permissionChecker.hasPermission("deals", OperationType.STATS));
        
        assertTrue(permissionChecker.hasPermission("commissions", OperationType.CREATE));
        assertTrue(permissionChecker.hasPermission("commissions", OperationType.READ));
        assertTrue(permissionChecker.hasPermission("commissions", OperationType.UPDATE));
        assertTrue(permissionChecker.hasPermission("commissions", OperationType.DELETE));
        assertTrue(permissionChecker.hasPermission("commissions", OperationType.STATS));
        
        assertTrue(permissionChecker.hasPermission("customer_leads", OperationType.READ));
        assertTrue(permissionChecker.hasPermission("customer_leads", OperationType.UPDATE));
        assertFalse(permissionChecker.hasPermission("customer_leads", OperationType.CREATE));
        assertFalse(permissionChecker.hasPermission("customer_leads", OperationType.DELETE));
        
        assertTrue(permissionChecker.hasPermission("products", OperationType.READ));
        assertFalse(permissionChecker.hasPermission("products", OperationType.CREATE));
        
        assertTrue(permissionChecker.hasPermission("users", OperationType.READ));
        assertFalse(permissionChecker.hasPermission("users", OperationType.CREATE));
    }

    @Test
    @DisplayName("promotion-service权限配置验证")
    void testPromotionServicePermissions() {
        // Given
        ReflectionTestUtils.setField(permissionChecker, "serviceName", "promotion-service");
        
        // When & Then
        assertTrue(permissionChecker.hasPermission("promotions", OperationType.CREATE));
        assertTrue(permissionChecker.hasPermission("promotions", OperationType.READ));
        assertTrue(permissionChecker.hasPermission("promotions", OperationType.UPDATE));
        assertTrue(permissionChecker.hasPermission("promotions", OperationType.DELETE));
        assertTrue(permissionChecker.hasPermission("promotions", OperationType.STATS));
        
        assertTrue(permissionChecker.hasPermission("promotion_audit_history", OperationType.CREATE));
        assertTrue(permissionChecker.hasPermission("second_audit_requests", OperationType.CREATE));
        assertTrue(permissionChecker.hasPermission("submission_limits", OperationType.CREATE));
        
        assertTrue(permissionChecker.hasPermission("users", OperationType.READ));
        assertFalse(permissionChecker.hasPermission("users", OperationType.CREATE));
    }

    @Test
    @DisplayName("invitation-service权限配置验证")
    void testInvitationServicePermissions() {
        // Given
        ReflectionTestUtils.setField(permissionChecker, "serviceName", "invitation-service");
        
        // When & Then
        assertTrue(permissionChecker.hasPermission("invitation_codes", OperationType.CREATE));
        assertTrue(permissionChecker.hasPermission("invitation_codes", OperationType.READ));
        assertTrue(permissionChecker.hasPermission("invitation_codes", OperationType.UPDATE));
        assertTrue(permissionChecker.hasPermission("invitation_codes", OperationType.DELETE));
        assertTrue(permissionChecker.hasPermission("invitation_codes", OperationType.STATS));
        
        assertTrue(permissionChecker.hasPermission("invitation_records", OperationType.CREATE));
        assertTrue(permissionChecker.hasPermission("invitation_records", OperationType.READ));
        assertTrue(permissionChecker.hasPermission("invitation_records", OperationType.UPDATE));
        assertTrue(permissionChecker.hasPermission("invitation_records", OperationType.DELETE));
        assertTrue(permissionChecker.hasPermission("invitation_records", OperationType.STATS));
        
        assertTrue(permissionChecker.hasPermission("users", OperationType.READ));
        assertFalse(permissionChecker.hasPermission("users", OperationType.CREATE));
    }
}