package com.example.common.enums;

public enum PromotionPlatform {
    DOUYIN("douyin", "抖音"),
    XIAOHONGSHU("xiaohongshu", "小红书"),
    KUAISHOU("kuaishou", "快手");
    
    private final String code;
    private final String description;
    
    PromotionPlatform(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static PromotionPlatform fromCode(String code) {
        for (PromotionPlatform platform : PromotionPlatform.values()) {
            if (platform.getCode().equals(code)) {
                return platform;
            }
        }
        throw new IllegalArgumentException("未知推广平台代码: " + code);
    }
}