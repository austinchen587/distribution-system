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

/**
 * 推广任务审核通过事件
 * 
 * <p>当推广任务审核通过时发布此事件。该事件触发后续的业务流程，包括：
 * <ul>
 *   <li>计算推广奖励</li>
 *   <li>更新用户推广统计</li>
 *   <li>触发佣金计算</li>
 *   <li>发送奖励通知</li>
 * </ul>
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Data
@SuperBuilder

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("PromotionApprovedEvent")
public class PromotionApprovedEvent extends DomainEvent {

    /**
     * 推广任务ID
     */
    @NotNull(message = "推广任务ID不能为空")
    @JsonProperty("promotionId")
    private Long promotionId;

    /**
     * 推广人ID
     */
    @NotNull(message = "推广人ID不能为空")
    @JsonProperty("promoterId")
    private Long promoterId;

    /**
     * 推广人姓名
     */
    @NotBlank(message = "推广人姓名不能为空")
    @JsonProperty("promoterName")
    private String promoterName;

    /**
     * 推广平台
     */
    @NotBlank(message = "推广平台不能为空")
    @JsonProperty("platform")
    private String platform;

    /**
     * 推广链接
     */
    @NotBlank(message = "推广链接不能为空")
    @JsonProperty("promotionUrl")
    private String promotionUrl;

    /**
     * 审核人ID
     */
    @NotNull(message = "审核人ID不能为空")
    @JsonProperty("reviewerId")
    private Long reviewerId;

    /**
     * 审核人姓名
     */
    @NotBlank(message = "审核人姓名不能为空")
    @JsonProperty("reviewerName")
    private String reviewerName;

    /**
     * 奖励金额
     */
    @JsonProperty("rewardAmount")
    private BigDecimal rewardAmount;

    /**
     * 审核备注
     */
    @JsonProperty("reviewNotes")
    private String reviewNotes;

    /**
     * 创建推广审核通过事件的静态构建方法
     * 
     * @param promotionId 推广任务ID
     * @param promoterId 推广人ID
     * @param promoterName 推广人姓名
     * @param platform 推广平台
     * @param promotionUrl 推广链接
     * @param reviewerId 审核人ID
     * @param reviewerName 审核人姓名
     * @param correlationId 关联ID
     * @return 推广审核通过事件
     */
    public static PromotionApprovedEvent create(Long promotionId, Long promoterId, String promoterName,
                                              String platform, String promotionUrl, Long reviewerId,
                                              String reviewerName, String correlationId) {
        return PromotionApprovedEvent.builder()
                .promotionId(promotionId)
                .promoterId(promoterId)
                .promoterName(promoterName)
                .platform(platform)
                .promotionUrl(promotionUrl)
                .reviewerId(reviewerId)
                .reviewerName(reviewerName)
                .correlationId(correlationId)
                .eventType(EventType.PROMOTION_APPROVED)
                .build();
    }

    /**
     * 创建带奖励信息的推广审核通过事件
     * 
     * @param promotionId 推广任务ID
     * @param promoterId 推广人ID
     * @param promoterName 推广人姓名
     * @param platform 推广平台
     * @param promotionUrl 推广链接
     * @param reviewerId 审核人ID
     * @param reviewerName 审核人姓名
     * @param rewardAmount 奖励金额
     * @param reviewNotes 审核备注
     * @param correlationId 关联ID
     * @return 推广审核通过事件
     */
    public static PromotionApprovedEvent createWithReward(Long promotionId, Long promoterId, String promoterName,
                                                         String platform, String promotionUrl, Long reviewerId,
                                                         String reviewerName, BigDecimal rewardAmount,
                                                         String reviewNotes, String correlationId) {
        return PromotionApprovedEvent.builder()
                .promotionId(promotionId)
                .promoterId(promoterId)
                .promoterName(promoterName)
                .platform(platform)
                .promotionUrl(promotionUrl)
                .reviewerId(reviewerId)
                .reviewerName(reviewerName)
                .rewardAmount(rewardAmount)
                .reviewNotes(reviewNotes)
                .correlationId(correlationId)
                .eventType(EventType.PROMOTION_APPROVED)
                .build();
    }

    @Override
    public boolean isValid() {
        return super.isValid() 
                && promotionId != null
                && promoterId != null
                && promoterName != null && !promoterName.trim().isEmpty()
                && platform != null && !platform.trim().isEmpty()
                && promotionUrl != null && !promotionUrl.trim().isEmpty()
                && reviewerId != null
                && reviewerName != null && !reviewerName.trim().isEmpty();
    }
}