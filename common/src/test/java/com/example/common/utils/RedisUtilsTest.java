package com.example.common.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Redis工具类单元测试")
class RedisUtilsTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private RedisUtils redisUtils;

    private static final String TEST_KEY = "test:key";
    private static final String TEST_VALUE = "test_value";
    private static final long TEST_TIMEOUT = 60L;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("测试设置缓存值并设置过期时间")
    void testSetWithTimeout() {
        redisUtils.set(TEST_KEY, TEST_VALUE, TEST_TIMEOUT, TimeUnit.SECONDS);
        
        verify(valueOperations).set(TEST_KEY, TEST_VALUE, TEST_TIMEOUT, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("测试设置缓存值无过期时间")
    void testSetWithoutTimeout() {
        redisUtils.set(TEST_KEY, TEST_VALUE);
        
        verify(valueOperations).set(TEST_KEY, TEST_VALUE);
    }

    @Test
    @DisplayName("测试获取缓存值")
    void testGet() {
        when(valueOperations.get(TEST_KEY)).thenReturn(TEST_VALUE);
        
        Object result = redisUtils.get(TEST_KEY);
        
        assertEquals(TEST_VALUE, result);
        verify(valueOperations).get(TEST_KEY);
    }

    @Test
    @DisplayName("测试获取不存在的缓存值")
    void testGetNonExistent() {
        when(valueOperations.get(TEST_KEY)).thenReturn(null);
        
        Object result = redisUtils.get(TEST_KEY);
        
        assertNull(result);
        verify(valueOperations).get(TEST_KEY);
    }

    @Test
    @DisplayName("测试删除缓存")
    void testDelete() {
        when(redisTemplate.delete(TEST_KEY)).thenReturn(true);
        
        Boolean result = redisUtils.delete(TEST_KEY);
        
        assertTrue(result);
        verify(redisTemplate).delete(TEST_KEY);
    }

    @Test
    @DisplayName("测试删除不存在的缓存")
    void testDeleteNonExistent() {
        when(redisTemplate.delete(TEST_KEY)).thenReturn(false);
        
        Boolean result = redisUtils.delete(TEST_KEY);
        
        assertFalse(result);
        verify(redisTemplate).delete(TEST_KEY);
    }

    @Test
    @DisplayName("测试检查Key是否存在")
    void testHasKey() {
        when(redisTemplate.hasKey(TEST_KEY)).thenReturn(true);
        
        Boolean result = redisUtils.hasKey(TEST_KEY);
        
        assertTrue(result);
        verify(redisTemplate).hasKey(TEST_KEY);
    }

    @Test
    @DisplayName("测试检查不存在的Key")
    void testHasNonExistentKey() {
        when(redisTemplate.hasKey(TEST_KEY)).thenReturn(false);
        
        Boolean result = redisUtils.hasKey(TEST_KEY);
        
        assertFalse(result);
        verify(redisTemplate).hasKey(TEST_KEY);
    }

    @Test
    @DisplayName("测试设置过期时间")
    void testExpire() {
        when(redisTemplate.expire(TEST_KEY, TEST_TIMEOUT, TimeUnit.SECONDS)).thenReturn(true);
        
        Boolean result = redisUtils.expire(TEST_KEY, TEST_TIMEOUT, TimeUnit.SECONDS);
        
        assertTrue(result);
        verify(redisTemplate).expire(TEST_KEY, TEST_TIMEOUT, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("测试获取剩余过期时间")
    void testGetExpire() {
        long expectedExpire = 30L;
        when(redisTemplate.getExpire(TEST_KEY, TimeUnit.SECONDS)).thenReturn(expectedExpire);
        
        Long result = redisUtils.getExpire(TEST_KEY);
        
        assertEquals(expectedExpire, result);
        verify(redisTemplate).getExpire(TEST_KEY, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("测试不同类型值的缓存")
    void testDifferentValueTypes() {
        // 测试字符串值
        redisUtils.set("string:key", "string_value");
        verify(valueOperations).set("string:key", "string_value");

        // 测试整数值
        redisUtils.set("int:key", 123);
        verify(valueOperations).set("int:key", 123);

        // 测试布尔值
        redisUtils.set("bool:key", true);
        verify(valueOperations).set("bool:key", true);

        // 测试对象值
        Object complexObject = new Object();
        redisUtils.set("object:key", complexObject);
        verify(valueOperations).set("object:key", complexObject);
    }

    @Test
    @DisplayName("测试不同时间单位")
    void testDifferentTimeUnits() {
        redisUtils.set("key1", "value1", 1, TimeUnit.HOURS);
        verify(valueOperations).set("key1", "value1", 1, TimeUnit.HOURS);

        redisUtils.set("key2", "value2", 1, TimeUnit.DAYS);
        verify(valueOperations).set("key2", "value2", 1, TimeUnit.DAYS);

        redisUtils.set("key3", "value3", 1000, TimeUnit.MILLISECONDS);
        verify(valueOperations).set("key3", "value3", 1000, TimeUnit.MILLISECONDS);
    }

    @Test
    @DisplayName("测试空值和空键的处理")
    void testNullValuesAndKeys() {
        // 测试空键
        redisUtils.set(null, TEST_VALUE);
        verify(valueOperations).set(null, TEST_VALUE);

        // 测试空值
        redisUtils.set(TEST_KEY, null);
        verify(valueOperations).set(TEST_KEY, null);

        // 测试空键和空值
        redisUtils.set(null, null);
        verify(valueOperations).set(null, null);
    }
}