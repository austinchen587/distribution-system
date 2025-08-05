package com.example.common.exception;

import com.example.common.dto.CommonResult;
import com.example.common.constants.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(BusinessException.class)
    public CommonResult<Object> handleBusinessException(BusinessException e) {
        logger.warn("业务异常: {}", e.getMessage());

        // 如果有业务错误码，使用新的错误响应格式
        if (e.getErrorCode() != null) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error_code", e.getErrorCode());
            return new CommonResult<>(e.getCode(), false, e.getMessage(), errorData);
        }

        // 向后兼容的处理方式
        return CommonResult.error(e.getCode(), e.getMessage());
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public CommonResult<String> handleAuthenticationException(AuthenticationException e) {
        logger.warn("认证异常: {}", e.getMessage());
        return CommonResult.unauthorized();
    }
    
    @ExceptionHandler(AuthorizationException.class)
    public CommonResult<String> handleAuthorizationException(AuthorizationException e) {
        logger.warn("授权异常: {}", e.getMessage());
        return CommonResult.forbidden();
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<String> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldError().getDefaultMessage();
        logger.warn("参数校验异常: {}", errorMessage);
        return CommonResult.badRequest(errorMessage);
    }
    
    @ExceptionHandler(BindException.class)
    public CommonResult<String> handleBindException(BindException e) {
        String errorMessage = e.getBindingResult().getFieldError().getDefaultMessage();
        logger.warn("参数绑定异常: {}", errorMessage);
        return CommonResult.badRequest(errorMessage);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public CommonResult<String> handleConstraintViolationException(ConstraintViolationException e) {
        logger.warn("约束校验异常: {}", e.getMessage());
        return CommonResult.badRequest(e.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    public CommonResult<Map<String, Object>> handleException(Exception e) {
        logger.error("系统异常", e);
        return CommonResult.error(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}