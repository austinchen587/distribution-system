package com.example.common.constants;

public class RedisKeys {
    
    public static final String USER_TOKEN_PREFIX = "user:token:";
    
    public static final String SMS_CODE_PREFIX = "sms:code:";
    
    public static final String USER_CACHE_PREFIX = "user:cache:";
    
    public static final String AGENT_LEVEL_CACHE_PREFIX = "agent:level:";
    
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
}