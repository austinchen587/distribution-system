package com.example.common.constants;

public class RedisKeys {
    
    public static final String USER_TOKEN_PREFIX = "user:token:";
    
    public static final String SMS_CODE_PREFIX = "sms:code:";
    
    public static final String USER_CACHE_PREFIX = "user:cache:";
    
    public static final String AGENT_LEVEL_CACHE_PREFIX = "agent:level:";
    
    public static final String USER_SESSION_PREFIX = "user:session:";
    
    public static final String VERIFY_CODE_PREFIX = "verify:code:";
    
    public static final String PRODUCT_CACHE_PREFIX = "product:cache:";
    
    public static final String USER_PERMISSION_PREFIX = "user:permission:";
    
    private RedisKeys() {
    }
    
    public static String getUserTokenKey(String userId) {
        return USER_TOKEN_PREFIX + userId;
    }
    
    public static String getSmsCodeKey(String phone) {
        return SMS_CODE_PREFIX + phone;
    }
    
    public static String getUserCacheKey(String userId) {
        return USER_CACHE_PREFIX + userId;
    }
    
    public static String getAgentLevelCacheKey(String userId) {
        return AGENT_LEVEL_CACHE_PREFIX + userId;
    }
    
    public static String getUserSessionKey(Long userId) {
        return USER_SESSION_PREFIX + userId;
    }
    
    public static String getVerifyCodeKey(String phone) {
        return VERIFY_CODE_PREFIX + phone;
    }
    
    public static String getProductCacheKey(Long productId) {
        return PRODUCT_CACHE_PREFIX + productId;
    }
    
    public static String getUserPermissionKey(Long userId) {
        return USER_PERMISSION_PREFIX + userId;
    }
}