package com.example.common.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("用户角色枚举单元测试")
class UserRoleTest {

    @Test
    @DisplayName("测试所有用户角色值")
    void testAllUserRoles() {
        UserRole[] roles = UserRole.values();
        
        assertEquals(5, roles.length);
        assertTrue(containsRole(roles, UserRole.SUPER_ADMIN));
        assertTrue(containsRole(roles, UserRole.DIRECTOR));
        assertTrue(containsRole(roles, UserRole.LEADER));
        assertTrue(containsRole(roles, UserRole.SALES));
        assertTrue(containsRole(roles, UserRole.AGENT));
    }

    @Test
    @DisplayName("测试角色名称和描述")
    void testRoleNamesAndDescriptions() {
        assertEquals("super_admin", UserRole.SUPER_ADMIN.getCode());
        assertEquals("director", UserRole.DIRECTOR.getCode());
        assertEquals("leader", UserRole.LEADER.getCode());
        assertEquals("sales", UserRole.SALES.getCode());
        assertEquals("agent", UserRole.AGENT.getCode());
    }

    @Test
    @DisplayName("测试角色权限级别")
    void testRolePermissionLevels() {
        // 测试权限级别顺序
        assertTrue(UserRole.SUPER_ADMIN.ordinal() < UserRole.DIRECTOR.ordinal());
        assertTrue(UserRole.DIRECTOR.ordinal() < UserRole.LEADER.ordinal());
        assertTrue(UserRole.LEADER.ordinal() < UserRole.SALES.ordinal());
        assertTrue(UserRole.SALES.ordinal() < UserRole.AGENT.ordinal());
    }

    @Test
    @DisplayName("测试角色枚举值转换")
    void testEnumValueOf() {
        assertEquals(UserRole.SUPER_ADMIN, UserRole.valueOf("SUPER_ADMIN"));
        assertEquals(UserRole.DIRECTOR, UserRole.valueOf("DIRECTOR"));
        assertEquals(UserRole.LEADER, UserRole.valueOf("LEADER"));
        assertEquals(UserRole.SALES, UserRole.valueOf("SALES"));
        assertEquals(UserRole.AGENT, UserRole.valueOf("AGENT"));
    }

    @Test
    @DisplayName("测试无效角色值处理")
    void testInvalidRoleValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            UserRole.valueOf("INVALID_ROLE");
        });
    }

    @Test
    @DisplayName("测试角色比较")
    void testRoleComparison() {
        assertNotEquals(UserRole.SUPER_ADMIN, UserRole.DIRECTOR);
        assertNotEquals(UserRole.LEADER, UserRole.SALES);
        assertEquals(UserRole.AGENT, UserRole.AGENT);
    }

    private boolean containsRole(UserRole[] roles, UserRole role) {
        for (UserRole r : roles) {
            if (r == role) {
                return true;
            }
        }
        return false;
    }
}