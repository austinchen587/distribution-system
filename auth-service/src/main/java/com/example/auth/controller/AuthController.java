package com.example.auth.controller;

import com.example.auth.entity.User;
import com.example.auth.service.AuthService;
import com.example.auth.service.SmsService;
import com.example.common.annotation.RequireRole;
import com.example.common.dto.ApiResponse;
import com.example.common.enums.UserRole;
import com.example.common.utils.JwtUtils;
import com.example.common.utils.UserContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 用户认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "用户认证", description = "用户注册、登录、token刷新等认证相关接口")
public class AuthController {
    
    @Autowired
    private SmsService smsService;
    
    @Autowired
    private AuthService authService;
    
    @Operation(summary = "发送注册验证码", description = "向手机号发送注册验证码")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "发送成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "手机号格式错误或发送太频繁")
    })
    @PostMapping("/send-code")
    public com.example.common.dto.ApiResponse<String> sendRegisterCode(
            @Valid @RequestBody SendCodeRequest request) {
        smsService.sendRegisterCode(request.getPhone());
        return com.example.common.dto.ApiResponse.success("验证码发送成功");
    }
    
    @Operation(summary = "用户注册", description = "新用户注册，支持销售和代理角色注册")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "注册成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数错误或手机号已存在")
    })
    @PostMapping("/register")
    public com.example.common.dto.ApiResponse<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        LoginResponse loginResponse = authService.register(request);
        
        // 转换为RegisterResponse
        RegisterResponse response = new RegisterResponse();
        response.setUserId(loginResponse.getUserId());
        response.setPhone(loginResponse.getPhone());
        response.setRole(loginResponse.getRole());
        response.setMessage("注册成功");
        
        return com.example.common.dto.ApiResponse.success(response);
    }
    
    @Operation(summary = "用户登录", description = "用户登录获取JWT令牌")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "登录成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
    @PostMapping("/login")
    public com.example.common.dto.ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return com.example.common.dto.ApiResponse.success(response);
    }
    
    @Operation(summary = "获取当前用户信息", description = "根据token获取当前登录用户信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未登录或token已过期")
    })
    @GetMapping("/current")
    @SecurityRequirement(name = "JWT")
    public com.example.common.dto.ApiResponse<UserInfo> getCurrentUser() {
        // 从UserContextHolder获取当前用户信息
        String userId = UserContextHolder.getCurrentUserId();
        if (userId == null) {
            return com.example.common.dto.ApiResponse.error(401, "未登录或token已过期");
        }
        
        User user = authService.getCurrentUser(Long.valueOf(userId));
        
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setPhone(user.getPhone());
        userInfo.setRole(user.getRole().getCode());
        userInfo.setNickname(user.getNickname());
        userInfo.setStatus(user.getStatus());
        
        return com.example.common.dto.ApiResponse.success(userInfo);
    }
    
    @Operation(summary = "刷新Token", description = "使用旧token换取新token")
    @PostMapping("/refresh")
    @SecurityRequirement(name = "JWT")
    public com.example.common.dto.ApiResponse<RefreshTokenResponse> refreshToken(
            @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return com.example.common.dto.ApiResponse.error(401, "无效的Authorization header");
        }
        
        String oldToken = authHeader.substring(7);
        String newToken = authService.refreshToken(oldToken);
        
        RefreshTokenResponse response = new RefreshTokenResponse();
        response.setToken(newToken);
        response.setExpiresIn(86400L); // 24小时
        
        return com.example.common.dto.ApiResponse.success(response);
    }
    
    @Operation(summary = "退出登录", description = "退出登录，使token失效")
    @PostMapping("/logout")
    @SecurityRequirement(name = "JWT")
    public com.example.common.dto.ApiResponse<Void> logout(
            @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return com.example.common.dto.ApiResponse.error(401, "无效的Authorization header");
        }
        
        String token = authHeader.substring(7);
        String userId = UserContextHolder.getCurrentUserId();
        if (userId != null) {
            authService.logout(token, Long.valueOf(userId));
        }
        
        return com.example.common.dto.ApiResponse.success(null);
    }
    
    /**
     * 快速创建下级用户
     * 
     * <p>销售及以上角色专用接口，用于快速创建下级用户账号。
     * 该接口无需短信验证，自动使用创建者的邀请码建立邀请关系。
     * 
     * <p>权限要求：
     * <ul>
     *   <li>SALES - 可创建 AGENT</li>
     *   <li>LEADER - 可创建 SALES, AGENT</li>
     *   <li>DIRECTOR - 可创建 LEADER, SALES, AGENT</li>
     *   <li>SUPER_ADMIN - 可创建任何角色</li>
     * </ul>
     * 
     * <p>业务规则：
     * <ul>
     *   <li>只能创建比自己权限低的角色</li>
     *   <li>自动绑定创建者为邀请人</li>
     *   <li>无需短信验证码</li>
     *   <li>手机号必须唯一</li>
     * </ul>
     * 
     * @param request 创建用户请求，包含手机号、密码、昵称、角色等信息
     * @return 创建成功的用户账号信息
     */
    @Operation(summary = "快速创建下级用户", 
               description = "销售及以上角色可通过手机号快速创建下级用户账号，无需短信验证，自动绑定邀请关系。只能创建比自己权限低的角色。")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "创建成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数错误或手机号已存在"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足，不能创建同级或更高级别的角色")
    })
    @PostMapping("/create-subordinate")
    @RequireRole(value = {UserRole.SALES, UserRole.LEADER, UserRole.DIRECTOR, UserRole.SUPER_ADMIN})
    @SecurityRequirement(name = "JWT")
    public com.example.common.dto.ApiResponse<CreateSubordinateResponse> createSubordinate(
            @Valid @RequestBody CreateSubordinateRequest request) {
        return authService.createSubordinateBySuperior(request);
    }
    
    /**
     * 注册请求
     */
    @Schema(description = "用户注册请求")
    public static class RegisterRequest {
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
    
    /**
     * 注册响应
     */
    @Schema(description = "注册响应")
    public static class RegisterResponse {
        @Schema(description = "用户ID")
        private Long userId;
        
        @Schema(description = "手机号")
        private String phone;
        
        @Schema(description = "角色")
        private String role;
        
        @Schema(description = "消息")
        private String message;
        
        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    /**
     * 登录请求
     */
    @Schema(description = "用户登录请求")
    public static class LoginRequest {
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
    
    /**
     * 登录响应
     */
    @Schema(description = "登录响应")
    public static class LoginResponse {
        @Schema(description = "JWT令牌")
        private String token;
        
        @Schema(description = "用户ID")
        private Long userId;
        
        @Schema(description = "手机号")
        private String phone;
        
        @Schema(description = "角色")
        private String role;
        
        @Schema(description = "昵称")
        private String nickname;
        
        // Getters and setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
    }
    
    /**
     * 用户信息
     */
    @Schema(description = "用户信息")
    public static class UserInfo {
        @Schema(description = "用户ID")
        private Long userId;
        
        @Schema(description = "手机号")
        private String phone;
        
        @Schema(description = "角色")
        private String role;
        
        @Schema(description = "昵称")
        private String nickname;
        
        @Schema(description = "状态")
        private String status;
        
        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    
    /**
     * 刷新Token响应
     */
    @Schema(description = "刷新Token响应")
    public static class RefreshTokenResponse {
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
    
    /**
     * 发送验证码请求
     */
    @Schema(description = "发送验证码请求")
    public static class SendCodeRequest {
        @NotBlank(message = "手机号不能为空")
        @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
        @Schema(description = "手机号", required = true, example = "13800138000")
        private String phone;
        
        // Getters and setters
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }
    
    /**
     * 快速创建下级用户请求
     */
    @Schema(description = "快速创建下级用户请求")
    public static class CreateSubordinateRequest {
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
    
    /**
     * 创建下级用户响应
     */
    @Schema(description = "创建下级用户响应")
    public static class CreateSubordinateResponse {
        @Schema(description = "用户ID")
        private Long userId;
        
        @Schema(description = "手机号")
        private String phone;
        
        @Schema(description = "昵称")
        private String nickname;
        
        @Schema(description = "角色")
        private String role;
        
        @Schema(description = "邀请码")
        private String inviteCode;
        
        @Schema(description = "邀请人ID")
        private Long inviterId;
        
        @Schema(description = "邀请人昵称")
        private String inviterNickname;
        
        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getInviteCode() { return inviteCode; }
        public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }
        public Long getInviterId() { return inviterId; }
        public void setInviterId(Long inviterId) { this.inviterId = inviterId; }
        public String getInviterNickname() { return inviterNickname; }
        public void setInviterNickname(String inviterNickname) { this.inviterNickname = inviterNickname; }
    }
}