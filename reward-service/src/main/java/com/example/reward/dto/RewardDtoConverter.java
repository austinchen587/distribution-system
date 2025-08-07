package com.example.reward.dto;

// Removed direct entity dependency to avoid circular dependency
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 奖励DTO转换工具类
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
public class RewardDtoConverter {
    
    private static final Logger logger = LoggerFactory.getLogger(RewardDtoConverter.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Note: Direct entity conversion methods removed to avoid circular dependency
    // These methods should be implemented in the service layer where both
    // common and data-access modules are available
    
    /**
     * 创建结算DTO
     *
     * @param agentId 代理ID
     * @param agentUsername 代理用户名
     * @param periodStartTime 结算周期开始时间
     * @param periodEndTime 结算周期结束时间
     * @param rewards 奖励列表
     * @return SettlementDto
     */
    public static SettlementDto createSettlementDto(Long agentId, String agentUsername, 
            LocalDateTime periodStartTime, LocalDateTime periodEndTime, List<RewardDto> rewards) {
        if (agentId == null || rewards == null) {
            return null;
        }
        
        try {
            SettlementDto dto = new SettlementDto();
            dto.setAgentId(agentId);
            dto.setAgentUsername(agentUsername);
            dto.setPeriodStartTime(periodStartTime);
            dto.setPeriodEndTime(periodEndTime);
            dto.setRewards(rewards);
            dto.setRewardCount(rewards.size());
            dto.setStatus("PENDING");
            dto.setCreatedAt(LocalDateTime.now());
            
            // 计算各项金额
            BigDecimal totalCommission = BigDecimal.ZERO;
            BigDecimal totalPromotionReward = BigDecimal.ZERO;
            BigDecimal totalReferralBonus = BigDecimal.ZERO;
            BigDecimal performanceBonus = BigDecimal.ZERO;
            
            for (RewardDto reward : rewards) {
                if (reward.getAmount() != null) {
                    switch (reward.getType()) {
                        case "COMMISSION":
                            totalCommission = totalCommission.add(reward.getAmount());
                            break;
                        case "PROMOTION_REWARD":
                            totalPromotionReward = totalPromotionReward.add(reward.getAmount());
                            break;
                        case "REFERRAL_BONUS":
                            totalReferralBonus = totalReferralBonus.add(reward.getAmount());
                            break;
                        case "PERFORMANCE_BONUS":
                            performanceBonus = performanceBonus.add(reward.getAmount());
                            break;
                    }
                }
            }
            
            dto.setTotalCommission(totalCommission);
            dto.setTotalPromotionReward(totalPromotionReward);
            dto.setTotalReferralBonus(totalReferralBonus);
            dto.setPerformanceBonus(performanceBonus);
            
            // 计算总结算金额
            BigDecimal totalAmount = totalCommission
                    .add(totalPromotionReward)
                    .add(totalReferralBonus)
                    .add(performanceBonus);
                    
            if (dto.getBaseSalary() != null) {
                totalAmount = totalAmount.add(dto.getBaseSalary());
            }
            
            if (dto.getTotalDeduction() != null) {
                totalAmount = totalAmount.subtract(dto.getTotalDeduction());
            }
            
            dto.setTotalSettlementAmount(totalAmount);
            dto.setActualPaymentAmount(totalAmount);
            
            return dto;
        } catch (Exception e) {
            logger.error("创建结算DTO时发生错误: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 创建佣金奖励
     *
     * @param agentId 代理ID
     * @param agentUsername 代理用户名
     * @param agentRealName 代理姓名
     * @param amount 佣金金额
     * @param sourceId 来源ID（如订单ID）
     * @param description 描述
     * @return RewardDto
     */
    public static RewardDto createCommissionReward(Long agentId, String agentUsername, String agentRealName,
            BigDecimal amount, Long sourceId, String description) {
        return createReward(agentId, agentUsername, agentRealName, "COMMISSION", amount, 
                "ORDER", sourceId, description, "按订单金额比例计算");
    }
    
    /**
     * 创建推广奖励
     *
     * @param agentId 代理ID
     * @param agentUsername 代理用户名
     * @param agentRealName 代理姓名
     * @param amount 奖励金额
     * @param sourceId 来源ID（如推广活动ID）
     * @param description 描述
     * @return RewardDto
     */
    public static RewardDto createPromotionReward(Long agentId, String agentUsername, String agentRealName,
            BigDecimal amount, Long sourceId, String description) {
        return createReward(agentId, agentUsername, agentRealName, "PROMOTION_REWARD", amount, 
                "PROMOTION", sourceId, description, "推广活动奖励");
    }
    
    /**
     * 创建邀请奖励
     *
     * @param agentId 代理ID
     * @param agentUsername 代理用户名
     * @param agentRealName 代理姓名
     * @param amount 奖励金额
     * @param sourceId 来源ID（如邀请记录ID）
     * @param description 描述
     * @return RewardDto
     */
    public static RewardDto createReferralBonus(Long agentId, String agentUsername, String agentRealName,
            BigDecimal amount, Long sourceId, String description) {
        return createReward(agentId, agentUsername, agentRealName, "REFERRAL_BONUS", amount, 
                "INVITATION", sourceId, description, "邀请用户奖励");
    }
    
    /**
     * 创建奖励记录
     *
     * @param agentId 代理ID
     * @param agentUsername 代理用户名
     * @param agentRealName 代理姓名
     * @param type 奖励类型
     * @param amount 奖励金额
     * @param source 来源类型
     * @param sourceId 来源ID
     * @param description 描述
     * @param calculationRule 计算规则
     * @return RewardDto
     */
    private static RewardDto createReward(Long agentId, String agentUsername, String agentRealName,
            String type, BigDecimal amount, String source, Long sourceId, String description, String calculationRule) {
        try {
            RewardDto dto = new RewardDto();
            dto.setAgentId(agentId);
            dto.setAgentUsername(agentUsername);
            dto.setAgentRealName(agentRealName);
            dto.setType(type);
            dto.setAmount(amount);
            dto.setSource(source);
            dto.setSourceId(sourceId);
            dto.setDescription(description);
            dto.setCalculationRule(calculationRule);
            dto.setStatus("PENDING");
            dto.setCreatedAt(LocalDateTime.now());
            
            return dto;
        } catch (Exception e) {
            logger.error("创建奖励记录时发生错误: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 检查是否可以查看奖励详情
     *
     * @param currentUserRole 当前用户角色
     * @param agentId 代理ID
     * @param currentUserId 当前用户ID
     * @return 是否可以查看
     */
    public static boolean canViewRewardDetails(String currentUserRole, Long agentId, Long currentUserId) {
        if (currentUserRole == null || currentUserId == null) {
            return false;
        }
        
        // 超级管理员和总监可以查看所有奖励详情
        if ("super_admin".equals(currentUserRole) || "director".equals(currentUserRole)) {
            return true;
        }
        
        // 主管可以查看其管辖范围内的奖励详情
        if ("leader".equals(currentUserRole)) {
            return true;
        }
        
        // 代理只能查看自己的奖励详情
        return agentId != null && currentUserId.equals(agentId);
    }
    
    /**
     * 检查是否可以处理结算
     *
     * @param currentUserRole 当前用户角色
     * @return 是否可以处理
     */
    public static boolean canProcessSettlement(String currentUserRole) {
        if (currentUserRole == null) {
            return false;
        }
        
        // 只有超级管理员和总监可以处理结算
        return "super_admin".equals(currentUserRole) || "director".equals(currentUserRole);
    }
    
    /**
     * 验证奖励类型
     *
     * @param type 奖励类型
     * @return 是否有效
     */
    public static boolean isValidRewardType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return false;
        }
        
        String[] validTypes = {"COMMISSION", "PROMOTION_REWARD", "REFERRAL_BONUS", "PERFORMANCE_BONUS", "BASE_SALARY", "EXTRA_BONUS"};
        for (String validType : validTypes) {
            if (validType.equals(type.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 验证奖励状态
     *
     * @param status 奖励状态
     * @return 是否有效
     */
    public static boolean isValidRewardStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        
        String[] validStatuses = {"PENDING", "APPROVED", "PAID", "CANCELLED", "EXPIRED"};
        for (String validStatus : validStatuses) {
            if (validStatus.equals(status.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 验证结算状态
     *
     * @param status 结算状态
     * @return 是否有效
     */
    public static boolean isValidSettlementStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        
        String[] validStatuses = {"PENDING", "PROCESSING", "COMPLETED", "FAILED", "CANCELLED"};
        for (String validStatus : validStatuses) {
            if (validStatus.equals(status.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}