package com.example.common.constants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 错误码可访问性测试
 * 验证所有错误码常量都可以正常访问，无编译错误
 */
@DisplayName("错误码可访问性测试")
class ErrorCodeAccessibilityTest {

    @Test
    @DisplayName("验证RESOURCE_NOT_FOUND错误码可访问")
    void testResourceNotFoundAccessibility() {
        // 验证常量存在且可访问
        assertNotNull(ErrorCode.RESOURCE_NOT_FOUND);
        assertEquals(404, ErrorCode.RESOURCE_NOT_FOUND.getHttpCode());
        assertEquals("RESOURCE_NOT_FOUND", ErrorCode.RESOURCE_NOT_FOUND.getErrorCode());
        assertEquals("资源不存在", ErrorCode.RESOURCE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("验证OPERATION_FAILED错误码可访问")
    void testOperationFailedAccessibility() {
        // 验证常量存在且可访问
        assertNotNull(ErrorCode.OPERATION_FAILED);
        assertEquals(500, ErrorCode.OPERATION_FAILED.getHttpCode());
        assertEquals("OPERATION_FAILED", ErrorCode.OPERATION_FAILED.getErrorCode());
        assertEquals("操作失败", ErrorCode.OPERATION_FAILED.getMessage());
    }

    @Test
    @DisplayName("验证错误码格式合规性")
    void testErrorCodeFormatCompliance() {
        // 验证RESOURCE_NOT_FOUND格式
        String resourceNotFoundCode = ErrorCode.RESOURCE_NOT_FOUND.getErrorCode();
        assertTrue(resourceNotFoundCode.matches("^[A-Z_]+$"), 
                "错误码应该只包含大写字母和下划线");
        
        // 验证OPERATION_FAILED格式
        String operationFailedCode = ErrorCode.OPERATION_FAILED.getErrorCode();
        assertTrue(operationFailedCode.matches("^[A-Z_]+$"), 
                "错误码应该只包含大写字母和下划线");
    }

    @Test
    @DisplayName("验证HTTP状态码映射正确性")
    void testHttpStatusCodeMapping() {
        // 验证404状态码映射
        assertEquals(404, ErrorCode.RESOURCE_NOT_FOUND.getHttpCode());
        
        // 验证500状态码映射
        assertEquals(500, ErrorCode.OPERATION_FAILED.getHttpCode());
        
        // 验证向后兼容的getCode()方法
        assertEquals(404, ErrorCode.RESOURCE_NOT_FOUND.getCode());
        assertEquals(500, ErrorCode.OPERATION_FAILED.getCode());
    }

    @Test
    @DisplayName("验证错误码枚举查找功能")
    void testErrorCodeLookup() {
        // 测试fromErrorCode方法
        ErrorCode foundResourceNotFound = ErrorCode.fromErrorCode("RESOURCE_NOT_FOUND");
        assertNotNull(foundResourceNotFound);
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, foundResourceNotFound);
        
        ErrorCode foundOperationFailed = ErrorCode.fromErrorCode("OPERATION_FAILED");
        assertNotNull(foundOperationFailed);
        assertEquals(ErrorCode.OPERATION_FAILED, foundOperationFailed);
    }

    @Test
    @DisplayName("验证toString方法正常工作")
    void testToStringMethod() {
        String resourceNotFoundString = ErrorCode.RESOURCE_NOT_FOUND.toString();
        assertTrue(resourceNotFoundString.contains("RESOURCE_NOT_FOUND"));
        assertTrue(resourceNotFoundString.contains("404"));
        assertTrue(resourceNotFoundString.contains("资源不存在"));
        
        String operationFailedString = ErrorCode.OPERATION_FAILED.toString();
        assertTrue(operationFailedString.contains("OPERATION_FAILED"));
        assertTrue(operationFailedString.contains("500"));
        assertTrue(operationFailedString.contains("操作失败"));
    }
}
