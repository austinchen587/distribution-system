package com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 注册请求
 */
@Schema(description = "用户注册请求")
public class RegisterRequest {
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", required = true, example = "13800138000")
    private String phone;
    
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "验证码格式不正确")
    @Schema(description = "短信验证码（6位数字）", required = true, example = "123456")
    private String code;
    
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码（6-20位）", required = true, example = "123456")
    private String password;
    
    @NotBlank(message = "角色不能为空")
    @Schema(description = "角色（sales/agent）", required = true, example = "sales")
    private String role;
    
    @Schema(description = "邀请码（代理注册时必填）", example = "INVITE123")
    private String inviteCode;
    
    @Schema(description = "昵称", example = "张三")
    private String nickname;
    
    // Getters and setters
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}