package com.example.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 权限缓存管理器
 * 
 * <p>专门负责权限相关数据的Redis缓存管理，提供统一的缓存操作接口。
 * 该组件封装了权限缓存的具体实现细节，提供了缓存的读写、清理、统计等功能。
 * 
 * <p>主要功能：
 * <ul>
 *   <li>权限配置的缓存读写操作</li>
 *   <li>缓存的批量清理和模式匹配删除</li>
 *   <li>缓存统计和监控功能</li>
 *   <li>缓存降级和故障恢复机制</li>
 * </ul>
 * 
 * <p>缓存策略：
 * <ul>
 *   <li>权限缓存TTL：1小时</li>
 *   <li>缓存键命名规范：perm:{serviceName}:{tableName}:{operationType}</li>
 *   <li>缓存值格式：权限级别代码（FULL/RESTRICTED/DENIED）</li>
 *   <li>支持缓存预热和批量操作</li>
 * </ul>
 * 
 * @author Edom
 * @date 2025-08-01
 * @since 1.0.0
 */
@Component
public class PermissionCacheManager {

    private static final Logger log = LoggerFactory.getLogger(PermissionCacheManager.class);
    private static final String PERMISSION_CACHE_PREFIX = "perm:";
    private static final String CACHE_STATS_KEY = "perm:stats";
    private static final long DEFAULT_TTL_HOURS = 1;

    @Autowired
    private StringRedisTemplate redisTemplate;
    
    /**
     * 获取权限缓存
     * 
     * @param cacheKey 缓存键
     * @return 权限级别，如果不存在返回null
     */
    public String getPermission(String cacheKey) {
        try {
            String permission = redisTemplate.opsForValue().get(cacheKey);
            if (permission != null) {
                // 记录缓存命中
                incrementCacheHit();
                log.debug("缓存命中: {} -> {}", cacheKey, permission);
            } else {
                // 记录缓存未命中
                incrementCacheMiss();
                log.debug("缓存未命中: {}", cacheKey);
            }
            return permission;
        } catch (Exception e) {
            log.warn("获取权限缓存失败: cacheKey={}", cacheKey, e);
            incrementCacheMiss();
            return null;
        }
    }
    
    /**
     * 缓存权限配置
     * 
     * @param cacheKey 缓存键
     * @param permissionLevel 权限级别
     */
    public void cachePermission(String cacheKey, String permissionLevel) {
        cachePermission(cacheKey, permissionLevel, DEFAULT_TTL_HOURS, TimeUnit.HOURS);
    }
    
