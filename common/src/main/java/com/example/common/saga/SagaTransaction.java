package com.example.common.saga;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Saga事务模型
 * 
 * <p>表示一个分布式事务的完整生命周期，包含多个执行步骤和补偿步骤。
 * Saga事务遵循最终一致性模式，通过补偿机制处理分布式事务的回滚。
 * 
 * <p>核心特性：
 * <ul>
 *   <li>支持多步骤的分布式事务协调</li>
 *   <li>自动补偿机制处理事务失败</li>
 *   <li>完整的状态追踪和监控</li>
 *   <li>支持并行和串行步骤执行</li>
 * </ul>
 * 
 * <p>状态流转：
 * CREATED → RUNNING → COMPLETED | COMPENSATING → COMPENSATED | FAILED
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class SagaTransaction {

    /**
     * Saga事务唯一标识
     */
    @NotBlank(message = "Saga事务ID不能为空")
    private String sagaId;

    /**
     * Saga事务类型
     */
    @NotBlank(message = "Saga事务类型不能为空")
    private String sagaType;

    /**
     * 关联ID，用于业务流程追踪
     */
    @NotBlank(message = "关联ID不能为空")
    private String correlationId;

    /**
     * 事务发起人ID
     */
    @NotNull(message = "发起人ID不能为空")
    private Long initiatorId;

    /**
     * 事务当前状态
     */
    @NotNull(message = "事务状态不能为空")
    private SagaStatus status = SagaStatus.CREATED;

    /**
     * 业务上下文数据
     */
    private Map<String, Object> businessContext = new HashMap<>();

    /**
     * 事务步骤列表
     */
    private List<SagaStep> steps = new ArrayList<>();

    /**
     * 当前执行步骤索引
     */
    private int currentStepIndex = 0;

    /**
     * 事务创建时间
     */
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * 事务开始时间
     */
    private LocalDateTime startedAt;

    /**
     * 事务完成时间
     */
    private LocalDateTime completedAt;

    /**
     * 事务失败时间
     */
    private LocalDateTime failedAt;

    /**
     * 失败原因
     */
    private String failureReason;

    /**
     * 重试次数
     */
    private int retryCount = 0;

    /**
     * 最大重试次数
     */
    private int maxRetries = 3;

    /**
     * 事务超时时间（毫秒）
     */
    private long timeoutMillis = 300000L; // 5分钟默认超时

    /**
     * 是否启用补偿
     */
    private boolean compensationEnabled = true;

    /**
     * Saga事务状态枚举
     */
    public enum SagaStatus {
        CREATED("已创建"),
        RUNNING("运行中"),
        COMPLETED("已完成"),
        COMPENSATING("补偿中"),
        COMPENSATED("已补偿"),
        FAILED("已失败"),
        TIMEOUT("已超时");

        private final String description;

        SagaStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 创建新的Saga事务
     * 
     * @param sagaType 事务类型
     * @param correlationId 关联ID
     * @param initiatorId 发起人ID
     * @return Saga事务实例
     */
    public static SagaTransaction create(String sagaType, String correlationId, Long initiatorId) {
        return SagaTransaction.builder()
                .sagaId(generateSagaId())
                .sagaType(sagaType)
                .correlationId(correlationId)
                .initiatorId(initiatorId)
                .status(SagaStatus.CREATED)
                .businessContext(new HashMap<>())
                .steps(new ArrayList<>())
                .currentStepIndex(0)
                .retryCount(0)
                .maxRetries(3)
                .timeoutMillis(300000L)
                .compensationEnabled(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 生成唯一的Saga事务ID
     * 
     * @return Saga事务ID
     */
    public static String generateSagaId() {
        return "SAGA-" + System.currentTimeMillis() + "-" + 
               java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 添加执行步骤
     * 
     * @param step Saga步骤
     */
    public void addStep(SagaStep step) {
        if (this.steps == null) {
            this.steps = new ArrayList<>();
        }
        step.setStepOrder(this.steps.size());
        this.steps.add(step);
    }

    /**
     * 启动Saga事务
     */
    public void start() {
        this.status = SagaStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
    }

    /**
     * 获取当前执行步骤
     * 
     * @return 当前步骤，如果不存在返回null
     */
    public SagaStep getCurrentStep() {
        if (steps == null || currentStepIndex >= steps.size()) {
            return null;
        }
        return steps.get(currentStepIndex);
    }

    /**
     * 移动到下一步
     * 
     * @return 是否成功移动到下一步
     */
    public boolean moveToNextStep() {
        if (hasNextStep()) {
            currentStepIndex++;
            return true;
        }
        return false;
    }

    /**
     * 检查是否还有下一步
     * 
     * @return 是否有下一步
     */
    public boolean hasNextStep() {
        return steps != null && currentStepIndex + 1 < steps.size();
    }

    /**
     * 完成Saga事务
     */
    public void complete() {
        this.status = SagaStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * 启动补偿流程
     * 
     * @param failureReason 失败原因
     */
    public void startCompensation(String failureReason) {
        this.status = SagaStatus.COMPENSATING;
        this.failureReason = failureReason;
        this.failedAt = LocalDateTime.now();
    }

    /**
     * 完成补偿流程
     */
    public void completeCompensation() {
        this.status = SagaStatus.COMPENSATED;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * 标记事务失败
     * 
     * @param failureReason 失败原因
     */
    public void fail(String failureReason) {
        this.status = SagaStatus.FAILED;
        this.failureReason = failureReason;
        this.failedAt = LocalDateTime.now();
    }

    /**
     * 增加重试次数
     */
    public void incrementRetryCount() {
        this.retryCount++;
    }

    /**
     * 检查是否可以重试
     * 
     * @return 是否可以重试
     */
    public boolean canRetry() {
        return retryCount < maxRetries;
    }

    /**
     * 检查是否超时
     * 
     * @return 是否超时
     */
    public boolean isTimeout() {
        if (startedAt == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(startedAt.plusNanos(timeoutMillis * 1_000_000));
    }

    /**
     * 获取已完成的步骤列表
     * 
     * @return 已完成的步骤列表
     */
    public List<SagaStep> getCompletedSteps() {
        if (steps == null) {
            return new ArrayList<>();
        }
        return steps.subList(0, Math.min(currentStepIndex + 1, steps.size()));
    }

    /**
     * 设置业务上下文数据
     * 
     * @param key 键
     * @param value 值
     */
    public void setContextData(String key, Object value) {
        if (businessContext == null) {
            businessContext = new HashMap<>();
        }
        businessContext.put(key, value);
    }

    /**
     * 获取业务上下文数据
     * 
     * @param key 键
     * @return 值
     */
    public Object getContextData(String key) {
        if (businessContext == null) {
            return null;
        }
        return businessContext.get(key);
    }

    /**
     * 获取业务上下文数据（带类型转换）
     * 
     * @param key 键
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 转换后的值
     */
    @SuppressWarnings("unchecked")
    public <T> T getContextData(String key, Class<T> clazz) {
        Object value = getContextData(key);
        if (value == null) {
            return null;
        }
        if (clazz.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        throw new ClassCastException("Cannot cast " + value.getClass() + " to " + clazz);
    }

    /**
     * 验证Saga事务的完整性
     * 
     * @return 是否有效
     */
    public boolean isValid() {
        return sagaId != null && !sagaId.trim().isEmpty()
                && sagaType != null && !sagaType.trim().isEmpty()
                && correlationId != null && !correlationId.trim().isEmpty()
                && initiatorId != null
                && status != null;
    }
}