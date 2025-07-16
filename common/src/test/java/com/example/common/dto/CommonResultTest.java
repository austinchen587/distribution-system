package com.example.common.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CommonResult 单元测试")
class CommonResultTest {

    @Test
    @DisplayName("测试成功响应 - 无数据")
    void testSuccessWithoutData() {
        CommonResult<String> result = CommonResult.success();
        
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData());
        assertNotNull(result.getTimestamp());
        assertTrue(result.getTimestamp() > 0);
    }

    @Test
    @DisplayName("测试成功响应 - 有数据")
    void testSuccessWithData() {
        String testData = "test data";
        CommonResult<String> result = CommonResult.success(testData);
        
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(testData, result.getData());
        assertNotNull(result.getTimestamp());
    }

    @Test
    @DisplayName("测试成功响应 - 自定义消息和数据")
    void testSuccessWithMessageAndData() {
        String message = "操作成功完成";
        String data = "自定义数据";
        CommonResult<String> result = CommonResult.success(message, data);
        
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(message, result.getMessage());
        assertEquals(data, result.getData());
    }

    @Test
    @DisplayName("测试错误响应 - 自定义代码和消息")
    void testErrorWithCodeAndMessage() {
        Integer code = 400;
        String message = "参数错误";
        CommonResult<String> result = CommonResult.error(code, message);
        
        assertNotNull(result);
        assertEquals(code, result.getCode());
        assertEquals(message, result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("测试错误响应 - 仅消息")
    void testErrorWithMessage() {
        String message = "系统错误";
        CommonResult<String> result = CommonResult.error(message);
        
        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(message, result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("测试未授权响应")
    void testUnauthorized() {
        CommonResult<String> result = CommonResult.unauthorized();
        
        assertNotNull(result);
        assertEquals(401, result.getCode());
        assertEquals("未授权访问", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("测试权限不足响应")
    void testForbidden() {
        CommonResult<String> result = CommonResult.forbidden();
        
        assertNotNull(result);
        assertEquals(403, result.getCode());
        assertEquals("权限不足", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("测试资源不存在响应")
    void testNotFound() {
        CommonResult<String> result = CommonResult.notFound();
        
        assertNotNull(result);
        assertEquals(404, result.getCode());
        assertEquals("资源不存在", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("测试参数错误响应")
    void testBadRequest() {
        String message = "参数格式错误";
        CommonResult<String> result = CommonResult.badRequest(message);
        
        assertNotNull(result);
        assertEquals(400, result.getCode());
        assertEquals(message, result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("测试构造方法")
    void testConstructor() {
        Integer code = 200;
        String message = "成功";
        String data = "测试数据";
        
        CommonResult<String> result = new CommonResult<>(code, message, data);
        
        assertEquals(code, result.getCode());
        assertEquals(message, result.getMessage());
        assertEquals(data, result.getData());
        assertNotNull(result.getTimestamp());
    }

    @Test
    @DisplayName("测试默认构造方法")
    void testDefaultConstructor() {
        CommonResult<String> result = new CommonResult<>();
        
        assertNull(result.getCode());
        assertNull(result.getMessage());
        assertNull(result.getData());
        assertNotNull(result.getTimestamp());
    }

    @Test
    @DisplayName("测试setter和getter方法")
    void testSettersAndGetters() {
        CommonResult<String> result = new CommonResult<>();
        
        result.setCode(201);
        result.setMessage("创建成功");
        result.setData("新数据");
        result.setTimestamp(123456789L);
        
        assertEquals(201, result.getCode());
        assertEquals("创建成功", result.getMessage());
        assertEquals("新数据", result.getData());
        assertEquals(123456789L, result.getTimestamp());
    }

    @Test
    @DisplayName("测试toString方法")
    void testToString() {
        CommonResult<String> result = CommonResult.success("测试数据");
        String resultString = result.toString();
        
        assertNotNull(resultString);
        assertTrue(resultString.contains("CommonResult"));
        assertTrue(resultString.contains("code=200"));
        assertTrue(resultString.contains("message=操作成功"));
        assertTrue(resultString.contains("data=测试数据"));
    }

    @Test
    @DisplayName("测试泛型支持")
    void testGenericSupport() {
        // 测试不同的数据类型
        CommonResult<Integer> intResult = CommonResult.success(123);
        assertEquals(123, intResult.getData());
        
        CommonResult<Boolean> boolResult = CommonResult.success(true);
        assertEquals(true, boolResult.getData());
        
        CommonResult<Object> objectResult = CommonResult.success(new Object());
        assertNotNull(objectResult.getData());
    }
}