package com.example.data.permission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据访问权限检查服务
 * 根据微服务权限矩阵检查数据访问权限
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-03
 */
@Service
public class DataPermissionChecker {
    
    private static final Logger logger = LoggerFactory.getLogger(DataPermissionChecker.class);
    
    @Value("${spring.application.name:unknown-service}")
    private String serviceName;
    
    /**
     * 微服务数据权限矩阵
     * 根据development-workflow-standard.md中的权限分配表定义
     */
    private static final Map<String, Map<String, List<OperationType>>> SERVICE_PERMISSIONS = new HashMap<>();
    
    static {
        // auth-service权限 - 用户管理核心服务
        Map<String, List<OperationType>> authPermissions = new HashMap<>();
        authPermissions.put("users", Arrays.asList(OperationType.READ, OperationType.CREATE, OperationType.UPDATE));
        authPermissions.put("agent_levels", Arrays.asList(OperationType.READ));
        authPermissions.put("user_agent_level", Arrays.asList(OperationType.READ, OperationType.CREATE, OperationType.UPDATE));
        SERVICE_PERMISSIONS.put("auth-service", authPermissions);
        
        // user-service权限 - 用户管理完全权限
        Map<String, List<OperationType>> userPermissions = new HashMap<>();
        userPermissions.put("users", Arrays.asList(OperationType.values()));
        userPermissions.put("agent_levels", Arrays.asList(OperationType.values()));
        userPermissions.put("user_agent_level", Arrays.asList(OperationType.values()));
        userPermissions.put("agent_level_audit", Arrays.asList(OperationType.values()));
        userPermissions.put("agent_level_history", Arrays.asList(OperationType.values()));
        SERVICE_PERMISSIONS.put("user-service", userPermissions);
        
        // lead-service权限 - 客资管理服务
        Map<String, List<OperationType>> leadPermissions = new HashMap<>();
        leadPermissions.put("customer_leads", Arrays.asList(OperationType.values()));
        leadPermissions.put("lead_audit_records", Arrays.asList(OperationType.values()));
        leadPermissions.put("lead_audit_rewards", Arrays.asList(OperationType.values()));
        leadPermissions.put("users", Arrays.asList(OperationType.READ, OperationType.STATS));
        leadPermissions.put("products", Arrays.asList(OperationType.READ));
        SERVICE_PERMISSIONS.put("lead-service", leadPermissions);
        
        // deal-service权限 - 交易核心服务
        Map<String, List<OperationType>> dealPermissions = new HashMap<>();
        dealPermissions.put("deals", Arrays.asList(OperationType.values()));
        dealPermissions.put("commissions", Arrays.asList(OperationType.values()));
        dealPermissions.put("customer_leads", Arrays.asList(OperationType.READ, OperationType.UPDATE));
        dealPermissions.put("products", Arrays.asList(OperationType.READ));
        dealPermissions.put("users", Arrays.asList(OperationType.READ));
        SERVICE_PERMISSIONS.put("deal-service", dealPermissions);
        
        // product-service权限 - 商品管理服务
        Map<String, List<OperationType>> productPermissions = new HashMap<>();
        productPermissions.put("products", Arrays.asList(OperationType.values()));
        SERVICE_PERMISSIONS.put("product-service", productPermissions);
        
        // promotion-service权限 - 推广管理服务
        Map<String, List<OperationType>> promotionPermissions = new HashMap<>();
        promotionPermissions.put("promotions", Arrays.asList(OperationType.values()));
        promotionPermissions.put("promotion_audit_history", Arrays.asList(OperationType.values()));
        promotionPermissions.put("second_audit_requests", Arrays.asList(OperationType.values()));
        promotionPermissions.put("submission_limits", Arrays.asList(OperationType.values()));
        promotionPermissions.put("users", Arrays.asList(OperationType.READ));
        SERVICE_PERMISSIONS.put("promotion-service", promotionPermissions);
        
        // invitation-service权限 - 邀请系统服务
        Map<String, List<OperationType>> invitationPermissions = new HashMap<>();
        invitationPermissions.put("invitation_codes", Arrays.asList(OperationType.values()));
        invitationPermissions.put("invitation_records", Arrays.asList(OperationType.values()));
        invitationPermissions.put("users", Arrays.asList(OperationType.READ));
        SERVICE_PERMISSIONS.put("invitation-service", invitationPermissions);
        
        // 通用权限 - 所有服务都可以记录操作日志
        for (Map<String, List<OperationType>> permissions : SERVICE_PERMISSIONS.values()) {
            permissions.put("data_operation_logs", Arrays.asList(OperationType.CREATE));
        }
    }
    
    /**
     * 检查当前服务是否有权限访问指定表和操作
     * 
     * @param table 数据表名
     * @param operation 操作类型
     * @return 是否有权限
     */
    public boolean hasPermission(String table, OperationType operation) {
        Map<String, List<OperationType>> servicePermissions = SERVICE_PERMISSIONS.get(serviceName);
        
        if (servicePermissions == null) {
            logger.warn("No permissions defined for service: {}", serviceName);
            return false;
        }
        
        List<OperationType> tablePermissions = servicePermissions.get(table);
        if (tablePermissions == null) {
            logger.warn("Service {} has no permissions defined for table: {}", serviceName, table);
            return false;
        }
        
        boolean hasPermission = tablePermissions.contains(operation);
        
        if (!hasPermission) {
            logger.warn("Service {} does not have {} permission for table: {}", 
                       serviceName, operation, table);
        }
        
        return hasPermission;
    }
    
    /**
     * 检查并抛出权限异常
     * 
     * @param table 数据表名
     * @param operation 操作类型
     * @throws DataPermissionException 权限不足异常
     */
    public void checkPermission(String table, OperationType operation) throws DataPermissionException {
        if (!hasPermission(table, operation)) {
            throw new DataPermissionException(
                String.format("Service %s does not have %s permission for table %s", 
                             serviceName, operation, table));
        }
    }
    
    /**
     * 获取当前服务名称
     */
    public String getServiceName() {
        return serviceName;
    }
    
    /**
     * 获取服务的所有权限
     */
    public Map<String, List<OperationType>> getServicePermissions() {
        return SERVICE_PERMISSIONS.get(serviceName);
    }
}