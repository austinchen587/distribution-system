package com.example.common.utils;

import java.util.Set;

/**
 * 权限服务接口
 * 用于获取用户权限信息
 */
public interface PermissionService {
    /**
     * 获取用户的所有权限
     * 
     * @param userId 用户ID
     * @return 用户权限集合
     */
    Set<String> getUserPermissions(Long userId);
    
    /**
     * 刷新用户权限缓存
     * 
     * @param userId 用户ID
     */
    void refreshUserPermissions(Long userId);
    
    /**
     * 检查用户是否有指定权限
     * 
     * @param userId 用户ID
     * @param permission 权限标识
     * @return 是否有权限
     */
    boolean hasPermission(Long userId, String permission);
    
    /**
     * 根据角色获取默认权限
     * 
     * @param roleCode 角色代码
     * @return 角色权限集合
     */
    Set<String> getRolePermissions(String roleCode);
    
    /**
     * 检查角色是否有指定权限
     * 
     * @param roleCode 角色代码
     * @param permission 权限标识
     * @return 是否有权限
     */
    boolean hasRolePermission(String roleCode, String permission);
}