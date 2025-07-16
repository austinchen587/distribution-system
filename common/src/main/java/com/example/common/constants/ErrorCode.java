package com.example.common.constants;

public enum ErrorCode {
    
    SUCCESS(200, "操作成功"),
    
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权访问"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    
    INTERNAL_SERVER_ERROR(500, "系统内部错误"),
    
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    INVALID_CREDENTIALS(1003, "用户名或密码错误"),
    TOKEN_EXPIRED(1004, "Token已过期"),
    TOKEN_INVALID(1005, "Token无效"),
    
    PHONE_ALREADY_EXISTS(2001, "手机号已存在"),
    INVALID_PHONE_FORMAT(2002, "手机号格式错误"),
    SMS_SEND_FAILED(2003, "短信发送失败"),
    SMS_CODE_EXPIRED(2004, "验证码已过期"),
    SMS_CODE_INVALID(2005, "验证码错误"),
    
    CUSTOMER_NOT_FOUND(3001, "客户不存在"),
    CUSTOMER_ALREADY_EXISTS(3002, "客户已存在"),
    CUSTOMER_ASSIGNED(3003, "客户已分配"),
    
    PRODUCT_NOT_FOUND(4001, "商品不存在"),
    PRODUCT_INACTIVE(4002, "商品已下架"),
    
    DEAL_NOT_FOUND(5001, "成交记录不存在"),
    DEAL_ALREADY_REFUNDED(5002, "订单已退款"),
    
    COMMISSION_NOT_FOUND(6001, "佣金记录不存在"),
    COMMISSION_ALREADY_PAID(6002, "佣金已支付"),
    
    PROMOTION_NOT_FOUND(7001, "推广任务不存在"),
    PROMOTION_ALREADY_AUDITED(7002, "推广任务已审核"),
    
    LEVEL_NOT_FOUND(8001, "等级不存在"),
    LEVEL_AUDIT_NOT_FOUND(8002, "等级审核记录不存在"),
    LEVEL_AUDIT_ALREADY_PROCESSED(8003, "等级审核已处理"),
    
    PERMISSION_DENIED(9001, "权限不足"),
    ROLE_NOT_ALLOWED(9002, "角色权限不足"),
    DATA_ACCESS_DENIED(9003, "数据访问权限不足");
    
    private final Integer code;
    private final String message;
    
    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}