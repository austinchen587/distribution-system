package com.example.data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 邀请记录实体类
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-04
 */
public class Invitation {
    
    /**
     * 邀请记录ID
     */
    private Long id;
    
    /**
     * 邀请人用户ID
     */
    @NotNull(message = "邀请人用户ID不能为空")
    private Long inviterId;
    
    /**
     * 邀请人用户名
     */
    private String inviterUsername;
    
    /**
     * 邀请人真实姓名
     */
    private String inviterRealName;
    
    /**
     * 被邀请人用户ID
     */
    private Long inviteeId;
    
    /**
     * 被邀请人用户名
     */
    private String inviteeUsername;
    
    /**
     * 被邀请人真实姓名
     */
    private String inviteeRealName;
    
    /**
     * 使用的邀请码
     */
    @NotBlank(message = "邀请码不能为空")
    private String invitationCode;
    
    /**
     * 邀请状态
     */
    @NotBlank(message = "邀请状态不能为空")
    private String status;
    
    /**
     * 邀请渠道
     */
    private String invitationChannel;
    
    /**
     * 被邀请人注册时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String registeredAt;
    
    /**
     * 被邀请人激活时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String activatedAt;
    
    /**
     * 邀请奖励是否已发放
     */
    private Boolean rewardGranted;
    
    /**
     * 邀请奖励金额
     */
    @DecimalMin(value = "0", message = "邀请奖励金额不能为负数")
    private BigDecimal rewardAmount;
    
    /**
     * 邀请创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdAt;
    
    /**
     * 记录更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updatedAt;
    
    // Constructors
    public Invitation() {}
    
    public Invitation(Long inviterId, String invitationCode, String status) {
        this.inviterId = inviterId;
        this.invitationCode = invitationCode;
        this.status = status;
        this.rewardGranted = false;
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
    
    public BigDecimal getRewardAmount() {
        return rewardAmount;
    }
    
    public void setRewardAmount(BigDecimal rewardAmount) {
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invitation that = (Invitation) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(inviterId, that.inviterId) &&
               Objects.equals(invitationCode, that.invitationCode);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, inviterId, invitationCode);
    }
    
    @Override
    public String toString() {
        return "Invitation{" +
                "id=" + id +
                ", inviterId=" + inviterId +
                ", inviterUsername='" + inviterUsername + '\'' +
                ", inviteeId=" + inviteeId +
                ", inviteeUsername='" + inviteeUsername + '\'' +
                ", invitationCode='" + invitationCode + '\'' +
                ", status='" + status + '\'' +
                ", rewardGranted=" + rewardGranted +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}