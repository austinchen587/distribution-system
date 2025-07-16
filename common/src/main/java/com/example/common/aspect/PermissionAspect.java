package com.example.common.aspect;

import com.example.common.annotation.RequirePermission;
import com.example.common.annotation.RequireRole;
import com.example.common.constants.ErrorCode;
import com.example.common.exception.AuthorizationException;
import com.example.common.utils.PermissionService;
import com.example.common.utils.UserContextHolder;
import com.example.common.enums.UserRole;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(PermissionAspect.class);
    
    @Autowired
    private PermissionService permissionService;
    
    @Around("@annotation(requireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequireRole requireRole) throws Throwable {
        String currentRole = UserContextHolder.getCurrentUserRole();
        
        if (currentRole == null) {
            throw new AuthorizationException(ErrorCode.UNAUTHORIZED);
        }
        
        UserRole[] allowedRoles = requireRole.value();
        boolean hasRole = false;
        
        for (UserRole role : allowedRoles) {
            if (role.name().equals(currentRole)) {
                hasRole = true;
                break;
            }
        }
        
        if (!hasRole) {
            logger.warn("用户角色权限不足: 当前角色={}, 需要角色={}", currentRole, allowedRoles);
            throw new AuthorizationException(ErrorCode.ROLE_NOT_ALLOWED);
        }
        
        return joinPoint.proceed();
    }
    
    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        String currentUserId = UserContextHolder.getCurrentUserId();
        String currentRole = UserContextHolder.getCurrentUserRole();
        
        if (currentUserId == null || currentRole == null) {
            throw new AuthorizationException(ErrorCode.UNAUTHORIZED);
        }
        
        String[] permissions = requirePermission.value();
        
        // 超级管理员拥有所有权限
        if (UserRole.SUPER_ADMIN.name().equals(currentRole)) {
            return joinPoint.proceed();
        }
        
        // 检查基于角色的权限
        boolean hasPermission = false;
        for (String permission : permissions) {
            if (permissionService.hasRolePermission(currentRole, permission)) {
                hasPermission = true;
                break;
            }
        }
        
        if (hasPermission) {
            return joinPoint.proceed();
        }
        
        // 检查用户特定权限
        Long userId = Long.parseLong(currentUserId);
        for (String permission : permissions) {
            if (permissionService.hasPermission(userId, permission)) {
                return joinPoint.proceed();
            }
        }
        
        logger.warn("用户权限不足: userId={}, role={}, 需要权限={}", currentUserId, currentRole, String.join(", ", permissions));
        throw new AuthorizationException(ErrorCode.PERMISSION_DENIED);
    }
}