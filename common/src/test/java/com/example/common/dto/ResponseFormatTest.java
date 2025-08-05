package com.example.common.dto;

import com.example.common.constants.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 响应格式综合测试
 * 验证新旧错误响应格式的兼容性和正确性
 */
@DisplayName("响应格式综合测试")
class ResponseFormatTest {

    @Test
    @DisplayName("验证新格式错误响应 - error_code字段存在")
    void testNewFormatErrorResponse() {
        // 测试新格式的错误响应
        CommonResult<Map<String, Object>> result = CommonResult.error(ErrorCode.UNAUTHORIZED);
        
        assertNotNull(result);
        assertEquals(401, result.getCode());
        assertEquals(false, result.getSuccess());
        assertEquals("未授权访问", result.getMessage());
        
        // 验证data字段包含error_code
        assertNotNull(result.getData());
        assertTrue(result.getData() instanceof Map);
        Map<String, Object> data = result.getData();
        assertTrue(data.containsKey("error_code"));
        assertEquals("UNAUTHORIZED", data.get("error_code"));
    }

    @Test
    @DisplayName("验证新格式错误响应 - 自定义消息")
    void testNewFormatErrorResponseWithCustomMessage() {
        String customMessage = "自定义错误消息";
        CommonResult<Map<String, Object>> result = CommonResult.error(ErrorCode.FORBIDDEN, customMessage);
        
        assertNotNull(result);
        assertEquals(403, result.getCode());
        assertEquals(false, result.getSuccess());
        assertEquals(customMessage, result.getMessage());
        
        // 验证data字段包含error_code
        assertNotNull(result.getData());
        Map<String, Object> data = result.getData();
        assertTrue(data.containsKey("error_code"));
        assertEquals("FORBIDDEN", data.get("error_code"));
    }

    @Test
    @DisplayName("验证向后兼容的错误响应格式")
    void testBackwardCompatibleErrorResponse() {
        // 测试向后兼容的方法
        CommonResult<String> unauthorizedResult = CommonResult.unauthorized();
        CommonResult<String> forbiddenResult = CommonResult.forbidden();
        CommonResult<String> notFoundResult = CommonResult.notFound();
        
        // 验证unauthorized
        assertNotNull(unauthorizedResult);
        assertEquals(401, unauthorizedResult.getCode());
        assertEquals(false, unauthorizedResult.getSuccess());
        assertEquals("未授权访问", unauthorizedResult.getMessage());
        assertNull(unauthorizedResult.getData());
        
        // 验证forbidden
        assertNotNull(forbiddenResult);
        assertEquals(403, forbiddenResult.getCode());
        assertEquals(false, forbiddenResult.getSuccess());
        assertEquals("权限不足", forbiddenResult.getMessage());
        assertNull(forbiddenResult.getData());
        
        // 验证notFound
        assertNotNull(notFoundResult);
        assertEquals(404, notFoundResult.getCode());
        assertEquals(false, notFoundResult.getSuccess());
        assertEquals("资源不存在", notFoundResult.getMessage());
        assertNull(notFoundResult.getData());
    }

    @Test
    @DisplayName("验证新格式便捷方法")
    void testNewFormatConvenienceMethods() {
        // 测试新格式的便捷方法
        CommonResult<Map<String, Object>> unauthorizedResult = CommonResult.unauthorizedWithErrorCode();
        CommonResult<Map<String, Object>> forbiddenResult = CommonResult.forbiddenWithErrorCode();
        CommonResult<Map<String, Object>> notFoundResult = CommonResult.notFoundWithErrorCode();
        
        // 验证unauthorizedWithErrorCode
        assertNotNull(unauthorizedResult);
        assertEquals(401, unauthorizedResult.getCode());
        assertEquals(false, unauthorizedResult.getSuccess());
        assertEquals("未授权访问", unauthorizedResult.getMessage());
        assertNotNull(unauthorizedResult.getData());
        assertEquals("UNAUTHORIZED", unauthorizedResult.getData().get("error_code"));
        
        // 验证forbiddenWithErrorCode
        assertNotNull(forbiddenResult);
        assertEquals(403, forbiddenResult.getCode());
        assertEquals(false, forbiddenResult.getSuccess());
        assertEquals("权限不足", forbiddenResult.getMessage());
        assertNotNull(forbiddenResult.getData());
        assertEquals("FORBIDDEN", forbiddenResult.getData().get("error_code"));
        
        // 验证notFoundWithErrorCode
        assertNotNull(notFoundResult);
        assertEquals(404, notFoundResult.getCode());
        assertEquals(false, notFoundResult.getSuccess());
        assertEquals("资源不存在", notFoundResult.getMessage());
        assertNotNull(notFoundResult.getData());
        assertEquals("NOT_FOUND", notFoundResult.getData().get("error_code"));
    }

