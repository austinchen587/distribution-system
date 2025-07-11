package com.example.common.constants;

public class ApiConstants {
    
    public static final Integer SUCCESS_CODE = 0;
    public static final String SUCCESS_MESSAGE = "成功";
    
    public static final Integer ERROR_CODE = 500;
    public static final String ERROR_MESSAGE = "系统内部错误";
    
    public static final Integer UNAUTHORIZED_CODE = 401;
    public static final String UNAUTHORIZED_MESSAGE = "未认证";
    
    public static final Integer FORBIDDEN_CODE = 403;
    public static final String FORBIDDEN_MESSAGE = "无权限";
    
    public static final Integer NOT_FOUND_CODE = 404;
    public static final String NOT_FOUND_MESSAGE = "资源不存在";
    
    public static final Integer BUSINESS_ERROR_CODE = 1000;
    
    private ApiConstants() {
    }
}