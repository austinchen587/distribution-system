package com.example.reward.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 奖励信息DTO - 用于佣金和奖励展示
 * 
 * @author DTO Generator
 * @version 1.0
 * @since 2025-08-04
 */
@Schema(description = "奖励信息")
public class RewardDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 奖励记录ID
     */
    @Schema(description = "奖励记录ID", example = "1")
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
     * 代理真实姓名
     */
    @Schema(description = "代理真实姓名", example = "张三")
    private String agentRealName;
    
    /**
     * 奖励类型
     */
    @Schema(description = "奖励类型", example = "COMMISSION", 
            allowableValues = {"COMMISSION", "PROMOTION_REWARD", "REFERRAL_BONUS", "PERFORMANCE_BONUS"})
    private String type;
    
    /**
     * 奖励金额
     */
    @Schema(description = "奖励金额", example = "500.00")
    private BigDecimal amount;
    
    /**
     * 关联订单ID（佣金类型时有值）
     */
    @Schema(description = "关联订单ID", example = "100")
    private Long relatedOrderId;
    
    /**
     * 关联推广任务ID（推广奖励时有值）
     */
    @Schema(description = "关联推广任务ID", example = "50")
    private Long relatedPromotionId;
    
    /**
     * 关联邀请记录ID（邀请奖励时有值）
     */
    @Schema(description = "关联邀请记录ID", example = "25")
    private Long relatedInvitationId;
    
    /**
     * 奖励状态
     */
    @Schema(description = "奖励状态", example = "CONFIRMED", 
            allowableValues = {"PENDING", "CONFIRMED", "SETTLED", "CANCELLED"})
    private String status;
    
    /**
     * 奖励描述
     */
    @Schema(description = "奖励描述", example = "订单佣金奖励")
    private String description;
    
    /**
     * 计算基数（用于佣金计算）
     */
    @Schema(description = "计算基数", example = "10000.00")
    private BigDecimal calculationBase;
    
    /**
     * 奖励比例
     */
    @Schema(description = "奖励比例", example = "0.05")
    private BigDecimal rewardRate;
    
    /**
     * 奖励生成时间
     */
    @Schema(description = "奖励生成时间", example = "2025-08-04 10:00:00")
    private LocalDateTime createdAt;
    
    /**
     * 奖励确认时间
     */
    @Schema(description = "奖励确认时间", example = "2025-08-04 12:00:00")
    private LocalDateTime confirmedAt;
    
    /**
     * 结算时间
     */
    @Schema(description = "结算时间", example = "2025-08-05 15:00:00")
    private LocalDateTime settledAt;
    
    /**
     * 奖励来源
     */
    @Schema(description = "奖励来源", example = "ORDER")
    private String source;
    
    /**
     * 来源ID
     */
    @Schema(description = "来源ID", example = "1001")
    private Long sourceId;
    
    /**
     * 计算规则
     */
    @Schema(description = "计算规则", example = "按订单金额5%计算")
    private String calculationRule;
    
    /**
     * 结算ID
     */
    @Schema(description = "结算ID", example = "2001")
    private Long settlementId;
    
    /**
     * 支付时间
     */
    @Schema(description = "支付时间", example = "2025-08-05 16:00:00")
    private LocalDateTime paidAt;
    
    // Constructors
    public RewardDto() {}
    
    public RewardDto(Long id, Long agentId, String type, BigDecimal amount, String status) {
        this.id = id;
        this.agentId = agentId;
        this.type = type;
        this.amount = amount;
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
    
    public String getAgentRealName() {
        return agentRealName;
    }
    
    public void setAgentRealName(String agentRealName) {
        this.agentRealName = agentRealName;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public Long getRelatedOrderId() {
        return relatedOrderId;
    }
    
    public void setRelatedOrderId(Long relatedOrderId) {
        this.relatedOrderId = relatedOrderId;
    }
    
    public Long getRelatedPromotionId() {
        return relatedPromotionId;
    }
    
    public void setRelatedPromotionId(Long relatedPromotionId) {
        this.relatedPromotionId = relatedPromotionId;
    }
    
    public Long getRelatedInvitationId() {
        return relatedInvitationId;
    }
    
    public void setRelatedInvitationId(Long relatedInvitationId) {
        this.relatedInvitationId = relatedInvitationId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getCalculationBase() {
        return calculationBase;
    }
    
    public void setCalculationBase(BigDecimal calculationBase) {
        this.calculationBase = calculationBase;
    }
    
    public BigDecimal getRewardRate() {
        return rewardRate;
    }
    
    public void setRewardRate(BigDecimal rewardRate) {
        this.rewardRate = rewardRate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }
    
    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }
    
    public LocalDateTime getSettledAt() {
        return settledAt;
    }
    
    public void setSettledAt(LocalDateTime settledAt) {
        this.settledAt = settledAt;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public Long getSourceId() {
        return sourceId;
    }
    
    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }
    
    public String getCalculationRule() {
        return calculationRule;
    }
    
    public void setCalculationRule(String calculationRule) {
        this.calculationRule = calculationRule;
    }
    
    public Long getSettlementId() {
        return settlementId;
    }
    
    public void setSettlementId(Long settlementId) {
        this.settlementId = settlementId;
    }
    
    public LocalDateTime getPaidAt() {
        return paidAt;
    }
    
    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
    
    /**
     * 检查是否为佣金类型
     * 
     * @return 是否为佣金类型
     */
    public boolean isCommission() {
        return "COMMISSION".equals(type);
    }
    
    /**
     * 检查是否为推广奖励
     * 
     * @return 是否为推广奖励
     */
    public boolean isPromotionReward() {
        return "PROMOTION_REWARD".equals(type);
    }
    
    /**
     * 检查是否已确认
     * 
     * @return 是否已确认
     */
    public boolean isConfirmed() {
        return "CONFIRMED".equals(status) || "SETTLED".equals(status);
    }
    
    /**
     * 检查是否已结算
     * 
     * @return 是否已结算
     */
    public boolean isSettled() {
        return "SETTLED".equals(status);
    }
    
    /**
     * 检查是否待处理
     * 
     * @return 是否待处理
     */
    public boolean isPending() {
        return "PENDING".equals(status);
    }
    
    @Override
    public String toString() {
        return "RewardDto{" +
                "id=" + id +
                ", agentId=" + agentId +
                ", agentUsername='" + agentUsername + '\'' +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", settledAt='" + settledAt + '\'' +
                '}';
    }
}