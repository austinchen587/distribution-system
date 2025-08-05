package com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 刷新Token响应
 */
@Schema(description = "刷新Token响应")
public class RefreshTokenResponse {
    @Schema(description = "新的JWT令牌")
    private String token;
    
    @Schema(description = "过期时间（秒）")
    private Long expiresIn;
    
    // Getters and setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
}