package com.example.common.saga;

import com.example.common.dto.CommonResult;
import com.example.common.event.DomainEventPublisher;
import com.example.common.event.saga.SagaStartedEvent;
import com.example.common.event.saga.SagaStepCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Saga事务协调器
 * 
 * <p>负责协调和管理Saga事务的整个生命周期，包括步骤执行、状态追踪、
 * 错误处理和补偿操作。是Saga模式实现的核心组件。
 * 
 * <p>主要职责：
 * <ul>
 *   <li>创建和启动Saga事务</li>
 *   <li>协调步骤的顺序执行</li>
 *   <li>处理步骤失败和重试</li>
 *   <li>触发补偿操作</li>
 *   <li>管理事务状态和生命周期</li>
 * </ul>
 * 
 * <p>工作流程：
 * <ol>
 *   <li>接收Saga创建请求</li>
 *   <li>初始化事务实例和步骤定义</li>
 *   <li>发布Saga开始事件</li>
 *   <li>执行步骤并监控状态</li>
 *   <li>处理成功完成或启动补偿</li>
 * </ol>
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Component
public class SagaCoordinator {

    private static final Logger logger = LoggerFactory.getLogger(SagaCoordinator.class);

    @Autowired
    private DomainEventPublisher eventPublisher;
    
    private RabbitTemplate rabbitTemplate;

    /**
     * 内存中的Saga事务缓存（实际应使用Redis或数据库）
     */
    private final Map<String, SagaTransaction> sagaCache = new ConcurrentHashMap<>();
    
    /**
     * 配置属性
     */
    private long defaultTimeoutMillis = 300000L; // 5分钟
    private int maxRetries = 3;
    private long retryIntervalMillis = 1000L;
    private boolean compensationEnabled = true;
    private long cleanupInterval = 60000L; // 1分钟
    private long retentionPeriod = 86400000L; // 24小时
    private int maxConcurrentSagas = 100;

    /**
     * 默认构造函数
     */
    public SagaCoordinator() {
    }
    
