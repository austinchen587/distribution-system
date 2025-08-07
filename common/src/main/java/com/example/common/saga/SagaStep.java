package com.example.common.saga;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Saga事务步骤
 * 
 * <p>表示Saga事务中的一个执行单元，包含正向操作和补偿操作。
 * 每个步骤都是原子性的，要么成功执行，要么执行补偿操作。
 * 
 * <p>核心特性：
 * <ul>
 *   <li>支持正向操作和补偿操作定义</li>
 *   <li>完整的状态追踪和执行历史</li>
 *   <li>灵活的参数传递和结果记录</li>
 *   <li>自动重试和错误处理</li>
 * </ul>
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class SagaStep {

    /**
     * 步骤名称
     */
    @NotBlank(message = "步骤名称不能为空")
    private String stepName;

    /**
     * 步骤描述
     */
    private String description;

    /**
     * 步骤序号
     */
    @NotNull(message = "步骤序号不能为空")
    private Integer stepOrder;

    /**
     * 执行服务名称
     */
    @NotBlank(message = "执行服务名称不能为空")
    private String serviceName;

    /**
     * 正向操作名称
     */
    @NotBlank(message = "正向操作名称不能为空")
    private String forwardAction;

    /**
     * 补偿操作名称
     */
    private String compensationAction;

    /**
     * 步骤状态
     */
    @NotNull(message = "步骤状态不能为空")
    private StepStatus status = StepStatus.PENDING;

    /**
     * 输入参数
     */
    private Map<String, Object> inputParameters = new HashMap<>();

    /**
     * 输出结果
     */
    private Map<String, Object> outputResult = new HashMap<>();

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 重试次数
     */
    private int retryCount = 0;

    /**
     * 最大重试次数
     */
    private int maxRetries = 3;

    /**
     * 步骤超时时间（毫秒）
     */
    private long timeoutMillis = 30000L; // 30秒默认超时

    /**
     * 是否并行执行
     */
    private boolean parallel = false;

    /**
     * 是否可补偿
     */
    private boolean compensable = true;

    /**
     * 步骤创建时间
     */
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * 步骤开始时间
     */
    private LocalDateTime startedAt;

    /**
     * 步骤完成时间
     */
    private LocalDateTime completedAt;

    /**
     * 补偿开始时间
     */
    private LocalDateTime compensationStartedAt;

    /**
     * 补偿完成时间
     */
    private LocalDateTime compensationCompletedAt;

    /**
     * 执行时长（毫秒）
     */
    private Long executionDuration;

    /**
     * 步骤状态枚举
     */
    public enum StepStatus {
        PENDING("待执行"),
        RUNNING("执行中"),
        COMPLETED("已完成"),
        FAILED("执行失败"),
        COMPENSATING("补偿中"),
        COMPENSATED("已补偿"),
        COMPENSATION_FAILED("补偿失败"),
        SKIPPED("已跳过");

        private final String description;

        StepStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 创建新的Saga步骤
     * 
     * @param stepName 步骤名称
     * @param serviceName 执行服务名称
     * @param forwardAction 正向操作名称
     * @return Saga步骤实例
     */
    public static SagaStep create(String stepName, String serviceName, String forwardAction) {
        return SagaStep.builder()
                .stepName(stepName)
                .serviceName(serviceName)
                .forwardAction(forwardAction)
                .status(StepStatus.PENDING)
                .inputParameters(new HashMap<>())
                .outputResult(new HashMap<>())
                .retryCount(0)
                .maxRetries(3)
                .timeoutMillis(30000L)
                .parallel(false)
                .compensable(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 创建可补偿的Saga步骤
     * 
     * @param stepName 步骤名称
     * @param serviceName 执行服务名称
     * @param forwardAction 正向操作名称
     * @param compensationAction 补偿操作名称
     * @return Saga步骤实例
     */
    public static SagaStep createCompensable(String stepName, String serviceName, String forwardAction, String compensationAction) {
        return SagaStep.builder()
                .stepName(stepName)
                .serviceName(serviceName)
                .forwardAction(forwardAction)
                .compensationAction(compensationAction)
                .status(StepStatus.PENDING)
                .inputParameters(new HashMap<>())
                .outputResult(new HashMap<>())
                .retryCount(0)
                .maxRetries(3)
                .timeoutMillis(30000L)
                .parallel(false)
                .compensable(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 开始执行步骤
     */
    public void start() {
        this.status = StepStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
    }

    /**
     * 完成步骤执行
     * 
     * @param outputResult 执行结果
     */
    public void complete(Map<String, Object> outputResult) {
        this.status = StepStatus.COMPLETED;
        this.outputResult = outputResult != null ? outputResult : new HashMap<>();
        this.completedAt = LocalDateTime.now();
        calculateExecutionDuration();
    }

    /**
     * 标记步骤失败
     * 
     * @param errorMessage 错误信息
     */
    public void fail(String errorMessage) {
        this.status = StepStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = LocalDateTime.now();
        calculateExecutionDuration();
    }

    /**
     * 开始补偿操作
     */
    public void startCompensation() {
        this.status = StepStatus.COMPENSATING;
        this.compensationStartedAt = LocalDateTime.now();
    }

    /**
     * 完成补偿操作
     */
    public void completeCompensation() {
        this.status = StepStatus.COMPENSATED;
        this.compensationCompletedAt = LocalDateTime.now();
    }

    /**
     * 补偿操作失败
     * 
     * @param errorMessage 错误信息
     */
    public void failCompensation(String errorMessage) {
        this.status = StepStatus.COMPENSATION_FAILED;
        this.errorMessage = errorMessage;
        this.compensationCompletedAt = LocalDateTime.now();
    }

    /**
     * 跳过步骤执行
     */
    public void skip() {
        this.status = StepStatus.SKIPPED;
        this.completedAt = LocalDateTime.now();
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
     * 设置输入参数
     * 
     * @param key 参数名
     * @param value 参数值
     */
    public void setInputParameter(String key, Object value) {
        if (inputParameters == null) {
            inputParameters = new HashMap<>();
        }
        inputParameters.put(key, value);
    }

    /**
     * 获取输入参数
     * 
     * @param key 参数名
     * @return 参数值
     */
    public Object getInputParameter(String key) {
        if (inputParameters == null) {
            return null;
        }
        return inputParameters.get(key);
    }

    /**
     * 设置输出结果
     * 
     * @param key 结果键
     * @param value 结果值
     */
    public void setOutputResult(String key, Object value) {
        if (outputResult == null) {
            outputResult = new HashMap<>();
        }
        outputResult.put(key, value);
    }

    /**
     * 获取输出结果
     * 
     * @param key 结果键
     * @return 结果值
     */
    public Object getOutputResult(String key) {
        if (outputResult == null) {
            return null;
        }
        return outputResult.get(key);
    }

    /**
     * 计算执行时长
     */
    private void calculateExecutionDuration() {
        if (startedAt != null && completedAt != null) {
            executionDuration = java.time.Duration.between(startedAt, completedAt).toMillis();
        }
    }

    /**
     * 检查步骤是否已完成（包括成功和失败）
     * 
     * @return 是否已完成
     */
    public boolean isCompleted() {
        return status == StepStatus.COMPLETED || status == StepStatus.FAILED || status == StepStatus.SKIPPED;
    }

    /**
     * 检查步骤是否成功完成
     * 
     * @return 是否成功
     */
    public boolean isSuccess() {
        return status == StepStatus.COMPLETED;
    }

    /**
     * 检查步骤是否需要补偿
     * 
     * @return 是否需要补偿
     */
    public boolean needsCompensation() {
        return compensable && isSuccess() && compensationAction != null && !compensationAction.trim().isEmpty();
    }

    /**
     * 验证步骤配置的完整性
     * 
     * @return 是否有效
     */
    public boolean isValid() {
        return stepName != null && !stepName.trim().isEmpty()
                && serviceName != null && !serviceName.trim().isEmpty()
                && forwardAction != null && !forwardAction.trim().isEmpty()
                && stepOrder != null && stepOrder >= 0
                && status != null;
    }
}