    /**
     * 缓存权限配置（指定TTL）
     * 
     * @param cacheKey 缓存键
     * @param permissionLevel 权限级别
     * @param timeout 过期时间
     * @param timeUnit 时间单位
     */
    public void cachePermission(String cacheKey, String permissionLevel, long timeout, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(cacheKey, permissionLevel, timeout, timeUnit);
            log.debug("缓存权限配置: {} -> {} (TTL: {} {})", cacheKey, permissionLevel, timeout, timeUnit);
        } catch (Exception e) {
            log.warn("缓存权限配置失败: cacheKey={}, permissionLevel={}", cacheKey, permissionLevel, e);
        }
    }
    
    /**
     * 删除指定的权限缓存
     * 
     * @param cacheKey 缓存键
     * @return 是否删除成功
     */
    public boolean deletePermission(String cacheKey) {
        try {
            Boolean deleted = redisTemplate.delete(cacheKey);
            log.debug("删除权限缓存: {} -> {}", cacheKey, deleted);
            return Boolean.TRUE.equals(deleted);
        } catch (Exception e) {
            log.warn("删除权限缓存失败: cacheKey={}", cacheKey, e);
            return false;
        }
    }
    
    /**
     * 批量删除权限缓存
     * 
     * @param pattern 缓存键模式（支持通配符）
     * @return 删除的缓存数量
     */
    public long deletePermissionsByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                Long deleted = redisTemplate.delete(keys);
                log.info("批量删除权限缓存: pattern={}, count={}", pattern, deleted);
                return deleted != null ? deleted : 0;
            }
            return 0;
        } catch (Exception e) {
            log.warn("批量删除权限缓存失败: pattern={}", pattern, e);
            return 0;
        }
    }
    
    /**
     * 清除指定服务的所有权限缓存
     * 
     * @param serviceName 微服务名称
     * @return 删除的缓存数量
     */
    public long clearServiceCache(String serviceName) {
        String pattern = PERMISSION_CACHE_PREFIX + serviceName + ":*";
        return deletePermissionsByPattern(pattern);
    }
    
    /**
     * 清除所有权限缓存
     * 
     * @return 删除的缓存数量
     */
    public long clearAllPermissionCache() {
        String pattern = PERMISSION_CACHE_PREFIX + "*";
        return deletePermissionsByPattern(pattern);
    }
    
    /**
     * 检查缓存是否存在
     * 
     * @param cacheKey 缓存键
     * @return 是否存在
     */
    public boolean existsPermission(String cacheKey) {
        try {
            Boolean exists = redisTemplate.hasKey(cacheKey);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.warn("检查权限缓存存在性失败: cacheKey={}", cacheKey, e);
            return false;
        }
    }
    
    /**
     * 获取缓存的剩余TTL
     * 
     * @param cacheKey 缓存键
     * @return 剩余TTL（秒），-1表示永不过期，-2表示不存在
     */
    public long getPermissionTTL(String cacheKey) {
        try {
            Long ttl = redisTemplate.getExpire(cacheKey, TimeUnit.SECONDS);
            return ttl != null ? ttl : -2;
        } catch (Exception e) {
            log.warn("获取权限缓存TTL失败: cacheKey={}", cacheKey, e);
            return -2;
        }
    }
    
    /**
     * 获取权限缓存统计信息
     * 
     * @return 缓存统计信息
     */
    public CacheStats getCacheStats() {
        try {
            Object hitCountObj = redisTemplate.opsForHash().get(CACHE_STATS_KEY, "hit");
            Object missCountObj = redisTemplate.opsForHash().get(CACHE_STATS_KEY, "miss");

            long hitCount = hitCountObj != null ? Long.parseLong(hitCountObj.toString()) : 0;
            long missCount = missCountObj != null ? Long.parseLong(missCountObj.toString()) : 0;
            
            return new CacheStats(hitCount, missCount);
        } catch (Exception e) {
            log.warn("获取缓存统计信息失败", e);
            return new CacheStats(0, 0);
        }
    }
    
    /**
     * 重置缓存统计信息
     */
    public void resetCacheStats() {
        try {
            redisTemplate.delete(CACHE_STATS_KEY);
            log.info("重置权限缓存统计信息");
        } catch (Exception e) {
            log.warn("重置缓存统计信息失败", e);
        }
    }
    
    /**
     * 增加缓存命中计数
     */
    private void incrementCacheHit() {
        try {
            redisTemplate.opsForHash().increment(CACHE_STATS_KEY, "hit", 1);
        } catch (Exception e) {
            log.debug("增加缓存命中计数失败", e);
        }
    }
    
    /**
     * 增加缓存未命中计数
     */
    private void incrementCacheMiss() {
        try {
            redisTemplate.opsForHash().increment(CACHE_STATS_KEY, "miss", 1);
        } catch (Exception e) {
            log.debug("增加缓存未命中计数失败", e);
        }
    }
    
    /**
     * 缓存统计信息
     */
    public static class CacheStats {
        private final long hitCount;
        private final long missCount;
        
        public CacheStats(long hitCount, long missCount) {
            this.hitCount = hitCount;
            this.missCount = missCount;
        }
        
        public long getHitCount() {
            return hitCount;
        }
        
        public long getMissCount() {
            return missCount;
        }
        
        public long getTotalCount() {
            return hitCount + missCount;
        }
        
        public double getHitRate() {
            long total = getTotalCount();
            return total > 0 ? (double) hitCount / total : 0.0;
        }
        
        @Override
        public String toString() {
            return String.format("CacheStats{hit=%d, miss=%d, total=%d, hitRate=%.2f%%}", 
                    hitCount, missCount, getTotalCount(), getHitRate() * 100);
        }
    }
}