    /**
     * 构造函数，用于依赖注入
     */
    public SagaCoordinator(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    // Setter methods for configuration
    public void setDefaultTimeoutMillis(long defaultTimeoutMillis) {
        this.defaultTimeoutMillis = defaultTimeoutMillis;
    }
    
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
    
    public void setRetryIntervalMillis(long retryIntervalMillis) {
        this.retryIntervalMillis = retryIntervalMillis;
    }
    
    public void setCompensationEnabled(boolean compensationEnabled) {
        this.compensationEnabled = compensationEnabled;
    }
    
    public void setCleanupInterval(long cleanupInterval) {
        this.cleanupInterval = cleanupInterval;
    }
    
    public void setRetentionPeriod(long retentionPeriod) {
        this.retentionPeriod = retentionPeriod;
    }
    
    public void setMaxConcurrentSagas(int maxConcurrentSagas) {
        this.maxConcurrentSagas = maxConcurrentSagas;
    }

    /**
     * 创建并启动Saga事务
     * 
     * @param sagaType 事务类型
     * @param correlationId 关联ID
     * @param initiatorId 发起人ID
     * @param businessContext 业务上下文
     * @return Saga事务创建结果
     */
    public CommonResult<SagaTransaction> createSaga(String sagaType, String correlationId, Long initiatorId,
                                                   Map<String, Object> businessContext) {
        try {
            logger.info("创建Saga事务: sagaType={}, correlationId={}, initiatorId={}", 
                sagaType, correlationId, initiatorId);

            // 创建Saga事务实例
            SagaTransaction saga = SagaTransaction.create(sagaType, correlationId, initiatorId);
            
            // 设置业务上下文
            if (businessContext != null) {
                saga.setBusinessContext(businessContext);
            }

            // 缓存事务实例
            sagaCache.put(saga.getSagaId(), saga);

            // 发布Saga开始事件
            SagaStartedEvent startEvent = SagaStartedEvent.createWithContext(
                saga.getSagaId(), sagaType, initiatorId, businessContext,
                "Saga事务开始: " + sagaType, correlationId);
            
            CommonResult<Void> publishResult = eventPublisher.publishEvent(startEvent);
            if (!publishResult.getSuccess()) {
                logger.error("发布Saga开始事件失败: {}", publishResult.getMessage());
                return CommonResult.error("Saga事务创建失败: " + publishResult.getMessage());
            }

            logger.info("Saga事务创建成功: sagaId={}", saga.getSagaId());
            return CommonResult.success(saga);

        } catch (Exception e) {
            logger.error("创建Saga事务失败: sagaType={}, correlationId={}", sagaType, correlationId, e);
            return CommonResult.error("Saga事务创建失败: " + e.getMessage());
        }
    }

    /**
     * 启动Saga事务执行
     * 
     * @param sagaId Saga事务ID
     * @return 启动结果
     */
    public CommonResult<Void> startSaga(String sagaId) {
        try {
            SagaTransaction saga = sagaCache.get(sagaId);
            if (saga == null) {
                return CommonResult.error("Saga事务不存在: " + sagaId);
            }

            if (saga.getStatus() != SagaTransaction.SagaStatus.CREATED) {
                return CommonResult.error("Saga事务状态不正确，无法启动: " + saga.getStatus());
            }

            logger.info("启动Saga事务: sagaId={}, sagaType={}", sagaId, saga.getSagaType());

            // 启动事务
            saga.start();

            // 执行第一个步骤
            executeNextStep(saga);

            return CommonResult.success();

        } catch (Exception e) {
            logger.error("启动Saga事务失败: sagaId={}", sagaId, e);
            return CommonResult.error("Saga事务启动失败: " + e.getMessage());
        }
    }

    /**
     * 处理步骤完成事件
     * 
     * @param sagaId Saga事务ID
     * @param stepName 步骤名称
     * @param success 是否成功
     * @param result 执行结果
     * @param errorMessage 错误信息（如果失败）
     * @return 处理结果
     */
    public CommonResult<Void> handleStepCompletion(String sagaId, String stepName, boolean success,
                                                   Map<String, Object> result, String errorMessage) {
        try {
            SagaTransaction saga = sagaCache.get(sagaId);
            if (saga == null) {
                return CommonResult.error("Saga事务不存在: " + sagaId);
            }

            logger.info("处理步骤完成: sagaId={}, stepName={}, success={}", sagaId, stepName, success);

            SagaStep currentStep = saga.getCurrentStep();
            if (currentStep == null || !currentStep.getStepName().equals(stepName)) {
                return CommonResult.error("当前步骤不匹配: expected=" + 
                    (currentStep != null ? currentStep.getStepName() : "null") + ", actual=" + stepName);
            }

            if (success) {
                // 步骤执行成功
                currentStep.complete(result);
                
                // 发布步骤完成事件
                SagaStepCompletedEvent stepEvent = SagaStepCompletedEvent.createWithResult(
                    sagaId, stepName, currentStep.getStepOrder(), currentStep.getServiceName(),
                    result, currentStep.getExecutionDuration(), saga.getCorrelationId());
                eventPublisher.publishEvent(stepEvent);

                // 检查是否还有下一步
                if (saga.hasNextStep()) {
                    saga.moveToNextStep();
                    executeNextStep(saga);
                } else {
                    // 所有步骤完成，标记Saga成功
                    completeSaga(saga);
                }
            } else {
                // 步骤执行失败
                currentStep.fail(errorMessage);

                // 检查是否可以重试
                if (currentStep.canRetry()) {
                    currentStep.incrementRetryCount();
                    logger.info("步骤执行失败，开始重试: sagaId={}, stepName={}, retryCount={}", 
                        sagaId, stepName, currentStep.getRetryCount());
                    executeNextStep(saga); // 重新执行当前步骤
                } else {
                    // 无法重试，启动补偿流程
                    logger.warn("步骤执行失败且无法重试，启动补偿: sagaId={}, stepName={}, error={}", 
                        sagaId, stepName, errorMessage);
                    startCompensation(saga, "步骤执行失败: " + errorMessage);
                }
            }

            return CommonResult.success();

        } catch (Exception e) {
            logger.error("处理步骤完成失败: sagaId={}, stepName={}", sagaId, stepName, e);
            return CommonResult.error("处理步骤完成失败: " + e.getMessage());
        }
    }

    /**
     * 执行下一个步骤
     * 
     * @param saga Saga事务
     */
    private void executeNextStep(SagaTransaction saga) {
        SagaStep currentStep = saga.getCurrentStep();
        if (currentStep == null) {
            logger.warn("没有可执行的步骤: sagaId={}", saga.getSagaId());
            return;
        }

        try {
            logger.info("执行步骤: sagaId={}, stepName={}, serviceName={}", 
                saga.getSagaId(), currentStep.getStepName(), currentStep.getServiceName());

            // 设置步骤为执行中状态
            currentStep.start();

            // TODO: 这里应该调用具体的业务服务执行步骤
            // 实际实现中，应该通过消息队列或RPC调用相应的服务
            // 例如：serviceInvoker.invoke(currentStep.getServiceName(), currentStep.getForwardAction(), currentStep.getInputParameters());

            logger.debug("步骤开始执行，等待异步结果: sagaId={}, stepName={}", 
                saga.getSagaId(), currentStep.getStepName());

        } catch (Exception e) {
            logger.error("执行步骤失败: sagaId={}, stepName={}", saga.getSagaId(), currentStep.getStepName(), e);
            currentStep.fail("步骤执行异常: " + e.getMessage());
            
            // 启动补偿流程
            startCompensation(saga, "步骤执行异常: " + e.getMessage());
        }
    }

    /**
     * 完成Saga事务
     * 
     * @param saga Saga事务
     */
    private void completeSaga(SagaTransaction saga) {
        try {
            logger.info("Saga事务完成: sagaId={}, sagaType={}", saga.getSagaId(), saga.getSagaType());

            saga.complete();

            // TODO: 发布Saga完成事件
            // SagaCompletedEvent completeEvent = SagaCompletedEvent.create(saga.getSagaId(), ...);
            // eventPublisher.publishEvent(completeEvent);

        } catch (Exception e) {
            logger.error("完成Saga事务失败: sagaId={}", saga.getSagaId(), e);
        }
    }

    /**
     * 启动补偿流程
     * 
     * @param saga Saga事务
     * @param failureReason 失败原因
     */
    private void startCompensation(SagaTransaction saga, String failureReason) {
        try {
            logger.warn("启动Saga补偿流程: sagaId={}, reason={}", saga.getSagaId(), failureReason);

            saga.startCompensation(failureReason);

            // 获取已完成的步骤，逆序执行补偿
            List<SagaStep> completedSteps = saga.getCompletedSteps();
            for (int i = completedSteps.size() - 1; i >= 0; i--) {
                SagaStep step = completedSteps.get(i);
                if (step.needsCompensation()) {
                    executeCompensationStep(saga, step);
                }
            }

            // 标记补偿完成
            saga.completeCompensation();

        } catch (Exception e) {
            logger.error("启动补偿流程失败: sagaId={}", saga.getSagaId(), e);
            saga.fail("补偿流程异常: " + e.getMessage());
        }
    }

    /**
     * 执行补偿步骤
     * 
     * @param saga Saga事务
     * @param step 需要补偿的步骤
     */
    private void executeCompensationStep(SagaTransaction saga, SagaStep step) {
        try {
            logger.info("执行补偿步骤: sagaId={}, stepName={}, compensationAction={}", 
                saga.getSagaId(), step.getStepName(), step.getCompensationAction());

            step.startCompensation();

            // TODO: 这里应该调用具体的补偿操作
            // serviceInvoker.invoke(step.getServiceName(), step.getCompensationAction(), step.getInputParameters());

            step.completeCompensation();

        } catch (Exception e) {
            logger.error("执行补偿步骤失败: sagaId={}, stepName={}", saga.getSagaId(), step.getStepName(), e);
            step.failCompensation("补偿操作异常: " + e.getMessage());
        }
    }

    /**
     * 获取Saga事务
     * 
     * @param sagaId Saga事务ID
     * @return Saga事务
     */
    public SagaTransaction getSaga(String sagaId) {
        return sagaCache.get(sagaId);
    }

    /**
     * 检查超时的Saga事务
     * 
     * @return 处理的超时事务数量
     */
    public int handleTimeoutSagas() {
        int count = 0;
        for (SagaTransaction saga : sagaCache.values()) {
            if (saga.isTimeout() && saga.getStatus() == SagaTransaction.SagaStatus.RUNNING) {
                logger.warn("检测到超时Saga事务: sagaId={}", saga.getSagaId());
                saga.fail("事务执行超时");
                count++;
            }
        }
        return count;
    }

    /**
     * 清理已完成的Saga事务
     * 
     * @return 清理的事务数量
     */
    public int cleanupCompletedSagas() {
        int count = 0;
        Iterator<Map.Entry<String, SagaTransaction>> iterator = sagaCache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, SagaTransaction> entry = iterator.next();
            SagaTransaction saga = entry.getValue();
            
            // 清理已完成、已补偿或失败的事务（可配置保留时间）
            if (saga.getStatus() == SagaTransaction.SagaStatus.COMPLETED ||
                saga.getStatus() == SagaTransaction.SagaStatus.COMPENSATED ||
                saga.getStatus() == SagaTransaction.SagaStatus.FAILED) {
                
                iterator.remove();
                count++;
            }
        }
        
        if (count > 0) {
            logger.info("清理已完成的Saga事务: count={}", count);
        }
        
        return count;
    }

    /**
     * 获取当前活跃的Saga事务数量
     * 
     * @return 活跃事务数量
     */
    public int getActiveSagaCount() {
        return (int) sagaCache.values().stream()
                .filter(saga -> saga.getStatus() == SagaTransaction.SagaStatus.RUNNING ||
                               saga.getStatus() == SagaTransaction.SagaStatus.COMPENSATING)
                .count();
    }
}