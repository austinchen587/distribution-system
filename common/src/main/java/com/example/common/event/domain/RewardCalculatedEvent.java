package com.example.common.event.domain;

import com.example.common.event.DomainEvent;
import com.example.common.event.EventType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 奖励计算事件
 * 
 * <p>当系统计算出用户的奖励金额时发布此事件。该事件触发后续的业务流程，包括：
 * <ul>
 *   <li>更新用户的奖励余额</li>
 *   <li>记录奖励明细</li>
 *   <li>发送奖励通知</li>
 *   <li>触发上级奖励分成</li>
 * </ul>
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Data
@SuperBuilder

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("RewardCalculatedEvent")
public class RewardCalculatedEvent extends DomainEvent {

    /**
     * 奖励计算ID
     */
    @NotNull(message = "奖励计算ID不能为空")
    @JsonProperty("calculationId")
    private Long calculationId;

    /**
     * 受益人ID
     */
    @NotNull(message = "受益人ID不能为空")
    @JsonProperty("beneficiaryId")
    private Long beneficiaryId;

    /**
     * 受益人姓名
     */
    @NotBlank(message = "受益人姓名不能为空")
    @JsonProperty("beneficiaryName")
    private String beneficiaryName;

    /**
     * 奖励类型（直接奖励、推荐奖励、团队奖励等）
     */
    @NotBlank(message = "奖励类型不能为空")
    @JsonProperty("rewardType")
    private String rewardType;

    /**
     * 奖励金额
     */
    @NotNull(message = "奖励金额不能为空")
    @JsonProperty("rewardAmount")
    private BigDecimal rewardAmount;

    /**
     * 来源事件ID（如推广任务ID、客资转换ID等）
     */
    @NotNull(message = "来源事件ID不能为空")
    @JsonProperty("sourceEventId")
    private Long sourceEventId;

    /**
     * 来源事件类型
     */
    @NotBlank(message = "来源事件类型不能为空")
    @JsonProperty("sourceEventType")
    private String sourceEventType;

    /**
     * 奖励明细列表
     */
    @JsonProperty("rewardDetails")
    private List<RewardDetail> rewardDetails;

    /**
     * 计算备注
     */
    @JsonProperty("calculationNotes")
    private String calculationNotes;

    /**
     * 奖励明细内部类
     */
    @Data
@SuperBuilder
    
    @NoArgsConstructor
    public static class RewardDetail {
        /**
         * 明细类型（基础奖励、等级加成、推荐分成等）
         */
        private String detailType;

        /**
         * 明细金额
         */
        private BigDecimal amount;

        /**
         * 计算公式或说明
         */
        private String formula;
    }

    /**
     * 创建奖励计算事件的静态构建方法
     * 
     * @param calculationId 奖励计算ID
     * @param beneficiaryId 受益人ID
     * @param beneficiaryName 受益人姓名
     * @param rewardType 奖励类型
     * @param rewardAmount 奖励金额
     * @param sourceEventId 来源事件ID
     * @param sourceEventType 来源事件类型
     * @param correlationId 关联ID
     * @return 奖励计算事件
     */
    public static RewardCalculatedEvent create(Long calculationId, Long beneficiaryId, String beneficiaryName,
                                             String rewardType, BigDecimal rewardAmount, Long sourceEventId,
                                             String sourceEventType, String correlationId) {
        return RewardCalculatedEvent.builder()
                .calculationId(calculationId)
                .beneficiaryId(beneficiaryId)
                .beneficiaryName(beneficiaryName)
                .rewardType(rewardType)
                .rewardAmount(rewardAmount)
                .sourceEventId(sourceEventId)
                .sourceEventType(sourceEventType)
                .correlationId(correlationId)
                .eventType(EventType.REWARD_CALCULATED)
                .build();
    }

    /**
     * 创建带明细的奖励计算事件
     * 
     * @param calculationId 奖励计算ID
     * @param beneficiaryId 受益人ID
     * @param beneficiaryName 受益人姓名
     * @param rewardType 奖励类型
     * @param rewardAmount 奖励金额
     * @param sourceEventId 来源事件ID
     * @param sourceEventType 来源事件类型
     * @param rewardDetails 奖励明细
     * @param calculationNotes 计算备注
     * @param correlationId 关联ID
     * @return 奖励计算事件
     */
    public static RewardCalculatedEvent createWithDetails(Long calculationId, Long beneficiaryId, String beneficiaryName,
                                                         String rewardType, BigDecimal rewardAmount, Long sourceEventId,
                                                         String sourceEventType, List<RewardDetail> rewardDetails,
                                                         String calculationNotes, String correlationId) {
        return RewardCalculatedEvent.builder()
                .calculationId(calculationId)
                .beneficiaryId(beneficiaryId)
                .beneficiaryName(beneficiaryName)
                .rewardType(rewardType)
                .rewardAmount(rewardAmount)
                .sourceEventId(sourceEventId)
                .sourceEventType(sourceEventType)
                .rewardDetails(rewardDetails)
                .calculationNotes(calculationNotes)
                .correlationId(correlationId)
                .eventType(EventType.REWARD_CALCULATED)
                .build();
    }

    @Override
    public boolean isValid() {
        return super.isValid() 
                && calculationId != null
                && beneficiaryId != null
                && beneficiaryName != null && !beneficiaryName.trim().isEmpty()
                && rewardType != null && !rewardType.trim().isEmpty()
                && rewardAmount != null && rewardAmount.compareTo(BigDecimal.ZERO) >= 0
                && sourceEventId != null
                && sourceEventType != null && !sourceEventType.trim().isEmpty();
    }
}