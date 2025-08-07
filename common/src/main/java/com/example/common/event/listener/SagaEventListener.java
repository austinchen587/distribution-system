package com.example.common.event.listener;

import com.example.common.dto.CommonResult;
import com.example.common.event.saga.SagaStartedEvent;
import com.example.common.event.saga.SagaStepCompletedEvent;
import com.example.common.saga.SagaCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Saga事务事件监听器
 * 
 * <p>监听Saga事务相关的事件，协调分布式事务的执行流程。
 * 处理事务开始、步骤完成、补偿等各种事务协调事件。
 * 
 * <p>处理的事件类型：
 * <ul>
 *   <li>Saga开始事件：初始化事务并启动第一个步骤</li>
 *   <li>Saga步骤完成事件：协调下一步骤的执行</li>
 *   <li>Saga步骤失败事件：启动补偿流程</li>
 *   <li>Saga补偿事件：执行补偿操作</li>
 * </ul>
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Component
public class SagaEventListener {

    private static final Logger logger = LoggerFactory.getLogger(SagaEventListener.class);

    @Autowired
    private SagaCoordinator sagaCoordinator;

    /**
     * 处理Saga开始事件
     * 
     * <p>当Saga事务开始时，执行以下逻辑：
     * <ul>
     *   <li>验证事务参数和配置</li>
     *   <li>初始化事务状态</li>
     *   <li>启动第一个执行步骤</li>
     *   <li>设置超时监控</li>
     * </ul>
     * 
     * @param event Saga开始事件
     */
    @RabbitListener(queues = "saga.queue")
    public void handleSagaStartedEvent(SagaStartedEvent event) {
        try {
            logger.info("接收到Saga开始事件: sagaId={}, sagaType={}, initiatorId={}, correlationId={}", 
                event.getSagaId(), event.getSagaType(), event.getInitiatorId(), event.getCorrelationId());

            // 1. 验证事务参数
            if (!validateSagaParameters(event)) {
                logger.error("Saga事务参数验证失败: sagaId={}", event.getSagaId());
                return;
            }

            // 2. 启动Saga事务执行
            CommonResult<Void> startResult = sagaCoordinator.startSaga(event.getSagaId());
            if (!startResult.getSuccess()) {
                logger.error("启动Saga事务失败: sagaId={}, error={}", 
                    event.getSagaId(), startResult.getMessage());
            } else {
                logger.info("Saga事务启动成功: sagaId={}", event.getSagaId());
            }

        } catch (Exception e) {
            logger.error("处理Saga开始事件失败: sagaId={}, correlationId={}", 
                event.getSagaId(), event.getCorrelationId(), e);
        }
    }

    /**
     * 处理Saga步骤完成事件
     * 
     * <p>当Saga步骤完成时，执行以下逻辑：
     * <ul>
     *   <li>更新步骤状态</li>
     *   <li>检查是否还有后续步骤</li>
     *   <li>启动下一个步骤或完成事务</li>
     *   <li>记录执行历史</li>
     * </ul>
     * 
     * @param event Saga步骤完成事件
     */
    @RabbitListener(queues = "saga.queue")
    public void handleSagaStepCompletedEvent(SagaStepCompletedEvent event) {
        try {
            logger.info("接收到Saga步骤完成事件: sagaId={}, stepName={}, stepOrder={}, correlationId={}", 
                event.getSagaId(), event.getStepName(), event.getStepOrder(), event.getCorrelationId());

            // 1. 记录步骤执行结果
            recordStepExecution(event);

            // 2. 更新Saga状态（这里的处理逻辑会在SagaCoordinator中统一处理）
            // SagaCoordinator会监听步骤完成并自动调用handleStepCompletion

            logger.info("Saga步骤完成事件处理完成: sagaId={}, stepName={}", 
                event.getSagaId(), event.getStepName());

        } catch (Exception e) {
            logger.error("处理Saga步骤完成事件失败: sagaId={}, stepName={}, correlationId={}", 
                event.getSagaId(), event.getStepName(), event.getCorrelationId(), e);
        }
    }

    /**
     * 处理Saga步骤失败事件
     * 
     * @param eventJson Saga步骤失败事件JSON
     */
    @RabbitListener(queues = "saga.queue")
    public void handleSagaStepFailedEvent(String eventJson) {
        try {
            logger.info("接收到Saga步骤失败事件: {}", eventJson);
            
            // TODO: 解析事件并处理步骤失败逻辑
            // 1. 记录失败原因
            // 2. 判断是否可以重试
            // 3. 启动补偿流程或重试
            
        } catch (Exception e) {
            logger.error("处理Saga步骤失败事件失败", e);
        }
    }

