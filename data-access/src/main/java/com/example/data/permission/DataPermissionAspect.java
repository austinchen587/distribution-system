package com.example.data.permission;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 数据访问权限控制切面
 * 拦截带有@DataPermission注解的方法，进行权限检查和日志记录
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-03
 */
@Aspect
@Component
public class DataPermissionAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(DataPermissionAspect.class);
    
    @Autowired
    private DataPermissionChecker permissionChecker;
    
    @Autowired
    private DataOperationLogger operationLogger;
    
    /**
     * 拦截带有@DataPermission注解的方法
     */
    @Around("@annotation(com.example.data.permission.DataPermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DataPermission annotation = method.getAnnotation(DataPermission.class);
        
        if (annotation == null) {
            return joinPoint.proceed();
        }
        
        // 如果设置了跳过检查，直接执行
        if (annotation.skipCheck()) {
            return joinPoint.proceed();
        }
        
        String table = annotation.table();
        OperationType operation = annotation.operation();
        String description = annotation.description();
        boolean logOperation = annotation.logOperation();
        
        long startTime = System.currentTimeMillis();
        String serviceName = permissionChecker.getServiceName();
        
        try {
            // 权限检查
            permissionChecker.checkPermission(table, operation);
            
            // 执行目标方法
            Object result = joinPoint.proceed();
            
            // 记录操作日志（如果需要）
            if (logOperation) {
                long executeTime = System.currentTimeMillis() - startTime;
                operationLogger.logSuccess(serviceName, table, operation, description, 
                                         method.getName(), executeTime);
            }
            
            return result;
            
        } catch (DataPermissionException e) {
            // 记录权限拒绝日志
            if (logOperation) {
                long executeTime = System.currentTimeMillis() - startTime;
                operationLogger.logPermissionDenied(serviceName, table, operation, description, 
                                                   method.getName(), e.getMessage(), executeTime);
            }
            throw e;
            
        } catch (Exception e) {
            // 记录执行失败日志
            if (logOperation) {
                long executeTime = System.currentTimeMillis() - startTime;
                operationLogger.logFailure(serviceName, table, operation, description, 
                                         method.getName(), e.getMessage(), executeTime);
            }
            throw e;
        }
    }
}