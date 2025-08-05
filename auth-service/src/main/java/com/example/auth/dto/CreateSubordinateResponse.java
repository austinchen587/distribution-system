package com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 创建下级用户响应
 */
@Schema(description = "创建下级用户响应")
public class CreateSubordinateResponse {
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