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
        if (code == null) {
            throw new IllegalArgumentException("角色代码为空");
        }
        String n = code.trim();
        if (n.isEmpty()) {
            throw new IllegalArgumentException("角色代码为空");
        }
        String normalized = n.toLowerCase().replace('-', '_');
        for (UserRole role : UserRole.values()) {
            if (role.getCode().equals(normalized)) {
                return role;
            }
        }
        // Fallback: allow enum name inputs like SUPER_ADMIN
        return UserRole.valueOf(normalized.toUpperCase());
    }
}