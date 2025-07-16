package com.example.common.constants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("错误码枚举单元测试")
class ErrorCodeTest {

    @Test
    @DisplayName("测试所有错误码值")
    void testAllErrorCodes() {
        assertEquals(200, ErrorCode.SUCCESS.getCode());
        assertEquals(400, ErrorCode.BAD_REQUEST.getCode());
        assertEquals(401, ErrorCode.UNAUTHORIZED.getCode());
        assertEquals(403, ErrorCode.FORBIDDEN.getCode());
        assertEquals(404, ErrorCode.RESOURCE_NOT_FOUND.getCode());
        assertEquals(409, ErrorCode.CONFLICT.getCode());
        assertEquals(422, ErrorCode.UNPROCESSABLE_ENTITY.getCode());
        assertEquals(500, ErrorCode.INTERNAL_SERVER_ERROR.getCode());
        assertEquals(400, ErrorCode.PARAM_ERROR.getCode());
        assertEquals(400, ErrorCode.VALIDATION_ERROR.getCode());
        assertEquals(500, ErrorCode.OPERATION_FAILED.getCode());
        assertEquals(404, ErrorCode.USER_NOT_FOUND.getCode());
        assertEquals(401, ErrorCode.INVALID_CREDENTIALS.getCode());
        assertEquals(409, ErrorCode.USER_ALREADY_EXISTS.getCode());
        assertEquals(409, ErrorCode.LEAD_ALREADY_EXISTS.getCode());
        assertEquals(429, ErrorCode.OPERATION_TOO_FREQUENT.getCode());
    }

    @Test
    @DisplayName("测试错误码消息")
    void testErrorCodeMessages() {
        assertEquals("操作成功", ErrorCode.SUCCESS.getMessage());
        assertEquals("请求参数错误", ErrorCode.BAD_REQUEST.getMessage());
        assertEquals("未授权访问", ErrorCode.UNAUTHORIZED.getMessage());
        assertEquals("权限不足", ErrorCode.FORBIDDEN.getMessage());
        assertEquals("资源不存在", ErrorCode.RESOURCE_NOT_FOUND.getMessage());
        assertEquals("资源冲突", ErrorCode.CONFLICT.getMessage());
        assertEquals("参数校验失败", ErrorCode.UNPROCESSABLE_ENTITY.getMessage());
        assertEquals("系统内部错误", ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }

    @Test
    @DisplayName("测试错误码唯一性")
    void testErrorCodeUniqueness() {
        int[] codes = {
            ErrorCode.SUCCESS.getCode(),
            ErrorCode.BAD_REQUEST.getCode(),
            ErrorCode.UNAUTHORIZED.getCode(),
            ErrorCode.FORBIDDEN.getCode(),
            ErrorCode.RESOURCE_NOT_FOUND.getCode(),
            ErrorCode.CONFLICT.getCode(),
            ErrorCode.UNPROCESSABLE_ENTITY.getCode(),
            ErrorCode.INTERNAL_SERVER_ERROR.getCode()
        };

        // 检查是否有重复的错误码
        for (int i = 0; i < codes.length; i++) {
            for (int j = i + 1; j < codes.length; j++) {
                if (i != j) {
                    assertNotEquals(codes[i], codes[j]);
                }
            }
        }
    }

    @Test
    @DisplayName("测试错误码枚举值转换")
    void testEnumValueOf() {
        assertEquals(ErrorCode.SUCCESS, ErrorCode.valueOf("SUCCESS"));
        assertEquals(ErrorCode.BAD_REQUEST, ErrorCode.valueOf("BAD_REQUEST"));
        assertEquals(ErrorCode.UNAUTHORIZED, ErrorCode.valueOf("UNAUTHORIZED"));
        assertEquals(ErrorCode.FORBIDDEN, ErrorCode.valueOf("FORBIDDEN"));
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, ErrorCode.valueOf("RESOURCE_NOT_FOUND"));
    }

    @Test
    @DisplayName("测试无效错误码值处理")
    void testInvalidErrorCode() {
        assertThrows(IllegalArgumentException.class, () -> {
            ErrorCode.valueOf("INVALID_CODE");
        });
    }

    @Test
    @DisplayName("测试错误码toString方法")
    void testToString() {
        assertEquals("200: 操作成功", ErrorCode.SUCCESS.toString());
        assertEquals("400: 请求参数错误", ErrorCode.BAD_REQUEST.toString());
        assertEquals("500: 系统内部错误", ErrorCode.INTERNAL_SERVER_ERROR.toString());
    }

    @Test
    @DisplayName("测试错误码比较")
    void testErrorCodeComparison() {
        assertNotEquals(ErrorCode.SUCCESS, ErrorCode.BAD_REQUEST);
        assertEquals(ErrorCode.SUCCESS, ErrorCode.SUCCESS);
        assertTrue(ErrorCode.SUCCESS.getCode() < ErrorCode.BAD_REQUEST.getCode());
    }

    @Test
    @DisplayName("测试业务相关错误码")
    void testBusinessRelatedErrorCodes() {
        assertEquals(404, ErrorCode.USER_NOT_FOUND.getCode());
        assertEquals("用户不存在", ErrorCode.USER_NOT_FOUND.getMessage());
        
        assertEquals(401, ErrorCode.INVALID_CREDENTIALS.getCode());
        assertEquals("用户名或密码错误", ErrorCode.INVALID_CREDENTIALS.getMessage());
        
        assertEquals(409, ErrorCode.USER_ALREADY_EXISTS.getCode());
        assertEquals("用户已存在", ErrorCode.USER_ALREADY_EXISTS.getMessage());
        
        assertEquals(409, ErrorCode.LEAD_ALREADY_EXISTS.getCode());
        assertEquals("客资已存在", ErrorCode.LEAD_ALREADY_EXISTS.getMessage());
    }
}