package com.example.common.utils.impl;

import com.example.common.constants.RedisKeys;
import com.example.common.enums.UserRole;
import com.example.common.utils.PermissionService;
import com.example.common.utils.UserContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 权限服务实现类
 * 处理用户权限的获取和缓存
 */
@Service
public class PermissionServiceImpl implements PermissionService {
    
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;
    
    // 缓存过期时间（小时）
    private static final long CACHE_EXPIRE_HOURS = 2;
    
    @Override
    public Set<String> getUserPermissions(Long userId) {
        // 先从缓存获取
        String cacheKey = RedisKeys.getUserPermissionKey(userId);
        if (redisTemplate != null) {
            @SuppressWarnings("unchecked")
            Set<String> cachedPermissions = (Set<String>) redisTemplate.opsForValue().get(cacheKey);
            if (cachedPermissions != null) {
                return cachedPermissions;
            }
        }
        
        // 从数据库获取用户角色，然后获取对应权限
        // 实际项目中这里应该查询数据库获取用户角色和自定义权限
        String userRoleStr = UserContextHolder.getCurrentUserRole();
        if (userRoleStr == null) {
            return new HashSet<>();
        }
        Set<String> permissions = getRolePermissions(userRoleStr);
        
        // 缓存权限
        if (redisTemplate != null && permissions != null && !permissions.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, permissions, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        }
        
        return permissions;
    }
    
    @Override
    public void refreshUserPermissions(Long userId) {
        String cacheKey = RedisKeys.getUserPermissionKey(userId);
        if (redisTemplate != null) {
            redisTemplate.delete(cacheKey);
        }
    }
    
    @Override
    public boolean hasPermission(Long userId, String permission) {
        Set<String> permissions = getUserPermissions(userId);
        return permissions != null && permissions.contains(permission);
    }
    
    @Override
    public Set<String> getRolePermissions(String roleCode) {
        Set<String> permissions = new HashSet<>();
        
        // 根据角色定义权限
        try {
            UserRole role = UserRole.valueOf(roleCode.toUpperCase());
            switch (role) {
                case SUPER_ADMIN:
                    // 超级管理员拥有所有权限
                    permissions.add("*");
                    break;
                    
                case DIRECTOR:
                    // 总监权限
                    permissions.add("user:view");
                    permissions.add("user:create");
                    permissions.add("user:update");
                    permissions.add("user:delete");
                    permissions.add("sales:*");
                    permissions.add("lead:*");
                    permissions.add("deal:*");
                    permissions.add("commission:view");
                    permissions.add("commission:approve");
                    permissions.add("agent:level:approve");
                    permissions.add("promotion:approve");
                    permissions.add("report:*");
                    break;
                    
                case LEADER:
                    // 组长权限
                    permissions.add("user:view");
                    permissions.add("sales:view");
                    permissions.add("sales:manage:team");
                    permissions.add("lead:view");
                    permissions.add("lead:assign");
                    permissions.add("deal:view");
                    permissions.add("deal:approve");
                    permissions.add("commission:view:team");
                    permissions.add("promotion:approve:team");
                    permissions.add("report:team");
                    break;
                    
                case SALES:
                    // 销售权限
                    permissions.add("user:view:self");
                    permissions.add("lead:create");
                    permissions.add("lead:view:assigned");
                    permissions.add("lead:update:assigned");
                    permissions.add("deal:create");
                    permissions.add("deal:view:own");
                    permissions.add("deal:update:own");
                    permissions.add("commission:view:own");
                    permissions.add("customer:view:assigned");
                    permissions.add("customer:update:assigned");
                    permissions.add("product:view");
                    permissions.add("promotion:view");
                    break;
                    
                case AGENT:
                    // 代理权限
                    permissions.add("user:view:self");
                    permissions.add("lead:create");
                    permissions.add("lead:view:own");
                    permissions.add("promotion:create");
                    permissions.add("promotion:view:own");
                    permissions.add("commission:view:own");
                    permissions.add("deal:view:related");
                    permissions.add("agent:team:view");
                    permissions.add("agent:invite");
                    break;
            }
        } catch (IllegalArgumentException e) {
            // 如果角色不存在，返回空集合
            return new HashSet<>();
        }
        
        return permissions;
    }

    @Override
    public boolean hasRolePermission(String roleCode, String permission) {
        Set<String> permissions = getRolePermissions(roleCode);
        return permissions.contains(permission) || permissions.contains("*");
    }
}