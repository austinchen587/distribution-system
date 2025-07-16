package com.example.common.exception;

import com.example.common.constants.ErrorCode;
import com.example.common.dto.CommonResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolationException;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("全局异常处理器单元测试")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    @DisplayName("测试处理业务异常")
    void testHandleBusinessException() {
        String errorMessage = "业务处理失败";
        BusinessException exception = new BusinessException(ErrorCode.OPERATION_FAILED);
        
        CommonResult<String> result = globalExceptionHandler.handleBusinessException(exception);
        
        assertNotNull(result);
        assertEquals(ErrorCode.OPERATION_FAILED.getCode(), result.getCode());
        assertEquals(ErrorCode.OPERATION_FAILED.getMessage(), result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("测试处理认证异常")
    void testHandleAuthenticationException() {
        String errorMessage = "认证失败";
        AuthenticationException exception = new AuthenticationException(errorMessage);
        
        CommonResult<String> result = globalExceptionHandler.handleAuthenticationException(exception);
        
        assertNotNull(result);
        assertEquals(401, result.getCode());
        assertEquals("未授权访问", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("测试处理授权异常")
    void testHandleAuthorizationException() {
        String errorMessage = "权限不足";
        AuthorizationException exception = new AuthorizationException(errorMessage);
        
        CommonResult<String> result = globalExceptionHandler.handleAuthorizationException(exception);
        
        assertNotNull(result);
        assertEquals(403, result.getCode());
        assertEquals("权限不足", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("测试处理方法参数校验异常")
    void testHandleValidationException() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        FieldError fieldError = new FieldError("user", "phone", "手机号格式不正确");
        when(exception.getBindingResult()).thenReturn(mock(org.springframework.validation.BindingResult.class));
        when(exception.getBindingResult().getFieldError()).thenReturn(fieldError);
        
        CommonResult<String> result = globalExceptionHandler.handleValidationException(exception);
        
        assertNotNull(result);
        assertEquals(400, result.getCode());
        assertEquals("手机号格式不正确", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("测试处理参数绑定异常")
    void testHandleBindException() {
        BindException exception = mock(BindException.class);
        FieldError fieldError = new FieldError("user", "email", "邮箱格式不正确");
        when(exception.getBindingResult()).thenReturn(mock(org.springframework.validation.BindingResult.class));
        when(exception.getBindingResult().getFieldError()).thenReturn(fieldError);
        
        CommonResult<String> result = globalExceptionHandler.handleBindException(exception);
        
        assertNotNull(result);
        assertEquals(400, result.getCode());
        assertEquals("邮箱格式不正确", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("测试处理约束校验异常")
    void testHandleConstraintViolationException() {
        String constraintMessage = "参数不能为空";
        ConstraintViolationException exception = new ConstraintViolationException(constraintMessage, Set.of());
        
        CommonResult<String> result = globalExceptionHandler.handleConstraintViolationException(exception);
        
        assertNotNull(result);
        assertEquals(400, result.getCode());
        assertEquals(constraintMessage, result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("测试处理通用异常")
    void testHandleException() {
        String errorMessage = "系统内部错误";
        Exception exception = new RuntimeException(errorMessage);
        
        CommonResult<String> result = globalExceptionHandler.handleException(exception);
        
        assertNotNull(result);
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), result.getCode());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("测试业务异常不同错误码")
    void testBusinessExceptionDifferentCodes() {
        // 测试不同的业务异常码
        BusinessException badRequestException = new BusinessException(ErrorCode.BAD_REQUEST);
        CommonResult<String> badRequestResult = globalExceptionHandler.handleBusinessException(badRequestException);
        assertEquals(ErrorCode.BAD_REQUEST.getCode(), badRequestResult.getCode());
        assertEquals(ErrorCode.BAD_REQUEST.getMessage(), badRequestResult.getMessage());

        BusinessException notFoundException = new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        CommonResult<String> notFoundResult = globalExceptionHandler.handleBusinessException(notFoundException);
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND.getCode(), notFoundResult.getCode());
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND.getMessage(), notFoundResult.getMessage());

        BusinessException unauthorizedException = new BusinessException(ErrorCode.UNAUTHORIZED);
        CommonResult<String> unauthorizedResult = globalExceptionHandler.handleBusinessException(unauthorizedException);
        assertEquals(ErrorCode.UNAUTHORIZED.getCode(), unauthorizedResult.getCode());
        assertEquals(ErrorCode.UNAUTHORIZED.getMessage(), unauthorizedResult.getMessage());
    }

    @Test
    @DisplayName("测试空消息处理")
    void testEmptyMessageHandling() {
        BusinessException exception = new BusinessException(500, null);
        CommonResult<String> result = globalExceptionHandler.handleBusinessException(exception);
        
        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertNull(result.getMessage());
    }

    @Test
    @DisplayName("测试异常链处理")
    void testExceptionChain() {
        Exception rootCause = new IllegalArgumentException("参数错误");
        Exception exception = new RuntimeException("操作失败", rootCause);
        
        CommonResult<String> result = globalExceptionHandler.handleException(exception);
        
        assertNotNull(result);
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), result.getCode());
    }
}