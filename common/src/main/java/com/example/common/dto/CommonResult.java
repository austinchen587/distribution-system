package com.example.common.dto;

import java.io.Serializable;

public class CommonResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;
    
    public CommonResult() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public CommonResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    public static <T> CommonResult<T> success() {
        return new CommonResult<>(200, "操作成功", null);
    }
    
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(200, "操作成功", data);
    }
    
    public static <T> CommonResult<T> success(String message, T data) {
        return new CommonResult<>(200, message, data);
    }
    
    public static <T> CommonResult<T> error(Integer code, String message) {
        return new CommonResult<>(code, message, null);
    }
    
    public static <T> CommonResult<T> error(String message) {
        return new CommonResult<>(500, message, null);
    }
    
    public static <T> CommonResult<T> unauthorized() {
        return new CommonResult<>(401, "未授权访问", null);
    }
    
    public static <T> CommonResult<T> forbidden() {
        return new CommonResult<>(403, "权限不足", null);
    }
    
    public static <T> CommonResult<T> notFound() {
        return new CommonResult<>(404, "资源不存在", null);
    }
    
    public static <T> CommonResult<T> badRequest(String message) {
        return new CommonResult<>(400, message, null);
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
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
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                '}';
    }
}