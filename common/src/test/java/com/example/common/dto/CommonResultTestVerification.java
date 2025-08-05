package com.example.common.dto;

/**
 * CommonResult测试验证程序
 * 验证向后兼容的方法是否返回正确的格式
 */
public class CommonResultTestVerification {
    
    public static void main(String[] args) {
        System.out.println("开始验证CommonResult测试断言...");
        
        try {
            // 验证unauthorized方法
            testUnauthorized();
            
            // 验证forbidden方法
            testForbidden();
            
            // 验证notFound方法
            testNotFound();
            
            System.out.println("\n🎉 所有CommonResult测试断言验证通过！");
            
        } catch (Exception e) {
            System.err.println("❌ CommonResult测试断言验证失败: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void testUnauthorized() {
        System.out.println("\n📋 测试unauthorized方法...");
        
        CommonResult<String> result = CommonResult.unauthorized();
        
        assert result != null : "结果不能为空";
        assert result.getCode().equals(401) : "HTTP状态码应该是401，实际是: " + result.getCode();
        assert result.getSuccess().equals(false) : "success应该是false，实际是: " + result.getSuccess();
        assert "未授权访问".equals(result.getMessage()) : "消息应该是'未授权访问'，实际是: " + result.getMessage();
        assert result.getData() == null : "data应该为null，实际是: " + result.getData();
        
        System.out.println("✅ unauthorized方法验证通过");
        System.out.println("   - Code: " + result.getCode());
        System.out.println("   - Success: " + result.getSuccess());
        System.out.println("   - Message: " + result.getMessage());
        System.out.println("   - Data: " + result.getData());
    }
    
    private static void testForbidden() {
        System.out.println("\n📋 测试forbidden方法...");
        
        CommonResult<String> result = CommonResult.forbidden();
        
        assert result != null : "结果不能为空";
        assert result.getCode().equals(403) : "HTTP状态码应该是403，实际是: " + result.getCode();
        assert result.getSuccess().equals(false) : "success应该是false，实际是: " + result.getSuccess();
        assert "权限不足".equals(result.getMessage()) : "消息应该是'权限不足'，实际是: " + result.getMessage();
        assert result.getData() == null : "data应该为null，实际是: " + result.getData();
        
        System.out.println("✅ forbidden方法验证通过");
        System.out.println("   - Code: " + result.getCode());
        System.out.println("   - Success: " + result.getSuccess());
        System.out.println("   - Message: " + result.getMessage());
        System.out.println("   - Data: " + result.getData());
    }
    
    private static void testNotFound() {
        System.out.println("\n📋 测试notFound方法...");
        
        CommonResult<String> result = CommonResult.notFound();
        
        assert result != null : "结果不能为空";
        assert result.getCode().equals(404) : "HTTP状态码应该是404，实际是: " + result.getCode();
        assert result.getSuccess().equals(false) : "success应该是false，实际是: " + result.getSuccess();
        assert "资源不存在".equals(result.getMessage()) : "消息应该是'资源不存在'，实际是: " + result.getMessage();
        assert result.getData() == null : "data应该为null，实际是: " + result.getData();
        
        System.out.println("✅ notFound方法验证通过");
        System.out.println("   - Code: " + result.getCode());
        System.out.println("   - Success: " + result.getSuccess());
        System.out.println("   - Message: " + result.getMessage());
        System.out.println("   - Data: " + result.getData());
    }
}
