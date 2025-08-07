package com.example.invitation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 邀请码DTO - 用于邀请码管理和展示
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
@Schema(description = "邀请码")
public class InviteCodeDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 邀请码记录ID
     */
    @Schema(description = "邀请码记录ID", example = "1")
    @NotNull
    private Long id;
    
    /**
     * 邀请码所有者用户ID
     */
    @Schema(description = "邀请码所有者用户ID", example = "10")
    @NotNull
    private Long ownerId;
    
    /**
     * 邀请码所有者用户名
     */
    @Schema(description = "邀请码所有者用户名", example = "agent001")
    private String ownerUsername;
    
    /**
     * 邀请码所有者真实姓名
     */
    @Schema(description = "邀请码所有者真实姓名", example = "张三")
    private String ownerRealName;
    
    /**
     * 邀请码
     */
    @Schema(description = "邀请码", example = "INV123456")
    @NotBlank
    private String code;
    
    /**
     * 邀请码类型
     */
    @Schema(description = "邀请码类型", example = "PERSONAL", 
            allowableValues = {"PERSONAL", "PROMOTIONAL", "LIMITED_TIME", "VIP"})
    private String codeType;
    
    /**
     * 邀请码状态
     */
    @Schema(description = "邀请码状态", example = "ACTIVE", 
            allowableValues = {"ACTIVE", "INACTIVE", "EXPIRED", "DISABLED"})
    private String status;
    
    /**
     * 使用次数限制
     */
    @Schema(description = "使用次数限制", example = "100")
    private Integer usageLimit;
    
    /**
     * 已使用次数
     */
    @Schema(description = "已使用次数", example = "25")
    private Integer usedCount;
    
    /**
     * 剩余使用次数
     */
    @Schema(description = "剩余使用次数", example = "75")
    private Integer remainingCount;
    
    /**
     * 邀请码生效时间
     */
    @Schema(description = "邀请码生效时间", example = "2025-08-01 00:00:00")
    private String validFrom;
    
    /**
     * 邀请码失效时间
     */
    @Schema(description = "邀请码失效时间", example = "2025-12-31 23:59:59")
    private String validUntil;
    
    /**
     * 奖励金额
     */
    @Schema(description = "奖励金额", example = "50.00")
    private java.math.BigDecimal rewardAmount;
    
    /**
     * 邀请码描述
     */
    @Schema(description = "邀请码描述", example = "个人专属邀请码")
    private String description;
    
    /**
     * 分享链接
     */
    @Schema(description = "分享链接", example = "https://app.example.com/invite?code=INV123456")
    private String shareUrl;
    
    /**
     * 二维码图片URL
     */
    @Schema(description = "二维码图片URL", example = "https://cdn.example.com/qrcode/INV123456.png")
    private String qrCodeUrl;
    
    /**
     * 邀请码创建时间
     */
    @Schema(description = "邀请码创建时间", example = "2025-08-01 10:00:00")
    private String createdAt;
    
    /**
     * 最后使用时间
     */
    @Schema(description = "最后使用时间", example = "2025-08-04 15:30:00")
    private String lastUsedAt;
    
    // Constructors
    public InviteCodeDto() {}
    
    public InviteCodeDto(Long id, Long ownerId, String code, String status) {
        this.id = id;
        this.ownerId = ownerId;
        this.code = code;
        this.status = status;
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
    
    public Integer getRemainingCount() {
        return remainingCount;
    }
    
    public void setRemainingCount(Integer remainingCount) {
        this.remainingCount = remainingCount;
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
    
    public java.math.BigDecimal getRewardAmount() {
        return rewardAmount;
    }
    
    public void setRewardAmount(java.math.BigDecimal rewardAmount) {
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
    
    /**
     * 检查邀请码是否有效
     * 
     * @return 是否有效
     */
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    /**
     * 检查邀请码是否已过期
     * 
     * @return 是否已过期
     */
    public boolean isExpired() {
        return "EXPIRED".equals(status);
    }
    
    /**
     * 检查邀请码是否已禁用
     * 
     * @return 是否已禁用
     */
    public boolean isDisabled() {
        return "DISABLED".equals(status);
    }
    
    /**
     * 检查邀请码是否还有剩余使用次数
     * 
     * @return 是否还有剩余使用次数
     */
    public boolean hasRemainingUsage() {
        if (usageLimit == null) {
            return true; // 无限制
        }
        return remainingCount != null && remainingCount > 0;
    }
    
    /**
     * 计算使用率
     * 
     * @return 使用率（0-1之间的小数）
     */
    public Double getUsageRate() {
        if (usageLimit == null || usageLimit == 0) {
            return 0.0;
        }
        if (usedCount == null) {
            return 0.0;
        }
        return usedCount.doubleValue() / usageLimit.doubleValue();
    }
    
    /**
     * 检查是否为个人邀请码
     * 
     * @return 是否为个人邀请码
     */
    public boolean isPersonalCode() {
        return "PERSONAL".equals(codeType);
    }
    
    /**
     * 检查是否为推广邀请码
     * 
     * @return 是否为推广邀请码
     */
    public boolean isPromotionalCode() {
        return "PROMOTIONAL".equals(codeType);
    }
    
    @Override
    public String toString() {
        return "InviteCodeDto{" +
                "id=" + id +
                ", ownerId=" + ownerId +
                ", ownerUsername='" + ownerUsername + '\'' +
                ", code='" + code + '\'' +
                ", codeType='" + codeType + '\'' +
                ", status='" + status + '\'' +
                ", usageLimit=" + usageLimit +
                ", usedCount=" + usedCount +
                ", remainingCount=" + remainingCount +
                ", rewardAmount=" + rewardAmount +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}