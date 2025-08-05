package com.example.auth.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 创建用户请求DTO
 * 包含完整的JSR-303验证规则
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
@Schema(description = "创建用户请求")
public class CreateUserRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 64, message = "用户名长度必须在3-64个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$", message = "用户名只能包含字母、数字、下划线和中文")
    private String username;
    
    /**
     * 密码
     */
    @Schema(description = "密码", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 128, message = "密码长度必须在6-128个字符之间")
    private String password;
    
    /**
     * 用户邮箱
     */
    @Schema(description = "用户邮箱", example = "zhangsan@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "邮箱不能为空")
    @Size(max = 255, message = "邮箱长度不能超过255个字符")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 手机号码
     */
    @Schema(description = "手机号码", example = "13800138000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;
    
    /**
     * 用户角色
     */
    @Schema(description = "用户角色", example = "agent", allowableValues = {"super_admin", "director", "leader", "sales", "agent"})
    @NotBlank(message = "用户角色不能为空")
    @Pattern(regexp = "^(super_admin|director|leader|sales|agent)$", message = "用户角色必须是: super_admin, director, leader, sales, agent")
    private String role;
    
    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名", example = "张三")
    @Size(max = 100, message = "真实姓名长度不能超过100个字符")
    private String realName;
    
    /**
     * 邀请码（可选）
     */
    @Schema(description = "邀请码", example = "INV123456")
    @Size(max = 50, message = "邀请码长度不能超过50个字符")
    private String invitationCode;
    
    // Constructors
    public CreateUserRequest() {}
    
    public CreateUserRequest(String username, String password, String email, String phone, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public String getInvitationCode() {
        return invitationCode;
    }
    
    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }
    
    @Override
    public String toString() {
        return "CreateUserRequest{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", realName='" + realName + '\'' +
                ", invitationCode='" + invitationCode + '\'' +
                '}';
    }
}