package com.example.common.saga.compensation;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * 补偿操作定义
 * 
 * <p>定义Saga事务中某个步骤失败时需要执行的补偿操作。
 * 补偿操作用于撤销已执行步骤的副作用，维持系统的最终一致性。
 * 
 * <p>补偿原则：
 * <ul>
 *   <li>幂等性：多次执行补偿操作结果一致</li>
 *   <li>可靠性：补偿操作必须最终成功</li>
 *   <li>语义补偿：不是简单的回滚，而是业务层面的补偿</li>
 *   <li>时序性：按照与正向操作相反的顺序执行</li>
 * </ul>
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class CompensationAction {

    /**
     * 补偿操作名称
     */
    @NotBlank(message = "补偿操作名称不能为空")
    private String actionName;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 目标服务名称
     */
    @NotBlank(message = "目标服务名称不能为空")
    private String targetService;

    /**
     * 补偿方法名称
     */
    @NotBlank(message = "补偿方法名称不能为空")
    private String compensationMethod;

    /**
     * 补偿操作优先级（数字越小优先级越高）
     */
    @NotNull(message = "优先级不能为空")
    private Integer priority = 0;

    /**
     * 输入参数映射
     */
    private Map<String, Object> inputParameters = new HashMap<>();

    /**
     * 补偿条件（SpEL表达式）
     */
    private String condition;

    /**
     * 最大重试次数
     */
    private int maxRetries = 3;

    /**
     * 重试间隔（毫秒）
     */
    private long retryInterval = 1000L;

    /**
     * 是否异步执行
     */
    private boolean async = false;

    /**
     * 超时时间（毫秒）
     */
    private long timeout = 30000L;

    /**
     * 补偿策略
     */
    @NotNull(message = "补偿策略不能为空")
    private CompensationStrategy strategy = CompensationStrategy.IMMEDIATE;

    /**
     * 补偿策略枚举
     */
    public enum CompensationStrategy {
        /**
         * 立即补偿：立即执行补偿操作
         */
        IMMEDIATE("立即补偿"),
        
        /**
         * 延迟补偿：延迟执行补偿操作
         */
        DELAYED("延迟补偿"),
        
        /**
         * 条件补偿：满足条件时执行补偿操作
         */
        CONDITIONAL("条件补偿"),
        
        /**
         * 手动补偿：需要人工干预的补偿操作
         */
        MANUAL("手动补偿"),
        
        /**
         * 忽略补偿：不执行补偿操作
         */
        IGNORE("忽略补偿");

        private final String description;

        CompensationStrategy(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 创建立即补偿操作
     * 
     * @param actionName 操作名称
     * @param targetService 目标服务
     * @param compensationMethod 补偿方法
     * @return 补偿操作
     */
    public static CompensationAction immediate(String actionName, String targetService, String compensationMethod) {
        return CompensationAction.builder()
                .actionName(actionName)
                .targetService(targetService)
                .compensationMethod(compensationMethod)
                .strategy(CompensationStrategy.IMMEDIATE)
                .priority(0)
                .maxRetries(3)
                .retryInterval(1000L)
                .async(false)
                .timeout(30000L)
                .build();
    }

    /**
     * 创建延迟补偿操作
     * 
     * @param actionName 操作名称
     * @param targetService 目标服务
     * @param compensationMethod 补偿方法
     * @param delayMillis 延迟时间（毫秒）
     * @return 补偿操作
     */
    public static CompensationAction delayed(String actionName, String targetService, 
                                           String compensationMethod, long delayMillis) {
        return CompensationAction.builder()
                .actionName(actionName)
                .targetService(targetService)
                .compensationMethod(compensationMethod)
                .strategy(CompensationStrategy.DELAYED)
                .retryInterval(delayMillis)
                .priority(0)
                .maxRetries(3)
                .async(true)
                .timeout(30000L)
                .build();
    }

    /**
     * 创建条件补偿操作
     * 
     * @param actionName 操作名称
     * @param targetService 目标服务
     * @param compensationMethod 补偿方法
     * @param condition 补偿条件（SpEL表达式）
     * @return 补偿操作
     */
    public static CompensationAction conditional(String actionName, String targetService, 
                                               String compensationMethod, String condition) {
        return CompensationAction.builder()
                .actionName(actionName)
                .targetService(targetService)
                .compensationMethod(compensationMethod)
                .strategy(CompensationStrategy.CONDITIONAL)
                .condition(condition)
                .priority(0)
                .maxRetries(3)
                .retryInterval(1000L)
                .async(false)
                .timeout(30000L)
                .build();
    }

    /**
     * 创建手动补偿操作
     * 
     * @param actionName 操作名称
     * @param description 操作描述
     * @return 补偿操作
     */
    public static CompensationAction manual(String actionName, String description) {
        return CompensationAction.builder()
                .actionName(actionName)
                .description(description)
                .targetService("manual-service")
                .compensationMethod("manualCompensation")
                .strategy(CompensationStrategy.MANUAL)
                .priority(Integer.MAX_VALUE) // 最低优先级
                .maxRetries(0)
                .async(false)
                .build();
    }

    /**
     * 设置输入参数
     * 
     * @param key 参数名
     * @param value 参数值
     * @return 当前对象（支持链式调用）
     */
    public CompensationAction withParameter(String key, Object value) {
        if (inputParameters == null) {
            inputParameters = new java.util.HashMap<>();
        }
        inputParameters.put(key, value);
        return this;
    }

    /**
     * 设置优先级
     * 
     * @param priority 优先级
     * @return 当前对象（支持链式调用）
     */
    public CompensationAction withPriority(Integer priority) {
        this.priority = priority;
        return this;
    }

    /**
     * 设置重试配置
     * 
     * @param maxRetries 最大重试次数
     * @param retryInterval 重试间隔
     * @return 当前对象（支持链式调用）
     */
    public CompensationAction withRetry(int maxRetries, long retryInterval) {
        this.maxRetries = maxRetries;
        this.retryInterval = retryInterval;
        return this;
    }

    /**
     * 设置为异步执行
     * 
     * @return 当前对象（支持链式调用）
     */
    public CompensationAction asAsync() {
        this.async = true;
        return this;
    }

    /**
     * 设置超时时间
     * 
     * @param timeout 超时时间（毫秒）
     * @return 当前对象（支持链式调用）
     */
    public CompensationAction withTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * 验证补偿操作配置的完整性
     * 
     * @return 是否有效
     */
    public boolean isValid() {
        if (strategy == CompensationStrategy.MANUAL) {
            return actionName != null && !actionName.trim().isEmpty();
        }
        
        return actionName != null && !actionName.trim().isEmpty()
                && targetService != null && !targetService.trim().isEmpty()
                && compensationMethod != null && !compensationMethod.trim().isEmpty()
                && priority != null
                && strategy != null;
    }

    /**
     * 判断是否需要执行补偿
     * 
     * @param context 执行上下文
     * @return 是否需要补偿
     */
    public boolean shouldCompensate(Map<String, Object> context) {
        if (strategy == CompensationStrategy.IGNORE) {
            return false;
        }
        
        if (strategy == CompensationStrategy.CONDITIONAL && condition != null) {
            // TODO: 实现SpEL表达式求值
            // 这里简化处理，实际应使用Spring SpEL
            return evaluateCondition(condition, context);
        }
        
        return true;
    }

    /**
     * 简化的条件求值（实际应使用SpEL）
     * 
     * @param condition 条件表达式
     * @param context 上下文
     * @return 求值结果
     */
    private boolean evaluateCondition(String condition, Map<String, Object> context) {
        // 简化实现，实际应使用Spring SpEL Expression Parser
        if (condition.contains("!=")) {
            String[] parts = condition.split("!=");
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim().replace("'", "");
                Object contextValue = context.get(key);
                return !value.equals(String.valueOf(contextValue));
            }
        } else if (condition.contains("==")) {
            String[] parts = condition.split("==");
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim().replace("'", "");
                Object contextValue = context.get(key);
                return value.equals(String.valueOf(contextValue));
            }
        }
        
        // 默认返回true
        return true;
    }
}