    /**
     * 处理Saga补偿开始事件
     * 
     * @param eventJson Saga补偿开始事件JSON
     */
    @RabbitListener(queues = "saga.queue")
    public void handleSagaCompensatingEvent(String eventJson) {
        try {
            logger.info("接收到Saga补偿开始事件: {}", eventJson);
            
            // TODO: 解析事件并处理补偿开始逻辑
            // 1. 获取需要补偿的步骤列表
            // 2. 按逆序执行补偿操作
            // 3. 监控补偿执行状态
            
        } catch (Exception e) {
            logger.error("处理Saga补偿开始事件失败", e);
        }
    }

    /**
     * 处理Saga事务完成事件
     * 
     * @param eventJson Saga事务完成事件JSON
     */
    @RabbitListener(queues = "saga.queue")
    public void handleSagaCompletedEvent(String eventJson) {
        try {
            logger.info("接收到Saga事务完成事件: {}", eventJson);
            
            // TODO: 解析事件并处理事务完成逻辑
            // 1. 记录事务完成日志
            // 2. 清理临时数据
            // 3. 发送完成通知
            // 4. 更新业务统计
            
        } catch (Exception e) {
            logger.error("处理Saga事务完成事件失败", e);
        }
    }

    /**
     * 处理Saga事务失败事件
     * 
     * @param eventJson Saga事务失败事件JSON
     */
    @RabbitListener(queues = "saga.queue")
    public void handleSagaFailedEvent(String eventJson) {
        try {
            logger.info("接收到Saga事务失败事件: {}", eventJson);
            
            // TODO: 解析事件并处理事务失败逻辑
            // 1. 记录失败原因
            // 2. 发送失败通知
            // 3. 清理相关数据
            // 4. 生成失败报告
            
        } catch (Exception e) {
            logger.error("处理Saga事务失败事件失败", e);
        }
    }

    /**
     * 验证Saga事务参数
     * 
     * @param event Saga开始事件
     * @return 验证结果
     */
    private boolean validateSagaParameters(SagaStartedEvent event) {
        try {
            // 基本参数验证
            if (event.getSagaId() == null || event.getSagaId().trim().isEmpty()) {
                logger.error("Saga事务ID为空");
                return false;
            }

            if (event.getSagaType() == null || event.getSagaType().trim().isEmpty()) {
                logger.error("Saga事务类型为空");
                return false;
            }

            if (event.getInitiatorId() == null) {
                logger.error("Saga发起人ID为空");
                return false;
            }

            if (event.getCorrelationId() == null || event.getCorrelationId().trim().isEmpty()) {
                logger.error("关联ID为空");
                return false;
            }

            // 业务参数验证
            if (event.getBusinessContext() == null || event.getBusinessContext().isEmpty()) {
                logger.warn("Saga业务上下文为空: sagaId={}", event.getSagaId());
                // 警告但不阻止执行
            }

            return true;

        } catch (Exception e) {
            logger.error("验证Saga参数时发生异常: sagaId={}", event.getSagaId(), e);
            return false;
        }
    }

    /**
     * 记录步骤执行结果
     * 
     * @param event Saga步骤完成事件
     */
    private void recordStepExecution(SagaStepCompletedEvent event) {
        try {
            logger.debug("记录步骤执行结果: sagaId={}, stepName={}, executionDuration={}ms", 
                event.getSagaId(), event.getStepName(), event.getExecutionDuration());
            
            // TODO: 记录到数据库或日志系统
            // executionHistoryService.recordStepExecution(event);
            
        } catch (Exception e) {
            logger.error("记录步骤执行结果失败: sagaId={}, stepName={}", 
                event.getSagaId(), event.getStepName(), e);
            // 记录失败不影响主流程
        }
    }

    /**
     * 处理Saga超时事件
     * 
     * @param eventJson Saga超时事件JSON
     */
    @RabbitListener(queues = "saga.queue")
    public void handleSagaTimeoutEvent(String eventJson) {
        try {
            logger.warn("接收到Saga超时事件: {}", eventJson);
            
            // TODO: 解析事件并处理超时逻辑
            // 1. 取消正在执行的步骤
            // 2. 启动补偿流程
            // 3. 标记事务为超时失败
            // 4. 发送超时通知
            
        } catch (Exception e) {
            logger.error("处理Saga超时事件失败", e);
        }
    }
}