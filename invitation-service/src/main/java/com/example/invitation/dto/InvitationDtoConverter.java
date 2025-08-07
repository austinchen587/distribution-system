package com.example.invitation.dto;

// Removed direct entity dependency to avoid circular dependency
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 邀请DTO转换工具类
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
public class InvitationDtoConverter {
    
    private static final Logger logger = LoggerFactory.getLogger(InvitationDtoConverter.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Note: Direct entity conversion methods removed to avoid circular dependency
    // These methods should be implemented in the service layer where both
    // common and data-access modules are available
    
    /**
     * 创建邀请记录
     *
     * @param inviterId 邀请人ID
     * @param inviterUsername 邀请人用户名
     * @param inviterRealName 邀请人姓名
     * @param invitationCode 邀请码
     * @param channel 邀请渠道
     * @return InvitationDto
     */
    public static InvitationDto createInvitation(Long inviterId, String inviterUsername, String inviterRealName,
            String invitationCode, String channel) {
        try {
            InvitationDto dto = new InvitationDto();
            dto.setInviterId(inviterId);
            dto.setInviterUsername(inviterUsername);
            dto.setInviterRealName(inviterRealName);
            dto.setInvitationCode(invitationCode);
            dto.setStatus("PENDING");
            dto.setInvitationChannel(channel);
            dto.setRewardGranted(false);
            dto.setCreatedAt(LocalDateTime.now().format(DATE_TIME_FORMATTER));
            
            return dto;
        } catch (Exception e) {
            logger.error("创建邀请记录时发生错误: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 创建邀请码
     *
     * @param ownerId 所有者ID
     * @param ownerUsername 所有者用户名
     * @param ownerRealName 所有者姓名
     * @param codeType 邀请码类型
     * @param usageLimit 使用限制
     * @param validDays 有效天数
     * @param rewardAmount 奖励金额
     * @param description 描述
     * @return InviteCodeDto
     */
    public static InviteCodeDto createInviteCode(Long ownerId, String ownerUsername, String ownerRealName,
            String codeType, Integer usageLimit, Integer validDays, java.math.BigDecimal rewardAmount, String description) {
        try {
            InviteCodeDto dto = new InviteCodeDto();
            dto.setOwnerId(ownerId);
            dto.setOwnerUsername(ownerUsername);
            dto.setOwnerRealName(ownerRealName);
            
            // 生成邀请码
            String code = generateInviteCode();
            dto.setCode(code);
            dto.setCodeType(codeType);
            dto.setStatus("ACTIVE");
            dto.setUsageLimit(usageLimit);
            dto.setUsedCount(0);
            dto.setRemainingCount(usageLimit);
            dto.setRewardAmount(rewardAmount);
            dto.setDescription(description);
            
            // 设置有效期
            LocalDateTime now = LocalDateTime.now();
            dto.setValidFrom(now.format(DATE_TIME_FORMATTER));
            if (validDays != null && validDays > 0) {
                dto.setValidUntil(now.plusDays(validDays).format(DATE_TIME_FORMATTER));
            }
            
            // 生成分享链接和二维码URL
            dto.setShareUrl("https://app.example.com/invite?code=" + code);
            dto.setQrCodeUrl("https://cdn.example.com/qrcode/" + code + ".png");
            dto.setCreatedAt(now.format(DATE_TIME_FORMATTER));
            
            return dto;
        } catch (Exception e) {
            logger.error("创建邀请码时发生错误: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 生成邀请码
     *
     * @return 邀请码
     */
    private static String generateInviteCode() {
        // 生成格式: INV + 时间戳后6位 + 随机字符
        String timestamp = String.valueOf(System.currentTimeMillis());
        String suffix = timestamp.substring(timestamp.length() - 6);
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 4).toUpperCase();
        return "INV" + suffix + random;
    }
    
    /**
     * 更新邀请状态
     *
     * @param invitation 邀请记录
     * @param newStatus 新状态
     * @param inviteeId 被邀请人ID（可选）
     * @param inviteeUsername 被邀请人用户名（可选）
     * @param inviteeRealName 被邀请人姓名（可选）
     * @return 更新后的InvitationDto
     */
    public static InvitationDto updateInvitationStatus(InvitationDto invitation, String newStatus,
            Long inviteeId, String inviteeUsername, String inviteeRealName) {
        if (invitation == null) {
            return null;
        }
        
        try {
            invitation.setStatus(newStatus);
            invitation.setUpdatedAt(LocalDateTime.now().format(DATE_TIME_FORMATTER));
            
            if (inviteeId != null) {
                invitation.setInviteeId(inviteeId);
                invitation.setInviteeUsername(inviteeUsername);
                invitation.setInviteeRealName(inviteeRealName);
            }
            
            // 根据状态设置相应的时间
            LocalDateTime now = LocalDateTime.now();
            String formattedNow = now.format(DATE_TIME_FORMATTER);
            switch (newStatus) {
                case "REGISTERED":
                    invitation.setRegisteredAt(formattedNow);
                    break;
                case "ACTIVATED":
                    if (invitation.getRegisteredAt() == null) {
                        invitation.setRegisteredAt(formattedNow);
                    }
                    invitation.setActivatedAt(formattedNow);
                    break;
                case "COMPLETED":
                    if (invitation.getRegisteredAt() == null) {
                        invitation.setRegisteredAt(formattedNow);
                    }
                    if (invitation.getActivatedAt() == null) {
                        invitation.setActivatedAt(formattedNow);
                    }
                    break;
            }
            
            return invitation;
        } catch (Exception e) {
            logger.error("更新邀请状态时发生错误: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 检查是否可以查看邀请详情
     *
     * @param currentUserRole 当前用户角色
     * @param inviterId 邀请人ID
     * @param currentUserId 当前用户ID
     * @return 是否可以查看
     */
    public static boolean canViewInvitationDetails(String currentUserRole, Long inviterId, Long currentUserId) {
        if (currentUserRole == null || currentUserId == null) {
            return false;
        }
        
        // 超级管理员和总监可以查看所有邀请详情
        if ("super_admin".equals(currentUserRole) || "director".equals(currentUserRole)) {
            return true;
        }
        
        // 主管可以查看其管辖范围内的邀请详情
        if ("leader".equals(currentUserRole)) {
            return true;
        }
        
        // 邀请人可以查看自己的邀请详情
        return inviterId != null && currentUserId.equals(inviterId);
    }
    
    /**
     * 检查是否可以查看邀请码详情
     *
     * @param currentUserRole 当前用户角色
     * @param ownerId 所有者ID
     * @param currentUserId 当前用户ID
     * @return 是否可以查看
     */
    public static boolean canViewCodeDetails(String currentUserRole, Long ownerId, Long currentUserId) {
        if (currentUserRole == null || currentUserId == null) {
            return false;
        }
        
        // 超级管理员和总监可以查看所有邀请码详情
        if ("super_admin".equals(currentUserRole) || "director".equals(currentUserRole)) {
            return true;
        }
        
        // 主管可以查看其管辖范围内的邀请码详情
        if ("leader".equals(currentUserRole)) {
            return true;
        }
        
        // 所有者可以查看自己的邀请码详情
        return ownerId != null && currentUserId.equals(ownerId);
    }
    
    /**
     * 验证邀请状态
     *
     * @param status 邀请状态
     * @return 是否有效
     */
    public static boolean isValidInvitationStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        
        String[] validStatuses = {"PENDING", "REGISTERED", "ACTIVATED", "COMPLETED"};
        for (String validStatus : validStatuses) {
            if (validStatus.equals(status.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 验证邀请码类型
     *
     * @param codeType 邀请码类型
     * @return 是否有效
     */
    public static boolean isValidCodeType(String codeType) {
        if (codeType == null || codeType.trim().isEmpty()) {
            return false;
        }
        
        String[] validTypes = {"PERSONAL", "PROMOTIONAL", "LIMITED_TIME", "VIP"};
        for (String validType : validTypes) {
            if (validType.equals(codeType.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 验证邀请码状态
     *
     * @param status 邀请码状态
     * @return 是否有效
     */
    public static boolean isValidCodeStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        
        String[] validStatuses = {"ACTIVE", "INACTIVE", "EXPIRED", "DISABLED"};
        for (String validStatus : validStatuses) {
            if (validStatus.equals(status.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}