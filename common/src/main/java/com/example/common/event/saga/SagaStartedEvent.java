package com.example.common.event.saga;

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
import java.util.Map;

/**
 * Saga事务开始事件
 * 
 * <p>当一个分布式事务流程开始时发布此事件。该事件用于：
 * <ul>
 *   <li>初始化Saga事务实例</li>
 *   <li>记录事务的初始状态</li>
 *   <li>启动第一个事务步骤</li>
 *   <li>建立事务追踪机制</li>
 * </ul>
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Data
@SuperBuilder

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("SagaStartedEvent")
public class SagaStartedEvent extends DomainEvent {

    /**
     * Saga事务ID
     */
    @NotBlank(message = "Saga事务ID不能为空")
    @JsonProperty("sagaId")
    private String sagaId;

    /**
     * Saga事务类型
     */
    @NotBlank(message = "Saga事务类型不能为空")
    @JsonProperty("sagaType")
    private String sagaType;

    /**
     * 发起人ID
     */
    @NotNull(message = "发起人ID不能为空")
    @JsonProperty("initiatorId")
    private Long initiatorId;

    /**
     * 业务上下文数据
     */
    @JsonProperty("businessContext")
    private Map<String, Object> businessContext;

    /**
     * Saga事务描述
     */
    @JsonProperty("description")
    private String description;

    /**
     * 创建Saga开始事件的静态构建方法
     * 
     * @param sagaId Saga事务ID
     * @param sagaType Saga事务类型
     * @param initiatorId 发起人ID
     * @param correlationId 关联ID
     * @return Saga开始事件
     */
    public static SagaStartedEvent create(String sagaId, String sagaType, Long initiatorId, String correlationId) {
        return SagaStartedEvent.builder()
                .sagaId(sagaId)
                .sagaType(sagaType)
                .initiatorId(initiatorId)
                .correlationId(correlationId)
                .eventType(EventType.SAGA_STARTED)
                .build();
    }

    /**
     * 创建带业务上下文的Saga开始事件
     * 
     * @param sagaId Saga事务ID
     * @param sagaType Saga事务类型
     * @param initiatorId 发起人ID
     * @param businessContext 业务上下文
     * @param description 描述
     * @param correlationId 关联ID
     * @return Saga开始事件
     */
    public static SagaStartedEvent createWithContext(String sagaId, String sagaType, Long initiatorId,
                                                    Map<String, Object> businessContext, String description,
                                                    String correlationId) {
        return SagaStartedEvent.builder()
                .sagaId(sagaId)
                .sagaType(sagaType)
                .initiatorId(initiatorId)
                .businessContext(businessContext)
                .description(description)
                .correlationId(correlationId)
                .eventType(EventType.SAGA_STARTED)
                .build();
    }

    @Override
    public boolean isValid() {
        return super.isValid() 
                && sagaId != null && !sagaId.trim().isEmpty()
                && sagaType != null && !sagaType.trim().isEmpty()
                && initiatorId != null;
    }
}