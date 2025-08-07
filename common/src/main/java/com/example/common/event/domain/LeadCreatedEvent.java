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

/**
 * 客资创建事件
 * 
 * <p>当新客资成功提交时发布此事件。该事件触发后续的业务流程，包括：
 * <ul>
 *   <li>执行重复检查逻辑</li>
 *   <li>分配给相应的销售人员</li>
 *   <li>启动客资审核流程</li>
 *   <li>更新销售人员的客资统计</li>
 * </ul>
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("LeadCreatedEvent")
public class LeadCreatedEvent extends DomainEvent {

    /**
     * 客资ID
     */
    @NotNull(message = "客资ID不能为空")
    @JsonProperty("leadId")
    private Long leadId;

    /**
     * 客户姓名
     */
    @NotBlank(message = "客户姓名不能为空")
    @JsonProperty("customerName")
    private String customerName;

    /**
     * 客户手机号
     */
    @NotBlank(message = "客户手机号不能为空")
    @JsonProperty("customerPhone")
    private String customerPhone;

    /**
     * 客户微信号
     */
    @JsonProperty("customerWechat")
    private String customerWechat;

    /**
     * 提交人ID
     */
    @NotNull(message = "提交人ID不能为空")
    @JsonProperty("submitterId")
    private Long submitterId;

    /**
     * 提交人姓名
     */
    @NotBlank(message = "提交人姓名不能为空")
    @JsonProperty("submitterName")
    private String submitterName;

    /**
     * 客资来源
     */
    @JsonProperty("source")
    private String source;

    /**
     * 备注信息
     */
    @JsonProperty("remarks")
    private String remarks;

    /**
     * 创建客资事件的静态构建方法
     * 
     * @param leadId 客资ID
     * @param customerName 客户姓名
     * @param customerPhone 客户手机号
     * @param submitterId 提交人ID
     * @param submitterName 提交人姓名
     * @param correlationId 关联ID
     * @return 客资创建事件
     */
    public static LeadCreatedEvent create(Long leadId, String customerName, String customerPhone,
                                         Long submitterId, String submitterName, String correlationId) {
        return LeadCreatedEvent.builder()
                .leadId(leadId)
                .customerName(customerName)
                .customerPhone(customerPhone)
                .submitterId(submitterId)
                .submitterName(submitterName)
                .correlationId(correlationId)
                .eventType(EventType.LEAD_CREATED)
                .build();
    }

    /**
     * 创建带完整信息的客资事件
     * 
     * @param leadId 客资ID
     * @param customerName 客户姓名
     * @param customerPhone 客户手机号
     * @param customerWechat 客户微信号
     * @param submitterId 提交人ID
     * @param submitterName 提交人姓名
     * @param source 客资来源
     * @param remarks 备注信息
     * @param correlationId 关联ID
     * @return 客资创建事件
     */
    public static LeadCreatedEvent createWithDetails(Long leadId, String customerName, String customerPhone,
                                                    String customerWechat, Long submitterId, String submitterName,
                                                    String source, String remarks, String correlationId) {
        return LeadCreatedEvent.builder()
                .leadId(leadId)
                .customerName(customerName)
                .customerPhone(customerPhone)
                .customerWechat(customerWechat)
                .submitterId(submitterId)
                .submitterName(submitterName)
                .source(source)
                .remarks(remarks)
                .correlationId(correlationId)
                .eventType(EventType.LEAD_CREATED)
                .build();
    }

    @Override
    public boolean isValid() {
        return super.isValid() 
                && leadId != null
                && customerName != null && !customerName.trim().isEmpty()
                && customerPhone != null && !customerPhone.trim().isEmpty()
                && submitterId != null
                && submitterName != null && !submitterName.trim().isEmpty();
    }
}