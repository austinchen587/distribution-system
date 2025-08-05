package com.example.common.dto;

import com.example.common.constants.ErrorCode;
import java.util.Map;

/**
 * 响应格式验证程序
 * 用于验证新旧响应格式的正确性
 */
public class ResponseFormatVerification {
    
    public static void main(String[] args) {
        System.out.println("开始验证响应格式...");
        
        try {
            // 验证新格式错误响应
            testNewFormatErrorResponse();
            
            // 验证向后兼容性
            testBackwardCompatibility();
            
            // 验证新添加的错误码
            testNewErrorCodes();
            
            System.out.println("\n🎉 所有响应格式验证通过！");
            
        } catch (Exception e) {
            System.err.println("❌ 响应格式验证失败: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void testNewFormatErrorResponse() {
        System.out.println("\n📋 测试新格式错误响应...");
        
        CommonResult<Map<String, Object>> result = CommonResult.error(ErrorCode.UNAUTHORIZED);
        
        assert result != null : "结果不能为空";
        assert result.getCode().equals(401) : "HTTP状态码应该是401";
        assert result.getSuccess().equals(false) : "success应该是false";
        assert "未授权访问".equals(result.getMessage()) : "消息不正确";
        assert result.getData() != null : "data字段不能为空";
        assert result.getData().containsKey("error_code") : "data应该包含error_code字段";
        assert "UNAUTHORIZED".equals(result.getData().get("error_code")) : "error_code值不正确";
        
        System.out.println("✅ 新格式错误响应验证通过");
        System.out.println("   - HTTP Code: " + result.getCode());
        System.out.println("   - Success: " + result.getSuccess());
        System.out.println("   - Message: " + result.getMessage());
        System.out.println("   - Error Code: " + result.getData().get("error_code"));
    }
    
    private static void testBackwardCompatibility() {
        System.out.println("\n📋 测试向后兼容性...");
        
        CommonResult<String> unauthorizedResult = CommonResult.unauthorized();
        CommonResult<String> forbiddenResult = CommonResult.forbidden();
        CommonResult<String> notFoundResult = CommonResult.notFound();
        
        // 验证unauthorized
        assert unauthorizedResult.getCode().equals(401) : "unauthorized HTTP状态码错误";
        assert unauthorizedResult.getSuccess().equals(false) : "unauthorized success字段错误";
        assert "未授权访问".equals(unauthorizedResult.getMessage()) : "unauthorized消息错误";
        assert unauthorizedResult.getData() == null : "unauthorized data应该为null";
        
        // 验证forbidden
        assert forbiddenResult.getCode().equals(403) : "forbidden HTTP状态码错误";
        assert forbiddenResult.getSuccess().equals(false) : "forbidden success字段错误";
        assert "权限不足".equals(forbiddenResult.getMessage()) : "forbidden消息错误";
        assert forbiddenResult.getData() == null : "forbidden data应该为null";
        
        // 验证notFound
        assert notFoundResult.getCode().equals(404) : "notFound HTTP状态码错误";
        assert notFoundResult.getSuccess().equals(false) : "notFound success字段错误";
        assert "资源不存在".equals(notFoundResult.getMessage()) : "notFound消息错误";
        assert notFoundResult.getData() == null : "notFound data应该为null";
        
        System.out.println("✅ 向后兼容性验证通过");
        System.out.println("   - unauthorized(): " + unauthorizedResult.getCode() + " - " + unauthorizedResult.getMessage());
        System.out.println("   - forbidden(): " + forbiddenResult.getCode() + " - " + forbiddenResult.getMessage());
        System.out.println("   - notFound(): " + notFoundResult.getCode() + " - " + notFoundResult.getMessage());
    }
    
    private static void testNewErrorCodes() {
        System.out.println("\n📋 测试新添加的错误码...");
        
        CommonResult<Map<String, Object>> resourceNotFoundResult = CommonResult.error(ErrorCode.RESOURCE_NOT_FOUND);
        CommonResult<Map<String, Object>> operationFailedResult = CommonResult.error(ErrorCode.OPERATION_FAILED);
        
        // 验证RESOURCE_NOT_FOUND
        assert resourceNotFoundResult.getCode().equals(404) : "RESOURCE_NOT_FOUND HTTP状态码错误";
        assert "资源不存在".equals(resourceNotFoundResult.getMessage()) : "RESOURCE_NOT_FOUND消息错误";
        assert "RESOURCE_NOT_FOUND".equals(resourceNotFoundResult.getData().get("error_code")) : "RESOURCE_NOT_FOUND错误码错误";
        
        // 验证OPERATION_FAILED
        assert operationFailedResult.getCode().equals(500) : "OPERATION_FAILED HTTP状态码错误";
        assert "操作失败".equals(operationFailedResult.getMessage()) : "OPERATION_FAILED消息错误";
        assert "OPERATION_FAILED".equals(operationFailedResult.getData().get("error_code")) : "OPERATION_FAILED错误码错误";
        
        System.out.println("✅ 新错误码验证通过");
        System.out.println("   - RESOURCE_NOT_FOUND: " + resourceNotFoundResult.getCode() + " - " + resourceNotFoundResult.getMessage());
        System.out.println("   - OPERATION_FAILED: " + operationFailedResult.getCode() + " - " + operationFailedResult.getMessage());
    }
}
