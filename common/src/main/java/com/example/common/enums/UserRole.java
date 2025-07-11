package com.example.common.enums;

public enum UserRole {
    SUPER_ADMIN("super_admin", "超级管理员"),
    DIRECTOR("director", "销售总监"),
    LEADER("leader", "销售组长"),
    SALES("sales", "销售"),
    AGENT("agent", "代理");
    
    private final String code;
    private final String description;
    
    UserRole(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static UserRole fromCode(String code) {
        for (UserRole role : UserRole.values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("未知角色代码: " + code);
    }
}