    @Test
    @DisplayName("验证成功响应格式")
    void testSuccessResponseFormat() {
        // 测试成功响应
        CommonResult<String> successResult = CommonResult.success("测试数据");
        
        assertNotNull(successResult);
        assertEquals(200, successResult.getCode());
        assertEquals(true, successResult.getSuccess());
        assertEquals("操作成功", successResult.getMessage());
        assertEquals("测试数据", successResult.getData());
        assertNotNull(successResult.getTimestamp());
    }

    @Test
    @DisplayName("验证时间戳字段")
    void testTimestampField() {
        long beforeTime = System.currentTimeMillis();
        CommonResult<String> result = CommonResult.success();
        long afterTime = System.currentTimeMillis();
        
        assertNotNull(result.getTimestamp());
        assertTrue(result.getTimestamp() >= beforeTime);
        assertTrue(result.getTimestamp() <= afterTime);
    }

    @Test
    @DisplayName("验证错误码格式合规性")
    void testErrorCodeFormatCompliance() {
        // 测试新添加的错误码
        CommonResult<Map<String, Object>> resourceNotFoundResult = CommonResult.error(ErrorCode.RESOURCE_NOT_FOUND);
        CommonResult<Map<String, Object>> operationFailedResult = CommonResult.error(ErrorCode.OPERATION_FAILED);
        
        // 验证RESOURCE_NOT_FOUND
        assertNotNull(resourceNotFoundResult);
        assertEquals(404, resourceNotFoundResult.getCode());
        assertEquals("资源不存在", resourceNotFoundResult.getMessage());
        assertEquals("RESOURCE_NOT_FOUND", resourceNotFoundResult.getData().get("error_code"));
        
        // 验证OPERATION_FAILED
        assertNotNull(operationFailedResult);
        assertEquals(500, operationFailedResult.getCode());
        assertEquals("操作失败", operationFailedResult.getMessage());
        assertEquals("OPERATION_FAILED", operationFailedResult.getData().get("error_code"));
    }

    @Test
    @DisplayName("验证HTTP状态码映射正确性")
    void testHttpStatusCodeMapping() {
        // 验证各种错误码的HTTP状态码映射
        assertEquals(401, CommonResult.error(ErrorCode.UNAUTHORIZED).getCode());
        assertEquals(403, CommonResult.error(ErrorCode.FORBIDDEN).getCode());
        assertEquals(404, CommonResult.error(ErrorCode.NOT_FOUND).getCode());
        assertEquals(404, CommonResult.error(ErrorCode.RESOURCE_NOT_FOUND).getCode());
        assertEquals(500, CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR).getCode());
        assertEquals(500, CommonResult.error(ErrorCode.OPERATION_FAILED).getCode());
    }

    @Test
    @DisplayName("验证响应结构完整性")
    void testResponseStructureCompleteness() {
        CommonResult<Map<String, Object>> result = CommonResult.error(ErrorCode.BAD_REQUEST);
        
        // 验证所有必需字段都存在
        assertNotNull(result.getCode(), "code字段不能为空");
        assertNotNull(result.getSuccess(), "success字段不能为空");
        assertNotNull(result.getMessage(), "message字段不能为空");
        assertNotNull(result.getData(), "data字段不能为空");
        assertNotNull(result.getTimestamp(), "timestamp字段不能为空");
        
        // 验证字段类型正确
        assertTrue(result.getCode() instanceof Integer, "code应该是Integer类型");
        assertTrue(result.getSuccess() instanceof Boolean, "success应该是Boolean类型");
        assertTrue(result.getMessage() instanceof String, "message应该是String类型");
        assertTrue(result.getData() instanceof Map, "data应该是Map类型");
        assertTrue(result.getTimestamp() instanceof Long, "timestamp应该是Long类型");
    }

    @Test
    @DisplayName("验证错误响应data字段结构")
    void testErrorResponseDataStructure() {
        CommonResult<Map<String, Object>> result = CommonResult.error(ErrorCode.UNAUTHORIZED);
        Map<String, Object> data = result.getData();
        
        // 验证data字段结构
        assertNotNull(data);
        assertTrue(data.containsKey("error_code"));
        assertEquals(1, data.size(), "data字段应该只包含error_code");
        
        // 验证error_code值的类型和格式
        Object errorCode = data.get("error_code");
        assertTrue(errorCode instanceof String, "error_code应该是String类型");
        String errorCodeStr = (String) errorCode;
        assertTrue(errorCodeStr.matches("^[A-Z_]+$"), "error_code应该只包含大写字母和下划线");
    }
}
