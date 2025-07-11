package com.example.common.enums;

public enum PromotionType {
    TEXT("text", "图文推广"),
    VIDEO("video", "视频推广"),
    REAL_PERSON("real_person", "真人出镜");
    
    private final String code;
    private final String description;
    
    PromotionType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static PromotionType fromCode(String code) {
        for (PromotionType type : PromotionType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知推广类型代码: " + code);
    }
}