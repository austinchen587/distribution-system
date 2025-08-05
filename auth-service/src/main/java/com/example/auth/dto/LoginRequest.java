package com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求
 */
@Schema(description = "用户登录请求")
public class LoginRequest {
    @NotBlank(message = "手机号不能为空")
    @Schema(description = "手机号", required = true, example = "13800138000")
    private String phone;
    
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", required = true, example = "123456")
    private String password;
    
    // Getters and setters
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}