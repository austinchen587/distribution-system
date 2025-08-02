package com.example.common.service;

import com.example.common.dto.DataAccessContext;
import com.example.common.entity.ServiceDataPermission;
import com.example.common.enums.PermissionLevel;
import com.example.common.mapper.ServicePermissionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 服务权限检查器
 * 
 * <p>负责检查微服务对数据表的访问权限，是权限控制系统的核心组件。
 * 该组件实现了高效的权限验证逻辑，支持缓存机制以提高性能。
 * 
 * <p>主要功能：
 * <ul>
 *   <li>验证服务对数据表的访问权限</li>
 *   <li>支持表级和操作级的细粒度权限控制</li>
 *   <li>集成Redis缓存机制提高查询性能</li>
 *   <li>支持权限级别的判断和条件验证</li>
 * </ul>
 * 
 * <p>权限检查流程：
 * <ol>
 *   <li>从缓存中查询权限配置</li>
 *   <li>如果缓存未命中，从数据库查询</li>
 *   <li>根据权限级别进行访问控制判断</li>
 *   <li>将查询结果缓存以提高后续性能</li>
 * </ol>
 * 
 * <p>缓存策略：
 * <ul>
 *   <li>缓存键格式：perm:{serviceName}:{tableName}:{operationType}</li>
 *   <li>缓存TTL：1小时</li>
 *   <li>缓存值：权限级别代码（FULL/RESTRICTED/DENIED）</li>
 * </ul>
 * 
 * @author Edom
 * @date 2025-08-01
 * @since 1.0.0
 */
@Component
public class ServicePermissionChecker {

    private static final Logger log = LoggerFactory.getLogger(ServicePermissionChecker.class);
    
    private static final String PERMISSION_CACHE_PREFIX = "perm:";
    private static final long CACHE_TTL_HOURS = 1;
    private static final String DENIED_PERMISSION = "DENIED";
    
    @Autowired
    private ServicePermissionMapper permissionMapper;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    /**
     * 检查服务是否有权限访问指定的数据表和操作
     * 
     * @param serviceName 微服务名称
     * @param tableName 数据表名称
     * @param operationType 操作类型
     * @return 是否有权限访问
     */
    public boolean hasPermission(String serviceName, String tableName, String operationType) {
        try {
            // 参数验证
            if (!StringUtils.hasText(serviceName) || !StringUtils.hasText(tableName) || !StringUtils.hasText(operationType)) {
                log.warn("权限检查参数无效: serviceName={}, tableName={}, operationType={}", 
                        serviceName, tableName, operationType);
                return false;
            }
            
            // 构建缓存键
            String cacheKey = buildCacheKey(serviceName, tableName, operationType);
            
            // 1. 从缓存获取权限
            String cachedPermission = redisTemplate.opsForValue().get(cacheKey);
            if (cachedPermission != null) {
                log.debug("从缓存获取权限: {} -> {}", cacheKey, cachedPermission);
                return !DENIED_PERMISSION.equals(cachedPermission);
            }
            
            // 2. 从数据库查询权限
            ServiceDataPermission permission = permissionMapper.findPermission(serviceName, tableName, operationType);
            
            // 3. 判断权限级别
            String permissionLevel = evaluatePermission(permission);
            boolean hasAccess = !DENIED_PERMISSION.equals(permissionLevel);
            
            // 4. 缓存结果
            cachePermission(cacheKey, permissionLevel);
            
            log.debug("权限检查结果: serviceName={}, tableName={}, operationType={}, result={}, level={}", 
                    serviceName, tableName, operationType, hasAccess, permissionLevel);
            
            return hasAccess;
            
        } catch (Exception e) {
            log.error("权限检查发生异常: serviceName={}, tableName={}, operationType={}", 
                    serviceName, tableName, operationType, e);
            // 异常情况下拒绝访问，确保安全
            return false;
        }
    }
    
    /**
     * 检查数据访问上下文的权限
     * 
     * @param context 数据访问上下文
     * @return 是否有权限访问
     */
    public boolean hasPermission(DataAccessContext context) {
        if (context == null) {
            log.warn("数据访问上下文为空");
            return false;
        }
        
        return hasPermission(context.getServiceName(), context.getTableName(), context.getOperationType());
    }
    
