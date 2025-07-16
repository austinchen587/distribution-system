package com.example.common.exception;

import com.example.common.constants.ErrorCode;

public class AuthenticationException extends BusinessException {
    
    public AuthenticationException() {
        super(ErrorCode.UNAUTHORIZED);
    }
    
    public AuthenticationException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }
    
    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }
}