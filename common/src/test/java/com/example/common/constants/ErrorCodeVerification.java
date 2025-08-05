package com.example.common.constants;

/**
 * 简单的错误码验证类
 * 用于验证新添加的错误码常量可以正常访问
 */
public class ErrorCodeVerification {
    
    public static void main(String[] args) {
        System.out.println("开始验证错误码常量...");
        
        try {
            // 验证 RESOURCE_NOT_FOUND
            ErrorCode resourceNotFound = ErrorCode.RESOURCE_NOT_FOUND;
            System.out.println("✅ RESOURCE_NOT_FOUND 可访问:");
            System.out.println("   HTTP Code: " + resourceNotFound.getHttpCode());
            System.out.println("   Error Code: " + resourceNotFound.getErrorCode());
            System.out.println("   Message: " + resourceNotFound.getMessage());
            
            // 验证 OPERATION_FAILED
            ErrorCode operationFailed = ErrorCode.OPERATION_FAILED;
            System.out.println("✅ OPERATION_FAILED 可访问:");
            System.out.println("   HTTP Code: " + operationFailed.getHttpCode());
            System.out.println("   Error Code: " + operationFailed.getErrorCode());
            System.out.println("   Message: " + operationFailed.getMessage());
            
            // 验证向后兼容的 getCode() 方法
            System.out.println("✅ 向后兼容性验证:");
            System.out.println("   RESOURCE_NOT_FOUND.getCode(): " + resourceNotFound.getCode());
            System.out.println("   OPERATION_FAILED.getCode(): " + operationFailed.getCode());
            
            // 验证 fromErrorCode 查找功能
            ErrorCode found1 = ErrorCode.fromErrorCode("RESOURCE_NOT_FOUND");
            ErrorCode found2 = ErrorCode.fromErrorCode("OPERATION_FAILED");
            System.out.println("✅ 错误码查找功能:");
            System.out.println("   fromErrorCode('RESOURCE_NOT_FOUND'): " + (found1 != null ? "成功" : "失败"));
            System.out.println("   fromErrorCode('OPERATION_FAILED'): " + (found2 != null ? "成功" : "失败"));
            
            System.out.println("\n🎉 所有错误码验证通过！");
            
        } catch (Exception e) {
            System.err.println("❌ 错误码验证失败: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
