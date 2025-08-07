package com.example.common.saga.engine;

import com.example.common.dto.CommonResult;
import com.example.common.saga.SagaCoordinator;
import com.example.common.saga.SagaStep;
import com.example.common.saga.SagaTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Saga执行引擎
 * 
 * <p>负责实际执行Saga事务的步骤，管理异步执行、超时处理、
 * 重试机制和并发控制。是Saga模式的执行核心。
 * 
 * <p>核心特性：
 * <ul>
 *   <li>异步步骤执行和结果回调</li>
 *   <li>超时检测和自动处理</li>
 *   <li>智能重试机制</li>
 *   <li>并发执行控制</li>
 *   <li>执行状态监控</li>
 * </ul>
 * 
 * <p>执行模式：
 * <ul>
 *   <li>串行执行：步骤按顺序依次执行</li>
 *   <li>并行执行：支持无依赖步骤的并行处理</li>
 *   <li>混合执行：结合串行和并行的复杂流程</li>
 * </ul>
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Component
public class SagaExecutionEngine {

    private static final Logger logger = LoggerFactory.getLogger(SagaExecutionEngine.class);

    @Autowired
    private SagaCoordinator sagaCoordinator;

    @Autowired
    private ServiceInvoker serviceInvoker;

    /**
     * 步骤执行状态缓存
     */
    private final Map<String, StepExecution> stepExecutions = new ConcurrentHashMap<>();

    /**
     * 定时任务调度器，用于超时检测
     */
    private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(2);
    
    /**
     * 配置属性
     */
    private long stepTimeoutMillis = 30000L; // 30秒
    private int maxRetries = 3;
    private long retryIntervalMillis = 1000L;
    private Map<String, String> serviceEndpoints = new ConcurrentHashMap<>();
    private long connectTimeout = 5000L;
    private long readTimeout = 10000L;
    private int circuitBreakerFailureThreshold = 5;
    private long circuitBreakerRecoveryTime = 60000L;

    /**
     * 默认构造函数
     */
    public SagaExecutionEngine() {
    }
    
    /**
     * 构造函数，用于依赖注入
     */
    public SagaExecutionEngine(SagaCoordinator sagaCoordinator) {
        this.sagaCoordinator = sagaCoordinator;
    }
    
    // Setter methods for configuration
    public void setStepTimeoutMillis(long stepTimeoutMillis) {
        this.stepTimeoutMillis = stepTimeoutMillis;
    }
    
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
    
    public void setRetryIntervalMillis(long retryIntervalMillis) {
        this.retryIntervalMillis = retryIntervalMillis;
    }
    
