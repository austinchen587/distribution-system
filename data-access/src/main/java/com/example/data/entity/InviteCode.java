package com.example.data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 邀请码实体类
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-04
 */
public class InviteCode {
    
    /**
     * 邀请码记录ID
     */
    private Long id;
    
    /**
     * 邀请码所有者用户ID
     */
    @NotNull(message = "邀请码所有者用户ID不能为空")
    private Long ownerId;
    
    /**
     * 邀请码所有者用户名
     */
    private String ownerUsername;
    
    /**
     * 邀请码所有者真实姓名
     */
    private String ownerRealName;
    
    /**
     * 邀请码
     */
    @NotBlank(message = "邀请码不能为空")
    private String code;
    
    /**
     * 邀请码类型
     */
    private String codeType;
    
    /**
     * 邀请码状态
     */
    @NotBlank(message = "邀请码状态不能为空")
    private String status;
    
    /**
     * 使用次数限制
     */
    @Min(value = 0, message = "使用次数限制不能为负数")
    private Integer usageLimit;
    
    /**
     * 已使用次数
     */
    @Min(value = 0, message = "已使用次数不能为负数")
    private Integer usedCount;
    
    /**
     * 邀请码生效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String validFrom;
    
    /**
     * 邀请码失效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String validUntil;
    
    /**
     * 奖励金额
     */
    @DecimalMin(value = "0", message = "奖励金额不能为负数")
    private BigDecimal rewardAmount;
    
    /**
     * 邀请码描述
     */
    private String description;
    
    /**
     * 分享链接
     */
    private String shareUrl;
    
    /**
     * 二维码图片URL
     */
    private String qrCodeUrl;
    
    /**
     * 邀请码创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdAt;
    
    /**
     * 最后使用时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String lastUsedAt;
    
    // Constructors
    public InviteCode() {}
    
    public InviteCode(Long ownerId, String code, String status) {
        this.ownerId = ownerId;
        this.code = code;
        this.status = status;
        this.usedCount = 0;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
    
    public String getOwnerUsername() {
        return ownerUsername;
    }
    
    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }
    
    public String getOwnerRealName() {
        return ownerRealName;
    }
    
    public void setOwnerRealName(String ownerRealName) {
        this.ownerRealName = ownerRealName;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getCodeType() {
        return codeType;
    }
    
    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getUsageLimit() {
        return usageLimit;
    }
    
    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }
    
    public Integer getUsedCount() {
        return usedCount;
    }
    
    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }
    
    public String getValidFrom() {
        return validFrom;
    }
    
    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }
    
    public String getValidUntil() {
        return validUntil;
    }
    
    public void setValidUntil(String validUntil) {
        this.validUntil = validUntil;
    }
    
    public BigDecimal getRewardAmount() {
        return rewardAmount;
    }
    
    public void setRewardAmount(BigDecimal rewardAmount) {
        this.rewardAmount = rewardAmount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getShareUrl() {
        return shareUrl;
    }
    
    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }
    
    public String getQrCodeUrl() {
        return qrCodeUrl;
    }
    
    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getLastUsedAt() {
        return lastUsedAt;
    }
    
    public void setLastUsedAt(String lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InviteCode that = (InviteCode) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(ownerId, that.ownerId) &&
               Objects.equals(code, that.code);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, ownerId, code);
    }
    
    @Override
    public String toString() {
        return "InviteCode{" +
                "id=" + id +
                ", ownerId=" + ownerId +
                ", ownerUsername='" + ownerUsername + '\'' +
                ", code='" + code + '\'' +
                ", codeType='" + codeType + '\'' +
                ", status='" + status + '\'' +
                ", usageLimit=" + usageLimit +
                ", usedCount=" + usedCount +
                ", rewardAmount=" + rewardAmount +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}