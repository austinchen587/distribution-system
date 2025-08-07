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
 * Saga步骤完成事件
 * 
 * <p>当Saga事务的某个步骤成功完成时发布此事件。该事件用于：
 * <ul>
 *   <li>记录步骤的执行结果</li>
 *   <li>更新Saga事务状态</li>
 *   <li>触发下一个步骤的执行</li>
 *   <li>检查事务是否完成</li>
 * </ul>
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Data
@SuperBuilder

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("SagaStepCompletedEvent")
public class SagaStepCompletedEvent extends DomainEvent {

    /**
     * Saga事务ID
     */
    @NotBlank(message = "Saga事务ID不能为空")
    @JsonProperty("sagaId")
    private String sagaId;

    /**
     * 步骤名称
     */
    @NotBlank(message = "步骤名称不能为空")
    @JsonProperty("stepName")
    private String stepName;

    /**
     * 步骤序号
     */
    @NotNull(message = "步骤序号不能为空")
    @JsonProperty("stepOrder")
    private Integer stepOrder;

    /**
     * 执行服务名称
     */
    @NotBlank(message = "执行服务名称不能为空")
    @JsonProperty("executorService")
    private String executorService;

    /**
     * 步骤执行结果
     */
    @JsonProperty("stepResult")
    private Map<String, Object> stepResult;

    /**
     * 执行时长（毫秒）
     */
    @JsonProperty("executionDuration")
    private Long executionDuration;

    /**
     * 创建Saga步骤完成事件的静态构建方法
     * 
     * @param sagaId Saga事务ID
     * @param stepName 步骤名称
     * @param stepOrder 步骤序号
     * @param executorService 执行服务名称
     * @param correlationId 关联ID
     * @return Saga步骤完成事件
     */
    public static SagaStepCompletedEvent create(String sagaId, String stepName, Integer stepOrder,
                                              String executorService, String correlationId) {
        return SagaStepCompletedEvent.builder()
                .sagaId(sagaId)
                .stepName(stepName)
                .stepOrder(stepOrder)
                .executorService(executorService)
                .correlationId(correlationId)
                .eventType(EventType.SAGA_STEP_COMPLETED)
                .build();
    }

    /**
     * 创建带执行结果的Saga步骤完成事件
     * 
     * @param sagaId Saga事务ID
     * @param stepName 步骤名称
     * @param stepOrder 步骤序号
     * @param executorService 执行服务名称
     * @param stepResult 步骤执行结果
     * @param executionDuration 执行时长
     * @param correlationId 关联ID
     * @return Saga步骤完成事件
     */
    public static SagaStepCompletedEvent createWithResult(String sagaId, String stepName, Integer stepOrder,
                                                         String executorService, Map<String, Object> stepResult,
                                                         Long executionDuration, String correlationId) {
        return SagaStepCompletedEvent.builder()
                .sagaId(sagaId)
                .stepName(stepName)
                .stepOrder(stepOrder)
                .executorService(executorService)
                .stepResult(stepResult)
                .executionDuration(executionDuration)
                .correlationId(correlationId)
                .eventType(EventType.SAGA_STEP_COMPLETED)
                .build();
    }

    @Override
    public boolean isValid() {
        return super.isValid() 
                && sagaId != null && !sagaId.trim().isEmpty()
                && stepName != null && !stepName.trim().isEmpty()
                && stepOrder != null && stepOrder >= 0
                && executorService != null && !executorService.trim().isEmpty();
    }
}