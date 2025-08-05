package com.example.common.dto;

import com.example.common.constants.ErrorCode;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一响应结果类
 * 支持新的统一错误码格式
 * 
 * @author Backend Team
 * @version 2.0.0
 */
public class CommonResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer code;
    private Boolean success;
    private String message;
    private T data;
    private Long timestamp;
    
    public CommonResult() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public CommonResult(Integer code, Boolean success, String message, T data) {
        this.code = code;
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    // ============= 基础构造方法（向后兼容） =============
    public CommonResult(Integer code, String message, T data) {
        this.code = code;
        this.success = code == 200;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    // ============= 成功响应方法 =============
    public static <T> CommonResult<T> success() {
        return new CommonResult<>(200, true, "操作成功", null);
    }
    
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(200, true, "操作成功", data);
    }
    
    public static <T> CommonResult<T> success(String message, T data) {
        return new CommonResult<>(200, true, message, data);
    }
    
    // ============= 使用ErrorCode枚举的方法（新格式） =============
    public static CommonResult<Map<String, Object>> error(ErrorCode errorCode) {
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("error_code", errorCode.getErrorCode());
        return new CommonResult<>(errorCode.getHttpCode(), false, errorCode.getMessage(), errorData);
    }

    public static CommonResult<Map<String, Object>> error(ErrorCode errorCode, String customMessage) {
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("error_code", errorCode.getErrorCode());
        return new CommonResult<>(errorCode.getHttpCode(), false, customMessage, errorData);
    }
    
    public static <T> CommonResult<T> error(ErrorCode errorCode, T data) {
        return new CommonResult<>(errorCode.getHttpCode(), false, errorCode.getMessage(), data);
    }
    
    // ============= 向后兼容的错误方法 =============
    public static <T> CommonResult<T> error(Integer code, String message) {
        return new CommonResult<>(code, false, message, null);
    }
    
    public static <T> CommonResult<T> error(String message) {
        return new CommonResult<>(500, false, message, null);
    }
    
    public static <T> CommonResult<T> unauthorized() {
        return new CommonResult<>(401, false, "未授权访问", null);
    }

    public static <T> CommonResult<T> forbidden() {
        return new CommonResult<>(403, false, "权限不足", null);
    }

    public static <T> CommonResult<T> notFound() {
        return new CommonResult<>(404, false, "资源不存在", null);
    }

    // ============= 新格式的便捷方法 =============
    public static CommonResult<Map<String, Object>> unauthorizedWithErrorCode() {
        return error(ErrorCode.UNAUTHORIZED);
    }

    public static CommonResult<Map<String, Object>> forbiddenWithErrorCode() {
        return error(ErrorCode.FORBIDDEN);
    }

    public static CommonResult<Map<String, Object>> notFoundWithErrorCode() {
        return error(ErrorCode.NOT_FOUND);
    }
    
    public static <T> CommonResult<T> badRequest(String message) {
        return new CommonResult<>(400, false, message, null);
    }
    
    // ============= Getter和Setter方法 =============
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
        // 自动设置success字段
        this.success = code == 200;
    }
    
    public Boolean getSuccess() {
        return success;
    }
    
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "CommonResult{" +
                "code=" + code +
                ", success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                '}';
    }
}