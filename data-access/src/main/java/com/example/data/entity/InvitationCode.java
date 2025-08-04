package com.example.data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 邀请码管理实体类
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-03
 */
public class InvitationCode {
    
    /**
     * 邀请码主键ID
     */
    private Long id;
    
    /**
     * 邀请码创建者ID
     */
    @NotNull(message = "创建者ID不能为空")
    private Long userId;
    
    /**
     * 唯一的邀请码字符串
     */
    @NotBlank(message = "邀请码不能为空")
    @Size(max = 32, message = "邀请码长度不能超过32字符")
    private String code;
    
    /**
     * 该邀请码允许注册的目标角色
     */
    @NotBlank(message = "目标角色不能为空")
    @Size(max = 50, message = "目标角色长度不能超过50字符")
    private String targetRole;
    
    /**
     * 邀请码状态
     */
    @NotNull(message = "邀请码状态不能为空")
    private InvitationCodeStatus status = InvitationCodeStatus.ACTIVE;
    
    /**
     * 已被使用的次数
     */
    @Min(value = 0, message = "使用次数不能为负数")
    private Integer usageCount = 0;
    
    /**
     * 最大使用次数限制
     */
    @Min(value = 1, message = "最大使用次数必须大于0")
    private Integer maxUses;
    
    /**
     * 邀请码过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiresAt;
    
    /**
     * 记录创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    /**
     * 记录最后更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 邀请码状态枚举
    public enum InvitationCodeStatus {
        ACTIVE("active", "可用"),
        INACTIVE("inactive", "停用");
        
        private final String code;
        private final String description;
        
        InvitationCodeStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        public static InvitationCodeStatus fromCode(String code) {
            for (InvitationCodeStatus status : values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown invitation code status: " + code);
        }
    }
    
    // Constructors
    public InvitationCode() {}
    
    public InvitationCode(Long userId, String code, String targetRole, Integer maxUses, LocalDateTime expiresAt) {
        this.userId = userId;
        this.code = code;
        this.targetRole = targetRole;
        this.maxUses = maxUses;
        this.expiresAt = expiresAt;
        this.status = InvitationCodeStatus.ACTIVE;
        this.usageCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getTargetRole() {
        return targetRole;
    }
    
    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }
    
    public InvitationCodeStatus getStatus() {
        return status;
    }
    
    public void setStatus(InvitationCodeStatus status) {
        this.status = status;
    }
    
    public Integer getUsageCount() {
        return usageCount;
    }
    
    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }
    
    public Integer getMaxUses() {
        return maxUses;
    }
    
    public void setMaxUses(Integer maxUses) {
        this.maxUses = maxUses;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Business Methods
    
    /**
     * 检查邀请码是否可用
     * 
     * @return 是否可用
     */
    public boolean isActive() {
        return InvitationCodeStatus.ACTIVE.equals(this.status);
    }
    
    /**
     * 检查邀请码是否已过期
     * 
     * @return 是否已过期
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    /**
     * 检查邀请码使用次数是否已达上限
     * 
     * @return 是否已达上限
     */
    public boolean isUsageExhausted() {
        return maxUses != null && usageCount >= maxUses;
    }
    
    /**
     * 检查邀请码是否可以使用
     * 
     * @return 是否可以使用
     */
    public boolean canBeUsed() {
        return isActive() && !isExpired() && !isUsageExhausted();
    }
    
    /**
     * 获取剩余使用次数
     * 
     * @return 剩余使用次数，无限制时返回null
     */
    public Integer getRemainingUses() {
        if (maxUses == null) {
            return null; // 无限制
        }
        return Math.max(0, maxUses - usageCount);
    }
    
    /**
     * 增加使用次数
     * 
     * @return 是否成功增加
     */
    public boolean incrementUsage() {
        if (!canBeUsed()) {
            return false;
        }
        this.usageCount++;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    /**
     * 停用邀请码
     */
    public void deactivate() {
        this.status = InvitationCodeStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 激活邀请码
     */
    public void activate() {
        this.status = InvitationCodeStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 设置更新时间为当前时间
     */
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvitationCode that = (InvitationCode) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(code, that.code);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }
    
    @Override
    public String toString() {
        return "InvitationCode{" +
                "id=" + id +
                ", userId=" + userId +
                ", code='" + code + '\'' +
                ", targetRole='" + targetRole + '\'' +
                ", status=" + status +
                ", usageCount=" + usageCount +
                ", maxUses=" + maxUses +
                ", expiresAt=" + expiresAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}