    /**
     * 获取权限详细信息
     * 
     * @param serviceName 微服务名称
     * @param tableName 数据表名称
     * @param operationType 操作类型
     * @return 权限配置信息，如果不存在返回null
     */
    public ServiceDataPermission getPermissionDetail(String serviceName, String tableName, String operationType) {
        try {
            return permissionMapper.findPermission(serviceName, tableName, operationType);
        } catch (Exception e) {
            log.error("获取权限详情失败: serviceName={}, tableName={}, operationType={}", 
                    serviceName, tableName, operationType, e);
            return null;
        }
    }
    
    /**
     * 清除指定权限的缓存
     * 
     * @param serviceName 微服务名称
     * @param tableName 数据表名称
     * @param operationType 操作类型
     */
    public void clearPermissionCache(String serviceName, String tableName, String operationType) {
        String cacheKey = buildCacheKey(serviceName, tableName, operationType);
        redisTemplate.delete(cacheKey);
        log.info("清除权限缓存: {}", cacheKey);
    }
    
    /**
     * 清除指定服务的所有权限缓存
     * 
     * @param serviceName 微服务名称
     */
    public void clearServicePermissionCache(String serviceName) {
        String pattern = PERMISSION_CACHE_PREFIX + serviceName + ":*";
        redisTemplate.delete(redisTemplate.keys(pattern));
        log.info("清除服务权限缓存: {}", pattern);
    }
    
    /**
     * 预热权限缓存
     * 加载所有启用的权限配置到缓存中
     */
    public void warmupPermissionCache() {
        try {
            log.info("开始预热权限缓存");
            List<ServiceDataPermission> permissions = permissionMapper.selectAllEnabled();
            
            for (ServiceDataPermission permission : permissions) {
                String cacheKey = buildCacheKey(
                    permission.getServiceName(), 
                    permission.getTableName(), 
                    permission.getOperationType()
                );
                String permissionLevel = permission.getPermissionLevel();
                cachePermission(cacheKey, permissionLevel);
            }
            
            log.info("权限缓存预热完成，共加载 {} 条权限配置", permissions.size());
        } catch (Exception e) {
            log.error("权限缓存预热失败", e);
        }
    }
    
    /**
     * 构建缓存键
     * 
     * @param serviceName 微服务名称
     * @param tableName 数据表名称
     * @param operationType 操作类型
     * @return 缓存键
     */
    private String buildCacheKey(String serviceName, String tableName, String operationType) {
        return PERMISSION_CACHE_PREFIX + serviceName + ":" + tableName + ":" + operationType;
    }
    
    /**
     * 评估权限配置，返回权限级别
     * 
     * @param permission 权限配置
     * @return 权限级别代码
     */
    private String evaluatePermission(ServiceDataPermission permission) {
        if (permission == null || !Boolean.TRUE.equals(permission.getIsEnabled())) {
            return DENIED_PERMISSION;
        }
        
        return permission.getPermissionLevel();
    }
    
    /**
     * 缓存权限结果
     * 
     * @param cacheKey 缓存键
     * @param permissionLevel 权限级别
     */
    private void cachePermission(String cacheKey, String permissionLevel) {
        try {
            redisTemplate.opsForValue().set(cacheKey, permissionLevel, CACHE_TTL_HOURS, TimeUnit.HOURS);
            log.debug("缓存权限结果: {} -> {}", cacheKey, permissionLevel);
        } catch (Exception e) {
            log.warn("缓存权限结果失败: cacheKey={}, permissionLevel={}", cacheKey, permissionLevel, e);
        }
    }
    
    /**
     * 检查权限级别是否允许访问
     * 
     * @param permissionLevel 权限级别
     * @return 是否允许访问
     */
    private boolean isAccessAllowed(String permissionLevel) {
        return PermissionLevel.FULL.getCode().equals(permissionLevel) || 
               PermissionLevel.RESTRICTED.getCode().equals(permissionLevel);
    }
}
