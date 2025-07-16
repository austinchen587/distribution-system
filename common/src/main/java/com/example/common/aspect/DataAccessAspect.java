package com.example.common.aspect;

import com.example.common.utils.UserContextHolder;
import com.example.common.enums.UserRole;
import com.example.common.exception.AuthorizationException;
import com.example.common.constants.ErrorCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DataAccessAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(DataAccessAspect.class);
    
    @Around("execution(* com.example.*.service.*.get*ById(..)) && args(id)")
    public Object checkDataAccess(ProceedingJoinPoint joinPoint, Long id) throws Throwable {
        String currentUserId = UserContextHolder.getCurrentUserId();
        String currentRole = UserContextHolder.getCurrentUserRole();
        
        if (currentUserId == null || currentRole == null) {
            throw new AuthorizationException(ErrorCode.UNAUTHORIZED);
        }
        
        UserRole role = UserRole.valueOf(currentRole);
        
        // 超级管理员和总监可以访问所有数据
        if (role == UserRole.SUPER_ADMIN || role == UserRole.DIRECTOR) {
            return joinPoint.proceed();
        }
        
        // 其他角色只能访问自己的数据或相关的数据
        if (role == UserRole.SALES || role == UserRole.AGENT) {
            Long userIdLong = Long.parseLong(currentUserId);
            if (!userIdLong.equals(id)) {
                // 这里可以添加更复杂的逻辑来检查数据关联关系
                // 例如检查销售是否可以访问分配给他的客户数据
                logger.warn("用户尝试访问无权限的数据: userId={}, accessId={}", currentUserId, id);
                throw new AuthorizationException(ErrorCode.DATA_ACCESS_DENIED);
            }
        }
        
        return joinPoint.proceed();
    }
    
    @Around("execution(* com.example.*.service.*.update*(..)) && args(id, ..)")
    public Object checkUpdateAccess(ProceedingJoinPoint joinPoint, Long id) throws Throwable {
        String currentUserId = UserContextHolder.getCurrentUserId();
        String currentRole = UserContextHolder.getCurrentUserRole();
        
        if (currentUserId == null || currentRole == null) {
            throw new AuthorizationException(ErrorCode.UNAUTHORIZED);
        }
        
        UserRole role = UserRole.valueOf(currentRole);
        
        // 超级管理员可以修改所有数据
        if (role == UserRole.SUPER_ADMIN) {
            return joinPoint.proceed();
        }
        
        // 总监和组长可以修改团队数据
        if (role == UserRole.DIRECTOR || role == UserRole.LEADER) {
            // TODO: 实现团队关系检查
            return joinPoint.proceed();
        }
        
        // 销售和代理只能修改自己的数据
        if (role == UserRole.SALES || role == UserRole.AGENT) {
            Long userIdLong = Long.parseLong(currentUserId);
            if (!userIdLong.equals(id)) {
                logger.warn("用户尝试修改无权限的数据: userId={}, updateId={}", currentUserId, id);
                throw new AuthorizationException(ErrorCode.DATA_ACCESS_DENIED);
            }
        }
        
        return joinPoint.proceed();
    }
}