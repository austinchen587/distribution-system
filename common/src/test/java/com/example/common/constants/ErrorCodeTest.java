package com.example.common.constants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("统一错误码枚举单元测试")
class ErrorCodeTest {

    @Test
    @DisplayName("测试HTTP标准状态码")
    void testHttpStandardCodes() {
        assertEquals(200, ErrorCode.SUCCESS.getHttpCode());
        assertEquals("SUCCESS", ErrorCode.SUCCESS.getErrorCode());
        assertEquals("操作成功", ErrorCode.SUCCESS.getMessage());
        
        assertEquals(400, ErrorCode.BAD_REQUEST.getHttpCode());
        assertEquals("BAD_REQUEST", ErrorCode.BAD_REQUEST.getErrorCode());
        assertEquals("请求参数错误", ErrorCode.BAD_REQUEST.getMessage());
        
        assertEquals(401, ErrorCode.UNAUTHORIZED.getHttpCode());
        assertEquals("UNAUTHORIZED", ErrorCode.UNAUTHORIZED.getErrorCode());
        assertEquals("未授权访问", ErrorCode.UNAUTHORIZED.getMessage());
        
        assertEquals(403, ErrorCode.FORBIDDEN.getHttpCode());
        assertEquals("FORBIDDEN", ErrorCode.FORBIDDEN.getErrorCode());
        assertEquals("权限不足", ErrorCode.FORBIDDEN.getMessage());
        
        assertEquals(404, ErrorCode.NOT_FOUND.getHttpCode());
        assertEquals("NOT_FOUND", ErrorCode.NOT_FOUND.getErrorCode());
        assertEquals("资源不存在", ErrorCode.NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("测试业务错误码格式")
    void testBusinessErrorCodeFormat() {
        // 测试用户管理错误码
        assertEquals(404, ErrorCode.USER_001.getHttpCode());
        assertEquals("USER_001", ErrorCode.USER_001.getErrorCode());
        assertEquals("用户不存在", ErrorCode.USER_001.getMessage());
        
        assertEquals(409, ErrorCode.USER_002.getHttpCode());
        assertEquals("USER_002", ErrorCode.USER_002.getErrorCode());
        assertEquals("用户名已存在", ErrorCode.USER_002.getMessage());
        
        // 测试认证错误码
        assertEquals(401, ErrorCode.AUTH_001.getHttpCode());
        assertEquals("AUTH_001", ErrorCode.AUTH_001.getErrorCode());
        assertEquals("用户名或密码错误", ErrorCode.AUTH_001.getMessage());
        
        assertEquals(401, ErrorCode.AUTH_004.getHttpCode());
        assertEquals("AUTH_004", ErrorCode.AUTH_004.getErrorCode());
        assertEquals("Token已过期", ErrorCode.AUTH_004.getMessage());
        
        // 测试客资管理错误码
        assertEquals(404, ErrorCode.LEAD_001.getHttpCode());
        assertEquals("LEAD_001", ErrorCode.LEAD_001.getErrorCode());
        assertEquals("客资不存在", ErrorCode.LEAD_001.getMessage());
        
        assertEquals(409, ErrorCode.LEAD_002.getHttpCode());
        assertEquals("LEAD_002", ErrorCode.LEAD_002.getErrorCode());
        assertEquals("手机号已存在", ErrorCode.LEAD_002.getMessage());
    }

    @Test
    @DisplayName("测试推广管理错误码")
    void testPromotionErrorCodes() {
        assertEquals(404, ErrorCode.PROMOTION_001.getHttpCode());
        assertEquals("PROMOTION_001", ErrorCode.PROMOTION_001.getErrorCode());
        assertEquals("推广任务不存在", ErrorCode.PROMOTION_001.getMessage());
        
        assertEquals(403, ErrorCode.PROMOTION_002.getHttpCode());
        assertEquals("PROMOTION_002", ErrorCode.PROMOTION_002.getErrorCode());
        assertEquals("没有审核权限", ErrorCode.PROMOTION_002.getMessage());
        
        assertEquals(422, ErrorCode.PROMOTION_015.getHttpCode());
        assertEquals("PROMOTION_015", ErrorCode.PROMOTION_015.getErrorCode());
        assertEquals("URL无法识别或不支持", ErrorCode.PROMOTION_015.getMessage());
    }

    @Test
    @DisplayName("测试向后兼容性")
    void testBackwardCompatibility() {
        // 测试getCode()方法的向后兼容性
        assertEquals(200, ErrorCode.SUCCESS.getCode());
        assertEquals(404, ErrorCode.USER_001.getCode());
        assertEquals(401, ErrorCode.AUTH_004.getCode());
    }

    @Test
    @DisplayName("测试错误码查找方法")
    void testErrorCodeLookup() {
        // 测试根据业务错误码查找
        assertEquals(ErrorCode.USER_001, ErrorCode.fromErrorCode("USER_001"));
        assertEquals(ErrorCode.AUTH_004, ErrorCode.fromErrorCode("AUTH_004"));
        assertEquals(ErrorCode.PROMOTION_001, ErrorCode.fromErrorCode("PROMOTION_001"));
        assertNull(ErrorCode.fromErrorCode("INVALID_CODE"));
        
        // 测试根据HTTP状态码查找
        assertEquals(ErrorCode.SUCCESS, ErrorCode.fromHttpCode(200));
        assertEquals(ErrorCode.BAD_REQUEST, ErrorCode.fromHttpCode(400));
        assertEquals(ErrorCode.NOT_FOUND, ErrorCode.fromHttpCode(404));
    }

    @Test
    @DisplayName("测试错误码toString方法")
    void testToString() {
        assertEquals("SUCCESS(200): 操作成功", ErrorCode.SUCCESS.toString());
        assertEquals("USER_001(404): 用户不存在", ErrorCode.USER_001.toString());
        assertEquals("AUTH_004(401): Token已过期", ErrorCode.AUTH_004.toString());
        assertEquals("PROMOTION_002(403): 没有审核权限", ErrorCode.PROMOTION_002.toString());
    }

    @Test
    @DisplayName("测试错误码枚举值转换")
    void testEnumValueOf() {
        assertEquals(ErrorCode.SUCCESS, ErrorCode.valueOf("SUCCESS"));
        assertEquals(ErrorCode.USER_001, ErrorCode.valueOf("USER_001"));
        assertEquals(ErrorCode.AUTH_004, ErrorCode.valueOf("AUTH_004"));
        assertEquals(ErrorCode.PROMOTION_001, ErrorCode.valueOf("PROMOTION_001"));
    }

    @Test
    @DisplayName("测试无效错误码值处理")
    void testInvalidErrorCode() {
        assertThrows(IllegalArgumentException.class, () -> {
            ErrorCode.valueOf("INVALID_CODE");
        });
    }

    @Test
    @DisplayName("测试各模块错误码覆盖")
    void testModuleErrorCodes() {
        // 通用错误码
        assertNotNull(ErrorCode.COMMON_001);
        assertEquals("系统维护中", ErrorCode.COMMON_001.getMessage());
        
        // 代理管理错误码
        assertNotNull(ErrorCode.AGENT_001);
        assertEquals("代理不存在", ErrorCode.AGENT_001.getMessage());
        
        // 邀请系统错误码
        assertNotNull(ErrorCode.INVITE_001);
        assertEquals("邀请码无效或不存在", ErrorCode.INVITE_001.getMessage());
        
        // 奖励系统错误码
        assertNotNull(ErrorCode.REWARD_001);
        assertEquals("提交次数已达每日限制", ErrorCode.REWARD_001.getMessage());
        
        // 系统配置错误码
        assertNotNull(ErrorCode.CONFIG_001);
        assertEquals("配置不存在", ErrorCode.CONFIG_001.getMessage());
        
        // 仪表盘错误码
        assertNotNull(ErrorCode.DASHBOARD_001);
        assertEquals("数据不存在", ErrorCode.DASHBOARD_001.getMessage());
    }

    @Test
    @DisplayName("测试错误码比较")
    void testErrorCodeComparison() {
        assertNotEquals(ErrorCode.SUCCESS, ErrorCode.BAD_REQUEST);
        assertEquals(ErrorCode.SUCCESS, ErrorCode.SUCCESS);
        assertTrue(ErrorCode.SUCCESS.getHttpCode() < ErrorCode.BAD_REQUEST.getHttpCode());
    }
}