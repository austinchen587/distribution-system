package com.example.common.exception;

import com.example.common.constants.ErrorCode;

public class AuthorizationException extends BusinessException {
    
    public AuthorizationException() {
        super(ErrorCode.FORBIDDEN);
    }
    
    public AuthorizationException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }
    
    public AuthorizationException(ErrorCode errorCode) {
        super(errorCode);
    }
}