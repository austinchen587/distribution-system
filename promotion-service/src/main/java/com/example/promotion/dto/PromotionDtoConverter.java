package com.example.promotion.dto;

// Removed direct entity dependency to avoid circular dependency
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 推广DTO转换工具类
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
public class PromotionDtoConverter {
    
    private static final Logger logger = LoggerFactory.getLogger(PromotionDtoConverter.class);
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Note: Direct entity conversion methods removed to avoid circular dependency
    // These methods should be implemented in the service layer where both
    // common and data-access modules are available
    
    /**
     * 创建推广审计DTO
     *
     * @param promotionId 推广活动ID
     * @param operationType 操作类型
     * @param oldStatus 原状态
     * @param newStatus 新状态
     * @param reason 审计原因
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @return PromotionAuditDto
     */
    public static PromotionAuditDto createAuditDto(Long promotionId, String operationType, 
            String oldStatus, String newStatus, String reason, Long operatorId, String operatorName) {
        try {
            PromotionAuditDto dto = new PromotionAuditDto();
            dto.setId(promotionId);
            dto.setAuditStatus(newStatus);
            dto.setManualAuditComment(reason);
            dto.setManualAuditorId(operatorId);
            dto.setManualAuditorUsername(operatorName);
            dto.setUpdatedAt(LocalDateTime.now().format(DATE_TIME_FORMATTER));
            
            return dto;
        } catch (Exception e) {
            logger.error("创建推广审计DTO时发生错误: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 检查是否可以查看敏感信息
     *
     * @param currentUserRole 当前用户角色
     * @param createdBy 创建人ID
     * @param currentUserId 当前用户ID
     * @return 是否可以查看
     */
    public static boolean canViewSensitiveInfo(String currentUserRole, Long createdBy, Long currentUserId) {
        if (currentUserRole == null || currentUserId == null) {
            return false;
        }
        
        // 超级管理员和总监可以查看所有敏感信息
        if ("super_admin".equals(currentUserRole) || "director".equals(currentUserRole)) {
            return true;
        }
        
        // 主管可以查看其管辖范围内的推广活动信息
        if ("leader".equals(currentUserRole)) {
            return true;
        }
        
        // 创建人可以查看自己创建的推广活动信息
        return createdBy != null && currentUserId.equals(createdBy);
    }
    
    /**
     * 检查是否可以审核推广活动
     *
     * @param currentUserRole 当前用户角色
     * @return 是否可以审核
     */
    public static boolean canAuditPromotion(String currentUserRole) {
        if (currentUserRole == null) {
            return false;
        }
        
        // 只有超级管理员和总监可以审核推广活动
        return "super_admin".equals(currentUserRole) || "director".equals(currentUserRole);
    }
    
    /**
     * 验证推广活动类型
     *
     * @param type 推广活动类型
     * @return 是否有效
     */
    public static boolean isValidPromotionType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return false;
        }
        
        String[] validTypes = {"DISCOUNT", "CASHBACK", "GIFT", "POINTS", "COUPON"};
        for (String validType : validTypes) {
            if (validType.equals(type.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 验证推广活动状态
     *
     * @param status 推广活动状态
     * @return 是否有效
     */
    public static boolean isValidPromotionStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        
        String[] validStatuses = {"PENDING", "APPROVED", "ACTIVE", "PAUSED", "COMPLETED", "CANCELLED", "REJECTED"};
        for (String validStatus : validStatuses) {
            if (validStatus.equals(status.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 验证折扣类型
     *
     * @param discountType 折扣类型
     * @return 是否有效
     */
    public static boolean isValidDiscountType(String discountType) {
        if (discountType == null || discountType.trim().isEmpty()) {
            return false;
        }
        
        String[] validTypes = {"PERCENTAGE", "FIXED_AMOUNT", "FREE_SHIPPING"};
        for (String validType : validTypes) {
            if (validType.equals(discountType.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}