package com.example.common.exception;

import com.example.common.constants.ErrorCode;

/**
 * 业务异常类
 * 支持新的统一错误码格式
 * 
 * @author Backend Team
 * @version 2.0.0
 */
public class BusinessException extends RuntimeException {
    private Integer code;
    private String message;
    private String errorCode;
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    public BusinessException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }
    
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getHttpCode();
        this.message = errorCode.getMessage();
        this.errorCode = errorCode.getErrorCode();
    }
    
    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.code = errorCode.getHttpCode();
        this.message = customMessage;
        this.errorCode = errorCode.getErrorCode();
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}