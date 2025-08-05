package com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 快速创建下级用户请求
 */
@Schema(description = "快速创建下级用户请求")
public class CreateSubordinateRequest {
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", required = true, example = "13900139000")
    private String phone;
    
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码（6-20位）", required = true, example = "123456")
    private String password;
    
    @NotBlank(message = "角色不能为空")
    @Schema(description = "角色（agent/sales/leader/director）", required = true, example = "agent", 
            allowableValues = {"agent", "sales", "leader", "director"})
    private String role;
    
    @Schema(description = "昵称", example = "张代理")
    private String nickname;
    
    // Getters and setters
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}