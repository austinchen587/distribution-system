package com.example.common.dto;

import java.util.Map;

/**
 * 兼容旧版的统一响应类型。
 *
 * 历史上测试代码/部分调用方引用了 ApiResponse；当前主推 CommonResult。
 * 为减少大规模改动，这里提供一个轻量级的兼容层。
 */
@Deprecated
public class ApiResponse<T> extends CommonResult<T> {

    public ApiResponse() {
        super();
    }

    public ApiResponse(Integer code, Boolean success, String message, T data) {
        super(code, success, message, data);
    }

    public ApiResponse(Integer code, String message, T data) {
        super(code, message, data);
    }

    // 便捷方法 - 与 CommonResult 对齐
    public static <T> ApiResponse<T> success(T data) {
        CommonResult<T> r = CommonResult.success(data);
        return copy(r);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        CommonResult<T> r = CommonResult.success(message, data);
        return copy(r);
    }

    public static <T> ApiResponse<T> error(Integer code, String message) {
        CommonResult<T> r = CommonResult.error(code, message);
        return copy(r);
    }

    public static ApiResponse<Map<String, Object>> unauthorized() {
        CommonResult<Map<String, Object>> r = CommonResult.unauthorizedWithErrorCode();
        return copy(r);
    }

    public static ApiResponse<Map<String, Object>> forbidden() {
        CommonResult<Map<String, Object>> r = CommonResult.forbiddenWithErrorCode();
        return copy(r);
    }

    // 工具：从 CommonResult 复制数据
    private static <T> ApiResponse<T> copy(CommonResult<T> r) {
        ApiResponse<T> a = new ApiResponse<>();
        a.setCode(r.getCode());
        a.setSuccess(r.getSuccess());
        a.setMessage(r.getMessage());
        a.setData(r.getData());
        a.setTimestamp(r.getTimestamp());
        return a;
    }
}

