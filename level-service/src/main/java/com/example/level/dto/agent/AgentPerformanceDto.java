package com.example.level.dto.agent;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 代理业绩信息DTO - 包含完整的业绩统计和KPI数据
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
@Schema(description = "代理业绩信息")
public class AgentPerformanceDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 代理ID
     */
    @Schema(description = "代理ID", example = "1")
    private Long id;
    
    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "10")
    private Long userId;
    
    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "agent001")
    private String username;
    
    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名", example = "张三")
    private String realName;
    
    /**
     * 代理编码
     */
    @Schema(description = "代理编码", example = "AG001")
    private String agentCode;
    
    /**
     * 代理等级
     */
    @Schema(description = "代理等级", example = "GOLD")
    private String level;
    
    /**
     * 绩效评分
     */
    @Schema(description = "绩效评分", example = "85.5")
    private BigDecimal performanceScore;
    
    /**
     * 管理客户数量
     */
    @Schema(description = "管理客户数量", example = "50")
    private Integer managedCustomerCount;
    
    /**
     * 代理基础信息
     */
    @Schema(description = "代理基础信息")
    private AgentDto agentInfo;
    
    /**
     * 当月GMV
     */
    @Schema(description = "当月GMV", example = "50000.00")
    private BigDecimal currentMonthGmv;
    
    /**
     * 累计GMV
     */
    @Schema(description = "累计GMV", example = "500000.00")
    private BigDecimal totalGmv;
    
    /**
     * 当月佣金收入
     */
    @Schema(description = "当月佣金收入", example = "7500.00")
    private BigDecimal currentMonthCommission;
    
    /**
     * 累计佣金收入
     */
    @Schema(description = "累计佣金收入", example = "75000.00")
    private BigDecimal totalCommission;
    
    /**
     * 当月新增客资数量
     */
    @Schema(description = "当月新增客资数量", example = "25")
    private Integer currentMonthLeads;
    
    /**
     * 累计客资数量
     */
    @Schema(description = "累计客资数量", example = "300")
    private Integer totalLeads;
    
    /**
     * 当月转化客资数量
     */
    @Schema(description = "当月转化客资数量", example = "8")
    private Integer currentMonthConversions;
    
    /**
     * 累计转化客资数量
     */
    @Schema(description = "累计转化客资数量", example = "120")
    private Integer totalConversions;
    
    /**
     * 转化率
     */
    @Schema(description = "转化率", example = "0.40")
    private BigDecimal conversionRate;
    
    /**
     * 当月成交订单数量
     */
    @Schema(description = "当月成交订单数量", example = "10")
    private Integer currentMonthDeals;
    
    /**
     * 累计成交订单数量
     */
    @Schema(description = "累计成交订单数量", example = "150")
    private Integer totalDeals;
    
    /**
     * 总营收
     */
    @Schema(description = "总营收", example = "500000.00")
    private BigDecimal totalRevenue;
    
    /**
     * 月度营收
     */
    @Schema(description = "月度营收", example = "50000.00")
    private BigDecimal monthlyRevenue;
    
    /**
     * 客户满意度
     */
    @Schema(description = "客户满意度", example = "4.5")
    private Double customerSatisfaction;
    
    /**
     * 平均订单金额
     */
    @Schema(description = "平均订单金额", example = "5000.00")
    private BigDecimal averageOrderValue;
    
    /**
     * 当月推广任务数量
     */
    @Schema(description = "当月推广任务数量", example = "15")
    private Integer currentMonthPromotions;
    
    /**
     * 当月推广任务通过数量
     */
    @Schema(description = "当月推广任务通过数量", example = "12")
    private Integer currentMonthApprovedPromotions;
    
    /**
     * 推广任务通过率
     */
    @Schema(description = "推广任务通过率", example = "0.80")
    private BigDecimal promotionApprovalRate;
    
    /**
     * 团队下级代理数量
     */
    @Schema(description = "团队下级代理数量", example = "5")
    private Integer teamSize;
    
    /**
     * 团队总业绩
     */
    @Schema(description = "团队总业绩", example = "200000.00")
    private BigDecimal teamTotalGmv;
    
    /**
     * 排名（在所有代理中的排名）
     */
    @Schema(description = "业绩排名", example = "15")
    private Integer ranking;
    
    /**
     * 等级达标状态
     */
    @Schema(description = "等级达标状态", example = "QUALIFIED", allowableValues = {"QUALIFIED", "AT_RISK", "NEED_IMPROVEMENT"})
    private String levelStatus;
    
    /**
     * 距离下一等级所需GMV
     */
    @Schema(description = "距离下一等级所需GMV", example = "50000.00")
    private BigDecimal gmvToNextLevel;
    
    /**
     * 统计周期起始时间
     */
    @Schema(description = "统计周期起始时间", example = "2025-08-01 00:00:00")
    private String periodStartTime;
    
    /**
     * 统计周期结束时间
     */
    @Schema(description = "统计周期结束时间", example = "2025-08-31 23:59:59")
    private String periodEndTime;
    
    // Constructors
    public AgentPerformanceDto() {}
    
    public AgentPerformanceDto(AgentDto agentInfo) {
        this.agentInfo = agentInfo;
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
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public String getAgentCode() {
        return agentCode;
    }
    
    public void setAgentCode(String agentCode) {
        this.agentCode = agentCode;
    }
    
    public String getLevel() {
        return level;
    }
    
    public void setLevel(String level) {
        this.level = level;
    }
    
    public BigDecimal getPerformanceScore() {
        return performanceScore;
    }
    
    public void setPerformanceScore(BigDecimal performanceScore) {
        this.performanceScore = performanceScore;
    }
    
    public Integer getManagedCustomerCount() {
        return managedCustomerCount;
    }
    
    public void setManagedCustomerCount(Integer managedCustomerCount) {
        this.managedCustomerCount = managedCustomerCount;
    }
    
    public AgentDto getAgentInfo() {
        return agentInfo;
    }
    
    public void setAgentInfo(AgentDto agentInfo) {
        this.agentInfo = agentInfo;
    }
    
    public BigDecimal getCurrentMonthGmv() {
        return currentMonthGmv;
    }
    
    public void setCurrentMonthGmv(BigDecimal currentMonthGmv) {
        this.currentMonthGmv = currentMonthGmv;
    }
    
    public BigDecimal getTotalGmv() {
        return totalGmv;
    }
    
    public void setTotalGmv(BigDecimal totalGmv) {
        this.totalGmv = totalGmv;
    }
    
    public BigDecimal getCurrentMonthCommission() {
        return currentMonthCommission;
    }
    
    public void setCurrentMonthCommission(BigDecimal currentMonthCommission) {
        this.currentMonthCommission = currentMonthCommission;
    }
    
    public BigDecimal getTotalCommission() {
        return totalCommission;
    }
    
    public void setTotalCommission(BigDecimal totalCommission) {
        this.totalCommission = totalCommission;
    }
    
    public Integer getCurrentMonthLeads() {
        return currentMonthLeads;
    }
    
    public void setCurrentMonthLeads(Integer currentMonthLeads) {
        this.currentMonthLeads = currentMonthLeads;
    }
    
    public Integer getTotalLeads() {
        return totalLeads;
    }
    
    public void setTotalLeads(Integer totalLeads) {
        this.totalLeads = totalLeads;
    }
    
    public Integer getCurrentMonthConversions() {
        return currentMonthConversions;
    }
    
    public void setCurrentMonthConversions(Integer currentMonthConversions) {
        this.currentMonthConversions = currentMonthConversions;
    }
    
    public Integer getTotalConversions() {
        return totalConversions;
    }
    
    public void setTotalConversions(Integer totalConversions) {
        this.totalConversions = totalConversions;
    }
    
    public BigDecimal getConversionRate() {
        return conversionRate;
    }
    
    public void setConversionRate(BigDecimal conversionRate) {
        this.conversionRate = conversionRate;
    }
    
    public Integer getCurrentMonthDeals() {
        return currentMonthDeals;
    }
    
    public void setCurrentMonthDeals(Integer currentMonthDeals) {
        this.currentMonthDeals = currentMonthDeals;
    }
    
    public Integer getTotalDeals() {
        return totalDeals;
    }
    
    public void setTotalDeals(Integer totalDeals) {
        this.totalDeals = totalDeals;
    }
    
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    
    public BigDecimal getMonthlyRevenue() {
        return monthlyRevenue;
    }
    
    public void setMonthlyRevenue(BigDecimal monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }
    
    public Double getCustomerSatisfaction() {
        return customerSatisfaction;
    }
    
    public void setCustomerSatisfaction(Double customerSatisfaction) {
        this.customerSatisfaction = customerSatisfaction;
    }
    
    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }
    
    public void setAverageOrderValue(BigDecimal averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }
    
    public Integer getCurrentMonthPromotions() {
        return currentMonthPromotions;
    }
    
    public void setCurrentMonthPromotions(Integer currentMonthPromotions) {
        this.currentMonthPromotions = currentMonthPromotions;
    }
    
    public Integer getCurrentMonthApprovedPromotions() {
        return currentMonthApprovedPromotions;
    }
    
    public void setCurrentMonthApprovedPromotions(Integer currentMonthApprovedPromotions) {
        this.currentMonthApprovedPromotions = currentMonthApprovedPromotions;
    }
    
    public BigDecimal getPromotionApprovalRate() {
        return promotionApprovalRate;
    }
    
    public void setPromotionApprovalRate(BigDecimal promotionApprovalRate) {
        this.promotionApprovalRate = promotionApprovalRate;
    }
    
    public Integer getTeamSize() {
        return teamSize;
    }
    
    public void setTeamSize(Integer teamSize) {
        this.teamSize = teamSize;
    }
    
    public BigDecimal getTeamTotalGmv() {
        return teamTotalGmv;
    }
    
    public void setTeamTotalGmv(BigDecimal teamTotalGmv) {
        this.teamTotalGmv = teamTotalGmv;
    }
    
    public Integer getRanking() {
        return ranking;
    }
    
    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }
    
    public String getLevelStatus() {
        return levelStatus;
    }
    
    public void setLevelStatus(String levelStatus) {
        this.levelStatus = levelStatus;
    }
    
    public BigDecimal getGmvToNextLevel() {
        return gmvToNextLevel;
    }
    
    public void setGmvToNextLevel(BigDecimal gmvToNextLevel) {
        this.gmvToNextLevel = gmvToNextLevel;
    }
    
    public String getPeriodStartTime() {
        return periodStartTime;
    }
    
    public void setPeriodStartTime(String periodStartTime) {
        this.periodStartTime = periodStartTime;
    }
    
    public String getPeriodEndTime() {
        return periodEndTime;
    }
    
    public void setPeriodEndTime(String periodEndTime) {
        this.periodEndTime = periodEndTime;
    }
    
    /**
     * 检查是否达标当前等级要求
     * 
     * @return 是否达标
     */
    public boolean isQualifiedForCurrentLevel() {
        return "QUALIFIED".equals(levelStatus);
    }
    
    /**
     * 检查是否有团队
     * 
     * @return 是否有团队
     */
    public boolean hasTeam() {
        return teamSize != null && teamSize > 0;
    }
    
    /**
     * 计算个人与团队GMV比例
     * 
     * @return 个人GMV占团队GMV的比例
     */
    public BigDecimal getPersonalToTeamGmvRatio() {
        if (teamTotalGmv == null || teamTotalGmv.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ONE;
        }
        if (totalGmv == null) {
            return BigDecimal.ZERO;
        }
        return totalGmv.divide(teamTotalGmv, 4, BigDecimal.ROUND_HALF_UP);
    }
    
    @Override
    public String toString() {
        return "AgentPerformanceDto{" +
                "agentInfo=" + agentInfo +
                ", currentMonthGmv=" + currentMonthGmv +
                ", totalGmv=" + totalGmv +
                ", currentMonthCommission=" + currentMonthCommission +
                ", totalCommission=" + totalCommission +
                ", conversionRate=" + conversionRate +
                ", ranking=" + ranking +
                ", levelStatus='" + levelStatus + '\'' +
                ", teamSize=" + teamSize +
                ", periodStartTime='" + periodStartTime + '\'' +
                ", periodEndTime='" + periodEndTime + '\'' +
                '}';
    }
}