    public void setServiceEndpoints(Map<String, String> serviceEndpoints) {
        this.serviceEndpoints = serviceEndpoints;
    }
    
    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    
    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }
    
    public void setCircuitBreakerFailureThreshold(int circuitBreakerFailureThreshold) {
        this.circuitBreakerFailureThreshold = circuitBreakerFailureThreshold;
    }
    
    public void setCircuitBreakerRecoveryTime(long circuitBreakerRecoveryTime) {
        this.circuitBreakerRecoveryTime = circuitBreakerRecoveryTime;
    }

    /**
     * 步骤执行状态内部类
     */
    private static class StepExecution {
        private final String sagaId;
        private final String stepName;
        private final CompletableFuture<StepResult> future;
        private final long startTime;
        private final long timeoutMillis;

        public StepExecution(String sagaId, String stepName, CompletableFuture<StepResult> future, long timeoutMillis) {
            this.sagaId = sagaId;
            this.stepName = stepName;
            this.future = future;
            this.startTime = System.currentTimeMillis();
            this.timeoutMillis = timeoutMillis;
        }

        public boolean isTimeout() {
            return System.currentTimeMillis() - startTime > timeoutMillis;
        }

        // Getters
        public String getSagaId() { return sagaId; }
        public String getStepName() { return stepName; }
        public CompletableFuture<StepResult> getFuture() { return future; }
        public long getStartTime() { return startTime; }
        public long getTimeoutMillis() { return timeoutMillis; }
    }

    /**
     * 步骤执行结果
     */
    public static class StepResult {
        private final boolean success;
        private final Map<String, Object> result;
        private final String errorMessage;
        private final long executionTime;

        public StepResult(boolean success, Map<String, Object> result, String errorMessage, long executionTime) {
            this.success = success;
            this.result = result;
            this.errorMessage = errorMessage;
            this.executionTime = executionTime;
        }

        // Static factory methods
        public static StepResult success(Map<String, Object> result, long executionTime) {
            return new StepResult(true, result, null, executionTime);
        }

        public static StepResult failure(String errorMessage, long executionTime) {
            return new StepResult(false, null, errorMessage, executionTime);
        }

        // Getters
        public boolean isSuccess() { return success; }
        public Map<String, Object> getResult() { return result; }
        public String getErrorMessage() { return errorMessage; }
        public long getExecutionTime() { return executionTime; }
    }

    /**
     * 异步执行Saga步骤
     * 
     * @param sagaId Saga事务ID
     * @param step 要执行的步骤
     * @return 执行结果的Future
     */
    @Async("sagaExecutor")
    public CompletableFuture<StepResult> executeStepAsync(String sagaId, SagaStep step) {
        String executionKey = sagaId + ":" + step.getStepName();
        logger.info("开始异步执行步骤: sagaId={}, stepName={}, serviceName={}", 
            sagaId, step.getStepName(), step.getServiceName());

        CompletableFuture<StepResult> future = CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            try {
                // 调用业务服务执行步骤
                ServiceInvocationResult invocationResult = serviceInvoker.invoke(
                    step.getServiceName(), 
                    step.getForwardAction(), 
                    step.getInputParameters()
                );

                long executionTime = System.currentTimeMillis() - startTime;

                if (invocationResult.isSuccess()) {
                    logger.info("步骤执行成功: sagaId={}, stepName={}, executionTime={}ms", 
                        sagaId, step.getStepName(), executionTime);
                    return StepResult.success(invocationResult.getResult(), executionTime);
                } else {
                    logger.error("步骤执行失败: sagaId={}, stepName={}, error={}", 
                        sagaId, step.getStepName(), invocationResult.getErrorMessage());
                    return StepResult.failure(invocationResult.getErrorMessage(), executionTime);
                }

            } catch (Exception e) {
                long executionTime = System.currentTimeMillis() - startTime;
                logger.error("步骤执行异常: sagaId={}, stepName={}", sagaId, step.getStepName(), e);
                return StepResult.failure("执行异常: " + e.getMessage(), executionTime);
            }
        });

        // 缓存执行状态
        StepExecution execution = new StepExecution(sagaId, step.getStepName(), future, step.getTimeoutMillis());
        stepExecutions.put(executionKey, execution);

        // 设置结果回调
        future.whenComplete((result, throwable) -> {
            stepExecutions.remove(executionKey);
            
            if (throwable != null) {
                logger.error("步骤执行Future异常: sagaId={}, stepName={}", sagaId, step.getStepName(), throwable);
                handleStepCompletion(sagaId, step.getStepName(), false, null, throwable.getMessage());
            } else {
                handleStepCompletion(sagaId, step.getStepName(), result.isSuccess(), result.getResult(), result.getErrorMessage());
            }
        });

        // 设置超时处理
        scheduleTimeoutCheck(executionKey, step.getTimeoutMillis());

        return future;
    }

    /**
     * 执行补偿操作
     * 
     * @param sagaId Saga事务ID
     * @param step 需要补偿的步骤
     * @return 补偿结果
     */
    @Async("sagaExecutor")
    public CompletableFuture<StepResult> executeCompensationAsync(String sagaId, SagaStep step) {
        logger.info("开始执行补偿操作: sagaId={}, stepName={}, compensationAction={}", 
            sagaId, step.getStepName(), step.getCompensationAction());

        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            try {
                ServiceInvocationResult invocationResult = serviceInvoker.invoke(
                    step.getServiceName(), 
                    step.getCompensationAction(), 
                    step.getInputParameters()
                );

                long executionTime = System.currentTimeMillis() - startTime;

                if (invocationResult.isSuccess()) {
                    logger.info("补偿操作执行成功: sagaId={}, stepName={}, executionTime={}ms", 
                        sagaId, step.getStepName(), executionTime);
                    return StepResult.success(invocationResult.getResult(), executionTime);
                } else {
                    logger.error("补偿操作执行失败: sagaId={}, stepName={}, error={}", 
                        sagaId, step.getStepName(), invocationResult.getErrorMessage());
                    return StepResult.failure(invocationResult.getErrorMessage(), executionTime);
                }

            } catch (Exception e) {
                long executionTime = System.currentTimeMillis() - startTime;
                logger.error("补偿操作执行异常: sagaId={}, stepName={}", sagaId, step.getStepName(), e);
                return StepResult.failure("补偿异常: " + e.getMessage(), executionTime);
            }
        });
    }

    /**
     * 处理步骤完成
     * 
     * @param sagaId Saga事务ID
     * @param stepName 步骤名称
     * @param success 是否成功
     * @param result 执行结果
     * @param errorMessage 错误信息
     */
    private void handleStepCompletion(String sagaId, String stepName, boolean success, 
                                     Map<String, Object> result, String errorMessage) {
        try {
            sagaCoordinator.handleStepCompletion(sagaId, stepName, success, result, errorMessage);
        } catch (Exception e) {
            logger.error("处理步骤完成回调失败: sagaId={}, stepName={}", sagaId, stepName, e);
        }
    }

    /**
     * 安排超时检查
     * 
     * @param executionKey 执行键
     * @param timeoutMillis 超时时间（毫秒）
     */
    private void scheduleTimeoutCheck(String executionKey, long timeoutMillis) {
        scheduledExecutor.schedule(() -> {
            StepExecution execution = stepExecutions.get(executionKey);
            if (execution != null && execution.isTimeout()) {
                logger.warn("步骤执行超时: sagaId={}, stepName={}, timeout={}ms", 
                    execution.getSagaId(), execution.getStepName(), timeoutMillis);
                
                // 取消Future执行
                execution.getFuture().cancel(true);
                stepExecutions.remove(executionKey);
                
                // 通知协调器步骤超时
                handleStepCompletion(execution.getSagaId(), execution.getStepName(), 
                    false, null, "步骤执行超时");
            }
        }, timeoutMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * 重试执行步骤
     * 
     * @param sagaId Saga事务ID
     * @param step 要重试的步骤
     * @return 重试结果
     */
    public CompletableFuture<StepResult> retryStep(String sagaId, SagaStep step) {
        logger.info("重试执行步骤: sagaId={}, stepName={}, retryCount={}", 
            sagaId, step.getStepName(), step.getRetryCount());
        
        // 增加延迟，实现指数退避
        long delay = calculateRetryDelay(step.getRetryCount());
        
        CompletableFuture<StepResult> delayedExecution = new CompletableFuture<>();
        
        scheduledExecutor.schedule(() -> {
            try {
                CompletableFuture<StepResult> stepResult = executeStepAsync(sagaId, step);
                stepResult.whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        delayedExecution.completeExceptionally(throwable);
                    } else {
                        delayedExecution.complete(result);
                    }
                });
            } catch (Exception e) {
                delayedExecution.completeExceptionally(e);
            }
        }, delay, TimeUnit.MILLISECONDS);
        
        return delayedExecution;
    }

    /**
     * 计算重试延迟时间
     * 
     * @param retryCount 重试次数
     * @return 延迟时间（毫秒）
     */
    private long calculateRetryDelay(int retryCount) {
        // 指数退避：1s, 2s, 4s, 8s...
        return Math.min(1000L * (1L << retryCount), 30000L); // 最大30秒
    }

    /**
     * 取消步骤执行
     * 
     * @param sagaId Saga事务ID
     * @param stepName 步骤名称
     * @return 是否成功取消
     */
    public boolean cancelStepExecution(String sagaId, String stepName) {
        String executionKey = sagaId + ":" + stepName;
        StepExecution execution = stepExecutions.get(executionKey);
        
        if (execution != null) {
            boolean cancelled = execution.getFuture().cancel(true);
            if (cancelled) {
                stepExecutions.remove(executionKey);
                logger.info("成功取消步骤执行: sagaId={}, stepName={}", sagaId, stepName);
            }
            return cancelled;
        }
        
        return false;
    }

    /**
     * 获取步骤执行状态
     * 
     * @param sagaId Saga事务ID
     * @param stepName 步骤名称
     * @return 执行状态信息
     */
    public Map<String, Object> getStepExecutionStatus(String sagaId, String stepName) {
        String executionKey = sagaId + ":" + stepName;
        StepExecution execution = stepExecutions.get(executionKey);
        
        Map<String, Object> status = new ConcurrentHashMap<>();
        status.put("sagaId", sagaId);
        status.put("stepName", stepName);
        
        if (execution != null) {
            status.put("running", true);
            status.put("startTime", execution.getStartTime());
            status.put("timeoutMillis", execution.getTimeoutMillis());
            status.put("elapsedTime", System.currentTimeMillis() - execution.getStartTime());
            status.put("isTimeout", execution.isTimeout());
        } else {
            status.put("running", false);
        }
        
        return status;
    }

    /**
     * 获取当前执行中的步骤数量
     * 
     * @return 执行中的步骤数量
     */
    public int getRunningStepCount() {
        return stepExecutions.size();
    }

    /**
     * 清理超时的步骤执行
     * 
     * @return 清理的步骤数量
     */
    public int cleanupTimeoutSteps() {
        int count = 0;
        Iterator<Map.Entry<String, StepExecution>> iterator = stepExecutions.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<String, StepExecution> entry = iterator.next();
            StepExecution execution = entry.getValue();
            
            if (execution.isTimeout()) {
                logger.warn("清理超时步骤: sagaId={}, stepName={}", 
                    execution.getSagaId(), execution.getStepName());
                
                execution.getFuture().cancel(true);
                iterator.remove();
                count++;
                
                // 通知协调器步骤超时
                handleStepCompletion(execution.getSagaId(), execution.getStepName(), 
                    false, null, "步骤执行超时被清理");
            }
        }
        
        if (count > 0) {
            logger.info("清理超时步骤完成: count={}", count);
        }
        
        return count;
    }

    /**
     * 关闭执行引擎
     */
    public void shutdown() {
        logger.info("关闭Saga执行引擎...");
        
        // 取消所有正在执行的步骤
        stepExecutions.forEach((key, execution) -> {
            execution.getFuture().cancel(true);
        });
        stepExecutions.clear();
        
        // 关闭调度器
        scheduledExecutor.shutdown();
        try {
            if (!scheduledExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                scheduledExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduledExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        logger.info("Saga执行引擎已关闭");
    }
}