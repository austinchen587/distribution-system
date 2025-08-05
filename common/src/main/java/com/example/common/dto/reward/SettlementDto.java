package com.example.common.dto.reward;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 结算信息DTO - 用于奖励结算和财务处理
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
@Schema(description = "结算信息")
public class SettlementDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 结算批次ID
     */
    @Schema(description = "结算批次ID", example = "1")
    @NotNull
    private Long id;
    
    /**
     * 代理用户ID
     */
    @Schema(description = "代理用户ID", example = "10")
    @NotNull
    private Long agentId;
    
    /**
     * 代理用户名
     */
    @Schema(description = "代理用户名", example = "agent001")
    private String agentUsername;
    
    /**
     * 结算周期开始时间
     */
    @Schema(description = "结算周期开始时间", example = "2025-08-01 00:00:00")
    private String periodStartTime;
    
    /**
     * 结算周期结束时间
     */
    @Schema(description = "结算周期结束时间", example = "2025-08-31 23:59:59")
    private String periodEndTime;
    
    /**
     * 基础底薪
     */
    @Schema(description = "基础底薪", example = "5000.00")
    private BigDecimal baseSalary;
    
    /**
     * 佣金总额
     */
    @Schema(description = "佣金总额", example = "3500.00")
    private BigDecimal totalCommission;
    
    /**
     * 推广奖励总额
     */
    @Schema(description = "推广奖励总额", example = "800.00")
    private BigDecimal totalPromotionReward;
    
    /**
     * 邀请奖励总额
     */
    @Schema(description = "邀请奖励总额", example = "200.00")
    private BigDecimal totalReferralBonus;
    
    /**
     * 绩效奖金
     */
    @Schema(description = "绩效奖金", example = "1000.00")
    private BigDecimal performanceBonus;
    
    /**
     * 扣款总额
     */
    @Schema(description = "扣款总额", example = "50.00")
    private BigDecimal totalDeduction;
    
    /**
     * 扣款原因
     */
    @Schema(description = "扣款原因", example = "客资质量问题扣款")
    private String deductionReason;
    
    /**
     * 应结算总额
     */
    @Schema(description = "应结算总额", example = "10450.00")
    private BigDecimal totalSettlementAmount;
    
    /**
     * 实际支付金额
     */
    @Schema(description = "实际支付金额", example = "10450.00")
    private BigDecimal actualPaymentAmount;
    
    /**
     * 结算状态
     */
    @Schema(description = "结算状态", example = "COMPLETED", 
            allowableValues = {"PENDING", "PROCESSING", "COMPLETED", "FAILED", "CANCELLED"})
    private String status;
    
    /**
     * 支付方式
     */
    @Schema(description = "支付方式", example = "BANK_TRANSFER", 
            allowableValues = {"BANK_TRANSFER", "ALIPAY", "WECHAT_PAY", "INTERNAL_ACCOUNT"})
    private String paymentMethod;
    
    /**
     * 支付账户信息
     */
    @Schema(description = "支付账户信息", example = "中国银行****1234")
    private String paymentAccount;
    
    /**
     * 交易流水号
     */
    @Schema(description = "交易流水号", example = "TXN202508040001")
    private String transactionId;
    
    /**
     * 包含的奖励记录数量
     */
    @Schema(description = "包含的奖励记录数量", example = "15")
    private Integer rewardCount;
    
    /**
     * 奖励记录列表（可选，用于详情展示）
     */
    @Schema(description = "奖励记录列表")
    private List<RewardDto> rewards;
    
    /**
     * 结算创建时间
     */
    @Schema(description = "结算创建时间", example = "2025-09-01 10:00:00")
    private String createdAt;
    
    /**
     * 结算处理时间
     */
    @Schema(description = "结算处理时间", example = "2025-09-01 15:30:00")
    private String processedAt;
    
    /**
     * 结算完成时间
     */
    @Schema(description = "结算完成时间", example = "2025-09-01 16:00:00")
    private String completedAt;
    
    /**
     * 备注信息
     */
    @Schema(description = "备注信息", example = "8月份业绩结算")
    private String remarks;
    
    // Constructors
    public SettlementDto() {}
    
    public SettlementDto(Long id, Long agentId, String periodStartTime, String periodEndTime, String status) {
        this.id = id;
        this.agentId = agentId;
        this.periodStartTime = periodStartTime;
        this.periodEndTime = periodEndTime;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getAgentId() {
        return agentId;
    }
    
    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }
    
    public String getAgentUsername() {
        return agentUsername;
    }
    
    public void setAgentUsername(String agentUsername) {
        this.agentUsername = agentUsername;
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
    
    public BigDecimal getBaseSalary() {
        return baseSalary;
    }
    
    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }
    
    public BigDecimal getTotalCommission() {
        return totalCommission;
    }
    
    public void setTotalCommission(BigDecimal totalCommission) {
        this.totalCommission = totalCommission;
    }
    
    public BigDecimal getTotalPromotionReward() {
        return totalPromotionReward;
    }
    
    public void setTotalPromotionReward(BigDecimal totalPromotionReward) {
        this.totalPromotionReward = totalPromotionReward;
    }
    
    public BigDecimal getTotalReferralBonus() {
        return totalReferralBonus;
    }
    
    public void setTotalReferralBonus(BigDecimal totalReferralBonus) {
        this.totalReferralBonus = totalReferralBonus;
    }
    
    public BigDecimal getPerformanceBonus() {
        return performanceBonus;
    }
    
    public void setPerformanceBonus(BigDecimal performanceBonus) {
        this.performanceBonus = performanceBonus;
    }
    
    public BigDecimal getTotalDeduction() {
        return totalDeduction;
    }
    
    public void setTotalDeduction(BigDecimal totalDeduction) {
        this.totalDeduction = totalDeduction;
    }
    
    public String getDeductionReason() {
        return deductionReason;
    }
    
    public void setDeductionReason(String deductionReason) {
        this.deductionReason = deductionReason;
    }
    
    public BigDecimal getTotalSettlementAmount() {
        return totalSettlementAmount;
    }
    
    public void setTotalSettlementAmount(BigDecimal totalSettlementAmount) {
        this.totalSettlementAmount = totalSettlementAmount;
    }
    
    public BigDecimal getActualPaymentAmount() {
        return actualPaymentAmount;
    }
    
    public void setActualPaymentAmount(BigDecimal actualPaymentAmount) {
        this.actualPaymentAmount = actualPaymentAmount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getPaymentAccount() {
        return paymentAccount;
    }
    
    public void setPaymentAccount(String paymentAccount) {
        this.paymentAccount = paymentAccount;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public Integer getRewardCount() {
        return rewardCount;
    }
    
    public void setRewardCount(Integer rewardCount) {
        this.rewardCount = rewardCount;
    }
    
    public List<RewardDto> getRewards() {
        return rewards;
    }
    
    public void setRewards(List<RewardDto> rewards) {
        this.rewards = rewards;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getProcessedAt() {
        return processedAt;
    }
    
    public void setProcessedAt(String processedAt) {
        this.processedAt = processedAt;
    }
    
    public String getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
     * 检查是否处理中
     * 
     * @return 是否处理中
     */
    public boolean isProcessing() {
        return "PROCESSING".equals(status);
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
     * 检查是否失败
     * 
     * @return 是否失败
     */
    public boolean isFailed() {
        return "FAILED".equals(status);
    }
    
    /**
     * 检查支付金额是否与应结算金额一致
     * 
     * @return 是否一致
     */
    public boolean isPaymentAmountMatching() {
        if (totalSettlementAmount == null || actualPaymentAmount == null) {
            return false;
        }
        return totalSettlementAmount.compareTo(actualPaymentAmount) == 0;
    }
    
    /**
     * 计算总收入（不含扣款）
     * 
     * @return 总收入
     */
    public BigDecimal getTotalIncome() {
        BigDecimal income = BigDecimal.ZERO;
        if (baseSalary != null) income = income.add(baseSalary);
        if (totalCommission != null) income = income.add(totalCommission);
        if (totalPromotionReward != null) income = income.add(totalPromotionReward);
        if (totalReferralBonus != null) income = income.add(totalReferralBonus);
        if (performanceBonus != null) income = income.add(performanceBonus);
        return income;
    }
    
    @Override
    public String toString() {
        return "SettlementDto{" +
                "id=" + id +
                ", agentId=" + agentId +
                ", agentUsername='" + agentUsername + '\'' +
                ", periodStartTime='" + periodStartTime + '\'' +
                ", periodEndTime='" + periodEndTime + '\'' +
                ", totalSettlementAmount=" + totalSettlementAmount +
                ", actualPaymentAmount=" + actualPaymentAmount +
                ", status='" + status + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", rewardCount=" + rewardCount +
                ", completedAt='" + completedAt + '\'' +
                '}';
    }
}