package com.example.common.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 领域事件基类
 * 
 * <p>所有业务域事件的抽象基类，提供统一的事件结构和序列化支持。
 * 该基类包含事件追踪、去重、关联识别等核心功能。
 * 
 * <p>主要功能：
 * <ul>
 *   <li>事件唯一标识和去重（eventId）</li>
 *   <li>业务流程关联追踪（correlationId）</li>
 *   <li>事件分类和路由（eventType）</li>
 *   <li>事件时间戳和来源记录</li>
 *   <li>JSON序列化和多态类型支持</li>
 * </ul>
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "eventClass"
)
@JsonSubTypes({
    // 用户相关事件
    @JsonSubTypes.Type(value = com.example.common.event.domain.UserCreatedEvent.class, name = "UserCreatedEvent"),
    
    // 客资相关事件
    @JsonSubTypes.Type(value = com.example.common.event.domain.LeadCreatedEvent.class, name = "LeadCreatedEvent"),
    
    // 推广相关事件
    @JsonSubTypes.Type(value = com.example.common.event.domain.PromotionApprovedEvent.class, name = "PromotionApprovedEvent"),
    
    // 奖励相关事件
    @JsonSubTypes.Type(value = com.example.common.event.domain.RewardCalculatedEvent.class, name = "RewardCalculatedEvent"),
    
    // Saga事务事件
    @JsonSubTypes.Type(value = com.example.common.event.saga.SagaStartedEvent.class, name = "SagaStartedEvent"),
    @JsonSubTypes.Type(value = com.example.common.event.saga.SagaStepCompletedEvent.class, name = "SagaStepCompletedEvent")
})
public abstract class DomainEvent {
    
    /**
     * 事件唯一标识符
     * <p>用于事件去重，确保同一事件不会被重复处理
     */
    @NotBlank(message = "事件ID不能为空")
    @JsonProperty("eventId")
    private String eventId;
    
    /**
     * 关联标识符
     * <p>用于关联同一业务流程中的多个事件，支持分布式事务追踪
     */
    @NotBlank(message = "关联ID不能为空")
    @JsonProperty("correlationId")
    private String correlationId;
    
    /**
     * 事件类型
     * <p>事件分类标识，用于事件路由和处理器匹配
     */
    @NotNull(message = "事件类型不能为空")
    @JsonProperty("eventType")
    private EventType eventType;
    
    /**
     * 事件发生时间戳
     * <p>记录事件的实际发生时间，用于事件排序和过期处理
     */
    @NotNull(message = "时间戳不能为空")
    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    /**
     * 事件来源服务
     * <p>标识发布此事件的微服务名称
     */
    @NotBlank(message = "事件来源不能为空")
    @JsonProperty("source")
    private String source;
    
    /**
     * 事件负载数据
     * <p>具体的业务数据，由子类定义具体结构
     */
    @JsonProperty("payload")
    private Object payload;
    
    /**
     * 事件版本号
     * <p>用于事件结构版本管理和兼容性处理
     */
    @JsonProperty("version")
    private String version = "1.0";
    
    /**
     * 生成新的事件ID
     * <p>使用UUID确保全局唯一性
     * 
     * @return 新的事件ID
     */
    public static String generateEventId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * 生成新的关联ID
     * <p>用于开始新的业务流程追踪
     * 
     * @return 新的关联ID
     */
    public static String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * 设置事件基础信息
     * <p>在事件发布前设置必要的基础字段
     * 
     * @param source 事件来源服务名称
     */
    public void initializeEvent(String source) {
        if (this.eventId == null) {
            this.eventId = generateEventId();
        }
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
        this.source = source;
    }
    
    /**
     * 验证事件数据完整性
     * 
     * @return 是否有效
     */
    public boolean isValid() {
        return eventId != null && !eventId.trim().isEmpty()
            && correlationId != null && !correlationId.trim().isEmpty()
            && eventType != null
            && timestamp != null
            && source != null && !source.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return String.format("%s{eventId='%s', correlationId='%s', eventType=%s, timestamp=%s, source='%s'}",
            getClass().getSimpleName(), eventId, correlationId, eventType, timestamp, source);
    }
}