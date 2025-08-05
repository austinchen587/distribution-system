package com.example.level.dto.agent;

// Removed direct entity dependency to avoid circular dependency
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 代理DTO转换工具类
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
public class AgentDtoConverter {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentDtoConverter.class);
    
    // Note: Direct entity conversion methods removed to avoid circular dependency
    // These methods should be implemented in the service layer where both
    // common and data-access modules are available
    
    /**
     * 创建代理绩效DTO（基于参数构建，不依赖实体）
     *
     * @param id 代理ID
     * @param userId 用户ID
     * @param username 用户名
     * @param realName 真实姓名
     * @param agentCode 代理编码
     * @param level 等级
     * @param performanceScore 绩效评分
     * @param managedCustomerCount 管理客户数量
     * @param totalDeals 总成交数
     * @param totalCommission 总佣金
     * @param currentMonthCommission 当月佣金
     * @param totalRevenue 总营收
     * @param monthlyRevenue 月度营收
     * @param conversionRate 转化率
     * @param customerSatisfaction 客户满意度
     * @return AgentPerformanceDto
     */
    public static AgentPerformanceDto createPerformanceDto(Long id, Long userId, String username, String realName,
            String agentCode, String level, BigDecimal performanceScore, Integer managedCustomerCount, 
            Integer totalDeals, BigDecimal totalCommission, BigDecimal currentMonthCommission,
            BigDecimal totalRevenue, BigDecimal monthlyRevenue, Double conversionRate, Double customerSatisfaction) {
        try {
            AgentPerformanceDto dto = new AgentPerformanceDto();
            dto.setId(id);
            dto.setUserId(userId);
            dto.setUsername(username);
            dto.setRealName(realName);
            dto.setAgentCode(agentCode);
            dto.setLevel(level);
            dto.setPerformanceScore(performanceScore);
            dto.setManagedCustomerCount(managedCustomerCount);
            dto.setTotalDeals(totalDeals);
            dto.setTotalCommission(totalCommission);
            dto.setCurrentMonthCommission(currentMonthCommission);
            dto.setTotalRevenue(totalRevenue);
            dto.setMonthlyRevenue(monthlyRevenue);
            dto.setConversionRate(conversionRate != null ? BigDecimal.valueOf(conversionRate) : null);
            dto.setCustomerSatisfaction(customerSatisfaction);
            
            return dto;
        } catch (Exception e) {
            logger.error("创建代理绩效DTO时发生错误: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 创建代理等级DTO
     *
     * @param agentLevel 代理等级
     * @param levelName 等级名称
     * @param requiredScore 所需积分
     * @param commissionRate 佣金比例
     * @param privileges 特权描述
     * @return AgentLevelDto
     */
    public static AgentLevelDto toLevelDto(String agentLevel, String levelName, 
            Integer requiredScore, BigDecimal commissionRate, String privileges) {
        try {
            AgentLevelDto dto = new AgentLevelDto();
            dto.setLevel(agentLevel);
            dto.setLevelName(levelName);
            dto.setRequiredScore(requiredScore);
            dto.setCommissionRate(commissionRate);
            dto.setPrivileges(privileges);
            
            return dto;
        } catch (Exception e) {
            logger.error("创建代理等级DTO时发生错误: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 检查是否可以查看敏感信息
     *
     * @param currentUserRole 当前用户角色
     * @param targetUserId 目标用户ID
     * @param currentUserId 当前用户ID
     * @return 是否可以查看
     */
    public static boolean canViewSensitiveInfo(String currentUserRole, Long targetUserId, Long currentUserId) {
        if (currentUserRole == null || targetUserId == null || currentUserId == null) {
            return false;
        }
        
        // 超级管理员和总监可以查看所有敏感信息
        if ("super_admin".equals(currentUserRole) || "director".equals(currentUserRole)) {
            return true;
        }
        
        // 主管可以查看其管辖下的代理信息
        if ("leader".equals(currentUserRole)) {
            // 这里需要实际的权限检查逻辑，暂时返回true
            return true;
        }
        
        // 用户只能查看自己的敏感信息
        return currentUserId.equals(targetUserId);
    }
    
    /**
     * 检查是否可以查看绩效信息
     *
     * @param currentUserRole 当前用户角色
     * @param targetUserId 目标用户ID
     * @param currentUserId 当前用户ID
     * @return 是否可以查看
     */
    public static boolean canViewPerformanceInfo(String currentUserRole, Long targetUserId, Long currentUserId) {
        if (currentUserRole == null || targetUserId == null || currentUserId == null) {
            return false;
        }
        
        // 超级管理员、总监和主管可以查看绩效信息
        if ("super_admin".equals(currentUserRole) || "director".equals(currentUserRole) || "leader".equals(currentUserRole)) {
            return true;
        }
        
        // 代理只能查看自己的绩效信息
        return currentUserId.equals(targetUserId);
    }
    
    /**
     * 验证代理等级
     *
     * @param level 代理等级
     * @return 是否有效
     */
    public static boolean isValidAgentLevel(String level) {
        if (level == null || level.trim().isEmpty()) {
            return false;
        }
        
        String[] validLevels = {"BRONZE", "SILVER", "GOLD", "PLATINUM", "DIAMOND"};
        for (String validLevel : validLevels) {
            if (validLevel.equals(level.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 验证代理状态
     *
     * @param status 代理状态
     * @return 是否有效
     */
    public static boolean isValidAgentStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        
        String[] validStatuses = {"ACTIVE", "INACTIVE", "SUSPENDED", "TERMINATED"};
        for (String validStatus : validStatuses) {
            if (validStatus.equals(status.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}