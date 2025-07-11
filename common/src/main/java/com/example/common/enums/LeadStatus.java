package com.example.common.enums;

public enum LeadStatus {
    NEW("new", "新提交"),
    CONTACTED("contacted", "已联系"),
    INTERESTED("interested", "有意向"),
    CLOSED("closed", "已成交"),
    INVALID("invalid", "无效客户");
    
    private final String code;
    private final String description;
    
    LeadStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static LeadStatus fromCode(String code) {
        for (LeadStatus status : LeadStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知客户状态代码: " + code);
    }
}