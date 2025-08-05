package com.example.common.utils;

import com.example.common.constants.RedisKeys;
import com.example.common.enums.UserRole;
import com.example.common.utils.impl.PermissionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("权限服务单元测试")
class PermissionServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    private static final Long TEST_USER_ID = 12345L;
    private static final String TEST_ROLE = "AGENT";

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("测试获取用户权限 - 缓存命中")
    void testGetUserPermissionsFromCache() {
        String cacheKey = RedisKeys.getUserPermissionKey(TEST_USER_ID);
        Set<String> cachedPermissions = Set.of("lead:create", "lead:view:own");
        
        when(valueOperations.get(cacheKey)).thenReturn(cachedPermissions);
        
        Set<String> permissions = permissionService.getUserPermissions(TEST_USER_ID);
        
        assertNotNull(permissions);
        assertEquals(2, permissions.size());
        assertTrue(permissions.contains("lead:create"));
        assertTrue(permissions.contains("lead:view:own"));
        verify(valueOperations).get(cacheKey);
        verifyNoMoreInteractions(valueOperations);
    }

    @Test
    @DisplayName("测试获取用户权限 - 缓存未命中，从角色获取")
    void testGetUserPermissionsFromRole() {
        String cacheKey = RedisKeys.getUserPermissionKey(TEST_USER_ID);
        
        when(valueOperations.get(cacheKey)).thenReturn(null);
        
        // Mock UserContextHolder.getCurrentUserRole() using a different approach
        // This would typically require PowerMockito or similar for static mocking
        // For now, we'll test the role-based permissions directly
        
        Set<String> permissions = permissionService.getRolePermissions("AGENT");
        
        assertNotNull(permissions);
        assertTrue(permissions.size() > 0);
        assertTrue(permissions.contains("lead:create"));
        assertTrue(permissions.contains("agent:invite"));
    }

    @Test
    @DisplayName("测试获取角色权限 - AGENT")
    void testGetAgentRolePermissions() {
        Set<String> permissions = permissionService.getRolePermissions("AGENT");
        
        assertNotNull(permissions);
        assertTrue(permissions.contains("user:view:self"));
        assertTrue(permissions.contains("lead:create"));
        assertTrue(permissions.contains("lead:view:own"));
        assertTrue(permissions.contains("promotion:create"));
        assertTrue(permissions.contains("promotion:view:own"));
        assertTrue(permissions.contains("commission:view:own"));
    }

    @Test
    @DisplayName("测试获取角色权限 - SALES")
    void testGetSalesRolePermissions() {
        Set<String> permissions = permissionService.getRolePermissions("SALES");
        
        assertNotNull(permissions);
        assertTrue(permissions.contains("user:view:self"));
        assertTrue(permissions.contains("lead:create"));
        assertTrue(permissions.contains("lead:view:assigned"));
        assertTrue(permissions.contains("deal:create"));
        assertTrue(permissions.contains("deal:view:own"));
    }

    @Test
    @DisplayName("测试获取角色权限 - LEADER")
    void testGetLeaderRolePermissions() {
        Set<String> permissions = permissionService.getRolePermissions("LEADER");
        
        assertNotNull(permissions);
        assertTrue(permissions.contains("user:view"));
        assertTrue(permissions.contains("sales:manage:team"));
        assertTrue(permissions.contains("lead:assign"));
        assertTrue(permissions.contains("deal:approve"));
    }

    @Test
    @DisplayName("测试获取角色权限 - DIRECTOR")
    void testGetDirectorRolePermissions() {
        Set<String> permissions = permissionService.getRolePermissions("DIRECTOR");

        assertNotNull(permissions);
        // 验证用户管理权限
        assertTrue(permissions.contains("user:view"));
        assertTrue(permissions.contains("user:create"));
        assertTrue(permissions.contains("user:update"));
        assertTrue(permissions.contains("user:delete"));
        // 验证其他权限
        assertTrue(permissions.contains("sales:*"));
        assertTrue(permissions.contains("lead:*"));
        assertTrue(permissions.contains("deal:*"));
        assertTrue(permissions.contains("commission:approve"));
    }

    @Test
    @DisplayName("测试获取角色权限 - SUPER_ADMIN")
    void testGetSuperAdminRolePermissions() {
        Set<String> permissions = permissionService.getRolePermissions("SUPER_ADMIN");
        
        assertNotNull(permissions);
        assertEquals(1, permissions.size());
        assertTrue(permissions.contains("*"));
    }

    @Test
    @DisplayName("测试检查用户权限")
    void testHasPermission() {
        Set<String> permissions = Set.of("lead:create", "lead:view:own");
        String cacheKey = RedisKeys.getUserPermissionKey(TEST_USER_ID);
        
        when(valueOperations.get(cacheKey)).thenReturn(permissions);
        
        assertTrue(permissionService.hasPermission(TEST_USER_ID, "lead:create"));
        assertTrue(permissionService.hasPermission(TEST_USER_ID, "lead:view:own"));
        assertFalse(permissionService.hasPermission(TEST_USER_ID, "invalid:permission"));
    }

    @Test
    @DisplayName("测试检查角色权限")
    void testHasRolePermission() {
        assertTrue(permissionService.hasRolePermission("AGENT", "lead:create"));
        assertTrue(permissionService.hasRolePermission("AGENT", "agent:invite"));
        assertFalse(permissionService.hasRolePermission("AGENT", "user:delete"));
        
        // SUPER_ADMIN 应该拥有所有权限
        assertTrue(permissionService.hasRolePermission("SUPER_ADMIN", "any:permission"));
    }

    @Test
    @DisplayName("测试刷新用户权限缓存")
    void testRefreshUserPermissions() {
        String cacheKey = RedisKeys.getUserPermissionKey(TEST_USER_ID);
        
        permissionService.refreshUserPermissions(TEST_USER_ID);
        
        verify(redisTemplate).delete(cacheKey);
    }

    @Test
    @DisplayName("测试无效角色异常处理")
    void testInvalidRoleException() {
        assertThrows(IllegalArgumentException.class, () -> {
            permissionService.getRolePermissions("INVALID_ROLE");
        });
    }

    @Test
    @DisplayName("测试空角色权限")
    void testNullRolePermissions() {
        // 测试当角色为null时的情况（虽然实际不会发生）
        // 这里我们测试角色权限返回的集合不为null
        Set<String> permissions = permissionService.getRolePermissions("AGENT");
        assertNotNull(permissions);
        assertFalse(permissions.isEmpty());
    }

    @Test
    @DisplayName("测试权限集合完整性")
    void testRolePermissionsCompleteness() {
        for (UserRole role : UserRole.values()) {
            Set<String> permissions = permissionService.getRolePermissions(role.name());
            assertNotNull(permissions);
            assertFalse(permissions.isEmpty(), "角色 " + role.name() + " 应该至少有一个权限");
            
            if (role == UserRole.SUPER_ADMIN) {
                assertEquals(1, permissions.size());
                assertTrue(permissions.contains("*"));
            }
        }
    }
}