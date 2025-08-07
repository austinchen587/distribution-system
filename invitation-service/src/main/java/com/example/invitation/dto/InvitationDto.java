package com.example.invitation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 邀请记录DTO - 用于邀请关系和追踪
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
@Schema(description = "邀请记录")
public class InvitationDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 邀请记录ID
     */
    @Schema(description = "邀请记录ID", example = "1")
    @NotNull
    private Long id;
    
    /**
     * 邀请人用户ID
     */
    @Schema(description = "邀请人用户ID", example = "10")
    @NotNull
    private Long inviterId;
    
    /**
     * 邀请人用户名
     */
    @Schema(description = "邀请人用户名", example = "inviter001")
    private String inviterUsername;
    
    /**
     * 邀请人真实姓名
     */
    @Schema(description = "邀请人真实姓名", example = "张三")
    private String inviterRealName;
    
    /**
     * 被邀请人用户ID
     */
    @Schema(description = "被邀请人用户ID", example = "20")
    private Long inviteeId;
    
    /**
     * 被邀请人用户名
     */
    @Schema(description = "被邀请人用户名", example = "invitee001")
    private String inviteeUsername;
    
    /**
     * 被邀请人真实姓名
     */
    @Schema(description = "被邀请人真实姓名", example = "李四")
    private String inviteeRealName;
    
    /**
     * 使用的邀请码
     */
    @Schema(description = "使用的邀请码", example = "INV123456")
    @NotBlank
    private String invitationCode;
    
    /**
     * 邀请状态
     */
    @Schema(description = "邀请状态", example = "COMPLETED", 
            allowableValues = {"PENDING", "REGISTERED", "ACTIVATED", "COMPLETED"})
    private String status;
    
    /**
     * 邀请渠道
     */
    @Schema(description = "邀请渠道", example = "微信分享")
    private String invitationChannel;
    
    /**
     * 被邀请人注册时间
     */
    @Schema(description = "被邀请人注册时间", example = "2025-08-04 10:00:00")
    private String registeredAt;
    
    /**
     * 被邀请人激活时间
     */
    @Schema(description = "被邀请人激活时间", example = "2025-08-04 12:00:00")
    private String activatedAt;
    
    /**
     * 邀请奖励是否已发放
     */
    @Schema(description = "邀请奖励是否已发放", example = "true")
    private Boolean rewardGranted;
    
    /**
     * 邀请奖励金额
     */
    @Schema(description = "邀请奖励金额", example = "50.00")
    private java.math.BigDecimal rewardAmount;
    
    /**
     * 邀请创建时间
     */
    @Schema(description = "邀请创建时间", example = "2025-08-03 15:30:00")
    private String createdAt;
    
    /**
     * 记录更新时间
     */
    @Schema(description = "记录更新时间", example = "2025-08-04 12:00:00")
    private String updatedAt;
    
    // Constructors
    public InvitationDto() {}
    
    public InvitationDto(Long id, Long inviterId, String invitationCode, String status) {
        this.id = id;
        this.inviterId = inviterId;
        this.invitationCode = invitationCode;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getInviterId() {
        return inviterId;
    }
    
    public void setInviterId(Long inviterId) {
        this.inviterId = inviterId;
    }
    
    public String getInviterUsername() {
        return inviterUsername;
    }
    
    public void setInviterUsername(String inviterUsername) {
        this.inviterUsername = inviterUsername;
    }
    
    public String getInviterRealName() {
        return inviterRealName;
    }
    
    public void setInviterRealName(String inviterRealName) {
        this.inviterRealName = inviterRealName;
    }
    
    public Long getInviteeId() {
        return inviteeId;
    }
    
    public void setInviteeId(Long inviteeId) {
        this.inviteeId = inviteeId;
    }
    
    public String getInviteeUsername() {
        return inviteeUsername;
    }
    
    public void setInviteeUsername(String inviteeUsername) {
        this.inviteeUsername = inviteeUsername;
    }
    
    public String getInviteeRealName() {
        return inviteeRealName;
    }
    
    public void setInviteeRealName(String inviteeRealName) {
        this.inviteeRealName = inviteeRealName;
    }
    
    public String getInvitationCode() {
        return invitationCode;
    }
    
    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getInvitationChannel() {
        return invitationChannel;
    }
    
    public void setInvitationChannel(String invitationChannel) {
        this.invitationChannel = invitationChannel;
    }
    
    public String getRegisteredAt() {
        return registeredAt;
    }
    
    public void setRegisteredAt(String registeredAt) {
        this.registeredAt = registeredAt;
    }
    
    public String getActivatedAt() {
        return activatedAt;
    }
    
    public void setActivatedAt(String activatedAt) {
        this.activatedAt = activatedAt;
    }
    
    public Boolean getRewardGranted() {
        return rewardGranted;
    }
    
    public void setRewardGranted(Boolean rewardGranted) {
        this.rewardGranted = rewardGranted;
    }
    
    public java.math.BigDecimal getRewardAmount() {
        return rewardAmount;
    }
    
    public void setRewardAmount(java.math.BigDecimal rewardAmount) {
        this.rewardAmount = rewardAmount;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * 检查是否待处理
     * 
     * @return 是否待处理
     */
    public boolean isPending() {
        return "PENDING".equals(status);
    }
    
    /**
     * 检查是否已注册
     * 
     * @return 是否已注册
     */
    public boolean isRegistered() {
        return "REGISTERED".equals(status) || "ACTIVATED".equals(status) || "COMPLETED".equals(status);
    }
    
    /**
     * 检查是否已激活
     * 
     * @return 是否已激活
     */
    public boolean isActivated() {
        return "ACTIVATED".equals(status) || "COMPLETED".equals(status);
    }
    
    /**
     * 检查是否已完成
     * 
     * @return 是否已完成
     */
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
    
    /**
     * 检查是否可以发放奖励
     * 
     * @return 是否可以发放奖励
     */
    public boolean canGrantReward() {
        return isActivated() && !Boolean.TRUE.equals(rewardGranted);
    }
    
    @Override
    public String toString() {
        return "InvitationDto{" +
                "id=" + id +
                ", inviterId=" + inviterId +
                ", inviterUsername='" + inviterUsername + '\'' +
                ", inviteeId=" + inviteeId +
                ", inviteeUsername='" + inviteeUsername + '\'' +
                ", invitationCode='" + invitationCode + '\'' +
                ", status='" + status + '\'' +
                ", rewardGranted=" + rewardGranted +
                ", rewardAmount=" + rewardAmount +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}