package com.example.common.saga;

import com.example.common.dto.CommonResult;
import com.example.common.event.DomainEventPublisher;
import com.example.common.event.saga.SagaStartedEvent;
import com.example.common.event.saga.SagaStepCompletedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SagaCoordinator 单元测试
 * 
 * 测试Saga事务协调器的核心功能：
 * - Saga事务创建和启动
 * - 步骤执行协调和管理
 * - 步骤完成处理和流转
 * - 补偿流程触发和执行
 * - 事务状态管理和监控
 * - 超时和清理机制
 * 
 * @author Event-Driven Architecture Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SagaCoordinator 单元测试")
class SagaCoordinatorTest {

    @Mock
    private DomainEventPublisher eventPublisher;

    @Mock
    private RabbitTemplate rabbitTemplate;

    private SagaCoordinator sagaCoordinator;

    private final String testSagaType = "USER_REGISTRATION";
    private final String testCorrelationId = "test-correlation-123";
    private final Long testInitiatorId = 1001L;
    private Map<String, Object> testBusinessContext;

    @BeforeEach
    void setUp() {
        // 手动创建SagaCoordinator并注入mock依赖
        sagaCoordinator = new SagaCoordinator(rabbitTemplate);
        ReflectionTestUtils.setField(sagaCoordinator, "eventPublisher", eventPublisher);
        
        // 设置测试业务上下文
        testBusinessContext = new HashMap<>();
        testBusinessContext.put("userId", testInitiatorId);
        testBusinessContext.put("operationType", "REGISTRATION");
        testBusinessContext.put("source", "WEB");

        // 模拟事件发布器成功响应
        when(eventPublisher.publishEvent(any())).thenReturn(CommonResult.success());
    }

    @Test
    @DisplayName("正常场景：创建Saga事务应该成功")
    void should_CreateSagaSuccessfully_when_ValidParametersProvided() {
        // When
        CommonResult<SagaTransaction> result = sagaCoordinator.createSaga(
                testSagaType, testCorrelationId, testInitiatorId, testBusinessContext);
        
        // Then
        assertAll("Saga事务创建验证",
                () -> assertTrue(result.getSuccess(), "创建结果应该成功"),
                () -> assertNotNull(result.getData(), "应该返回Saga事务对象"),
                () -> assertEquals(testSagaType, result.getData().getSagaType(), "Saga类型应该正确设置"),
                () -> assertEquals(testCorrelationId, result.getData().getCorrelationId(), "关联ID应该正确设置"),
                () -> assertEquals(testInitiatorId, result.getData().getInitiatorId(), "发起人ID应该正确设置"),
                () -> assertEquals(SagaTransaction.SagaStatus.CREATED, result.getData().getStatus(), "状态应该为CREATED"),
                () -> assertEquals(testBusinessContext, result.getData().getBusinessContext(), "业务上下文应该正确设置"),
                () -> verify(eventPublisher, times(1)).publishEvent(any(SagaStartedEvent.class))
        );
        
        // 验证事务已被缓存
        SagaTransaction cachedSaga = sagaCoordinator.getSaga(result.getData().getSagaId());
        assertNotNull(cachedSaga, "事务应该被缓存");
        assertEquals(result.getData().getSagaId(), cachedSaga.getSagaId(), "缓存的事务ID应该匹配");
    }

    @Test
    @DisplayName("正常场景：创建Saga事务时空业务上下文应该被处理")
    void should_HandleNullBusinessContext_when_CreatingSaga() {
        // When
        CommonResult<SagaTransaction> result = sagaCoordinator.createSaga(
                testSagaType, testCorrelationId, testInitiatorId, null);
        
        // Then
        assertAll("空业务上下文创建验证",
                () -> assertTrue(result.getSuccess(), "创建结果应该成功"),
                () -> assertNotNull(result.getData(), "应该返回Saga事务对象"),
                () -> assertNotNull(result.getData().getBusinessContext(), "业务上下文应该被初始化"),
                () -> assertEquals(0, result.getData().getBusinessContext().size(), "业务上下文应该为空")
        );
    }

    @Test
    @DisplayName("异常场景：事件发布失败时Saga创建应该失败")
    void should_FailSagaCreation_when_EventPublishingFails() {
        // Given
        when(eventPublisher.publishEvent(any())).thenReturn(CommonResult.error("事件发布失败"));
        
        // When
        CommonResult<SagaTransaction> result = sagaCoordinator.createSaga(
                testSagaType, testCorrelationId, testInitiatorId, testBusinessContext);
        
        // Then
        assertAll("事件发布失败处理验证",
                () -> assertFalse(result.getSuccess(), "创建结果应该失败"),
                () -> assertTrue(result.getMessage().contains("Saga事务创建失败"), "错误消息应该包含创建失败信息"),
                () -> verify(eventPublisher, times(1)).publishEvent(any(SagaStartedEvent.class))
        );
    }

    @Test
    @DisplayName("异常场景：创建Saga时发生异常应该返回错误")
    void should_HandleException_when_CreatingSagaThrowsException() {
        // Given
        when(eventPublisher.publishEvent(any())).thenThrow(new RuntimeException("发布异常"));
        
        // When
        CommonResult<SagaTransaction> result = sagaCoordinator.createSaga(
                testSagaType, testCorrelationId, testInitiatorId, testBusinessContext);
        
        // Then
        assertAll("创建异常处理验证",
                () -> assertFalse(result.getSuccess(), "创建结果应该失败"),
                () -> assertTrue(result.getMessage().contains("Saga事务创建失败"), "错误消息应该包含创建失败信息")
        );
    }

    @Test
    @DisplayName("正常场景：启动Saga事务应该成功")
    void should_StartSagaSuccessfully_when_SagaExists() {
        // Given
        CommonResult<SagaTransaction> createResult = sagaCoordinator.createSaga(
                testSagaType, testCorrelationId, testInitiatorId, testBusinessContext);
        SagaTransaction saga = createResult.getData();
        
        // 添加一个测试步骤
        SagaStep step = SagaStep.create("test-step", "test-service", "test-action");
        saga.addStep(step);
        
        // When
        CommonResult<Void> startResult = sagaCoordinator.startSaga(saga.getSagaId());
        
        // Then
        assertAll("Saga启动验证",
                () -> assertTrue(startResult.getSuccess(), "启动结果应该成功"),
                () -> assertEquals(SagaTransaction.SagaStatus.RUNNING, saga.getStatus(), "Saga状态应该更新为RUNNING"),
                () -> assertNotNull(saga.getStartedAt(), "开始时间应该被设置"),
                () -> assertEquals(SagaStep.StepStatus.RUNNING, step.getStatus(), "第一个步骤应该开始执行")
        );
    }

    @Test
    @DisplayName("异常场景：启动不存在的Saga应该失败")
    void should_FailToStartSaga_when_SagaDoesNotExist() {
        // Given
        String nonExistentSagaId = "non-existent-saga-id";
        
        // When
        CommonResult<Void> result = sagaCoordinator.startSaga(nonExistentSagaId);
        
        // Then
        assertAll("不存在Saga启动验证",
                () -> assertFalse(result.getSuccess(), "启动结果应该失败"),
                () -> assertTrue(result.getMessage().contains("Saga事务不存在"), "错误消息应该指示Saga不存在")
        );
    }

    @Test
    @DisplayName("异常场景：启动状态不正确的Saga应该失败")
    void should_FailToStartSaga_when_SagaStatusIsIncorrect() {
        // Given
        CommonResult<SagaTransaction> createResult = sagaCoordinator.createSaga(
                testSagaType, testCorrelationId, testInitiatorId, testBusinessContext);
        SagaTransaction saga = createResult.getData();
        saga.start(); // 已经启动
        
        // When
        CommonResult<Void> result = sagaCoordinator.startSaga(saga.getSagaId());
        
        // Then
        assertAll("错误状态Saga启动验证",
                () -> assertFalse(result.getSuccess(), "启动结果应该失败"),
                () -> assertTrue(result.getMessage().contains("Saga事务状态不正确"), "错误消息应该指示状态不正确")
        );
    }

    @Test
    @DisplayName("正常场景：处理步骤成功完成应该继续下一步")
    void should_ProceedToNextStep_when_StepCompletesSuccessfully() {
        // Given
        CommonResult<SagaTransaction> createResult = sagaCoordinator.createSaga(
                testSagaType, testCorrelationId, testInitiatorId, testBusinessContext);
        SagaTransaction saga = createResult.getData();
        
        // 添加两个步骤
        SagaStep step1 = SagaStep.create("step1", "service1", "action1");
        SagaStep step2 = SagaStep.create("step2", "service2", "action2");
        saga.addStep(step1);
        saga.addStep(step2);
        
        sagaCoordinator.startSaga(saga.getSagaId());
        
        // 模拟第一个步骤的执行结果
        Map<String, Object> stepResult = new HashMap<>();
        stepResult.put("result", "success");
        
        // When
        CommonResult<Void> handleResult = sagaCoordinator.handleStepCompletion(
                saga.getSagaId(), "step1", true, stepResult, null);
        
        // Then
        assertAll("步骤成功完成处理验证",
                () -> assertTrue(handleResult.getSuccess(), "处理结果应该成功"),
                () -> assertEquals(SagaStep.StepStatus.COMPLETED, step1.getStatus(), "第一个步骤应该完成"),
                () -> assertEquals(stepResult, step1.getOutputResult(), "第一个步骤结果应该被设置"),
                () -> assertEquals(SagaStep.StepStatus.RUNNING, step2.getStatus(), "第二个步骤应该开始执行"),
                () -> assertEquals(1, saga.getCurrentStepIndex(), "当前步骤索引应该为1"),
                () -> verify(eventPublisher, atLeastOnce()).publishEvent(any(SagaStepCompletedEvent.class))
        );
    }

    @Test
    @DisplayName("正常场景：最后一个步骤完成应该完成Saga")
    void should_CompleteSaga_when_LastStepCompletes() {
        // Given
        CommonResult<SagaTransaction> createResult = sagaCoordinator.createSaga(
                testSagaType, testCorrelationId, testInitiatorId, testBusinessContext);
        SagaTransaction saga = createResult.getData();
        
        // 添加一个步骤
        SagaStep step = SagaStep.create("final-step", "service", "action");
        saga.addStep(step);
        
        sagaCoordinator.startSaga(saga.getSagaId());
        
        // When
        CommonResult<Void> handleResult = sagaCoordinator.handleStepCompletion(
                saga.getSagaId(), "final-step", true, new HashMap<>(), null);
        
        // Then
        assertAll("最后步骤完成验证",
                () -> assertTrue(handleResult.getSuccess(), "处理结果应该成功"),
                () -> assertEquals(SagaStep.StepStatus.COMPLETED, step.getStatus(), "步骤应该完成"),
                () -> assertEquals(SagaTransaction.SagaStatus.COMPLETED, saga.getStatus(), "Saga应该完成"),
                () -> assertNotNull(saga.getCompletedAt(), "Saga完成时间应该被设置")
        );
    }

    @Test
    @DisplayName("正常场景：步骤失败且可重试应该重试")
    void should_RetryStep_when_StepFailsAndCanRetry() {
        // Given
        CommonResult<SagaTransaction> createResult = sagaCoordinator.createSaga(
                testSagaType, testCorrelationId, testInitiatorId, testBusinessContext);
        SagaTransaction saga = createResult.getData();
        
        SagaStep step = SagaStep.create("retry-step", "service", "action");
        saga.addStep(step);
        
        sagaCoordinator.startSaga(saga.getSagaId());
        
        // When
        CommonResult<Void> handleResult = sagaCoordinator.handleStepCompletion(
                saga.getSagaId(), "retry-step", false, null, "临时错误");
        
        // Then
        assertAll("步骤重试验证",
                () -> assertTrue(handleResult.getSuccess(), "处理结果应该成功"),
                () -> assertEquals(SagaStep.StepStatus.FAILED, step.getStatus(), "步骤应该标记为失败"),
                () -> assertEquals(1, step.getRetryCount(), "重试次数应该增加"),
                () -> assertEquals("临时错误", step.getErrorMessage(), "错误信息应该被记录"),
                () -> assertEquals(SagaTransaction.SagaStatus.RUNNING, saga.getStatus(), "Saga应该继续运行")
        );
    }

    @Test
    @DisplayName("正常场景：步骤失败且无法重试应该启动补偿")
    void should_StartCompensation_when_StepFailsAndCannotRetry() {
        // Given
        CommonResult<SagaTransaction> createResult = sagaCoordinator.createSaga(
                testSagaType, testCorrelationId, testInitiatorId, testBusinessContext);
        SagaTransaction saga = createResult.getData();
        
        SagaStep step = SagaStep.create("fail-step", "service", "action");
        step.setMaxRetries(0); // 不允许重试
        saga.addStep(step);
        
        sagaCoordinator.startSaga(saga.getSagaId());
        
        // When
        CommonResult<Void> handleResult = sagaCoordinator.handleStepCompletion(
                saga.getSagaId(), "fail-step", false, null, "不可恢复错误");
        
        // Then
        assertAll("补偿启动验证",
                () -> assertTrue(handleResult.getSuccess(), "处理结果应该成功"),
                () -> assertEquals(SagaStep.StepStatus.FAILED, step.getStatus(), "步骤应该标记为失败"),
                () -> assertEquals(SagaTransaction.SagaStatus.COMPENSATED, saga.getStatus(), "Saga应该进入补偿完成状态"),
                () -> assertTrue(saga.getFailureReason().contains("步骤执行失败"), "失败原因应该被记录")
        );
    }

    @Test
    @DisplayName("异常场景：处理不存在Saga的步骤完成应该失败")
    void should_FailToHandleStepCompletion_when_SagaDoesNotExist() {
        // Given
        String nonExistentSagaId = "non-existent-saga";
        
        // When
        CommonResult<Void> result = sagaCoordinator.handleStepCompletion(
                nonExistentSagaId, "test-step", true, new HashMap<>(), null);
        
        // Then
        assertAll("不存在Saga步骤处理验证",
                () -> assertFalse(result.getSuccess(), "处理结果应该失败"),
                () -> assertTrue(result.getMessage().contains("Saga事务不存在"), "错误消息应该指示Saga不存在")
        );
    }

    @Test
    @DisplayName("异常场景：步骤名称不匹配应该失败")
    void should_FailToHandleStepCompletion_when_StepNameMismatch() {
        // Given
        CommonResult<SagaTransaction> createResult = sagaCoordinator.createSaga(
                testSagaType, testCorrelationId, testInitiatorId, testBusinessContext);
        SagaTransaction saga = createResult.getData();
        
        SagaStep step = SagaStep.create("expected-step", "service", "action");
        saga.addStep(step);
        sagaCoordinator.startSaga(saga.getSagaId());
        
        // When
        CommonResult<Void> result = sagaCoordinator.handleStepCompletion(
                saga.getSagaId(), "wrong-step", true, new HashMap<>(), null);
        
        // Then
        assertAll("步骤名称不匹配验证",
                () -> assertFalse(result.getSuccess(), "处理结果应该失败"),
                () -> assertTrue(result.getMessage().contains("当前步骤不匹配"), "错误消息应该指示步骤不匹配")
        );
    }

    @Test
    @DisplayName("正常场景：获取已缓存的Saga应该成功")
    void should_ReturnSaga_when_SagaExistsInCache() {
        // Given
        CommonResult<SagaTransaction> createResult = sagaCoordinator.createSaga(
                testSagaType, testCorrelationId, testInitiatorId, testBusinessContext);
        SagaTransaction originalSaga = createResult.getData();
        
        // When
        SagaTransaction retrievedSaga = sagaCoordinator.getSaga(originalSaga.getSagaId());
        
        // Then
        assertAll("获取缓存Saga验证",
                () -> assertNotNull(retrievedSaga, "应该返回Saga对象"),
                () -> assertEquals(originalSaga.getSagaId(), retrievedSaga.getSagaId(), "Saga ID应该匹配"),
                () -> assertSame(originalSaga, retrievedSaga, "应该返回相同的对象引用")
        );
    }

    @Test
    @DisplayName("边界条件：获取不存在的Saga应该返回null")
    void should_ReturnNull_when_SagaDoesNotExist() {
        // Given
        String nonExistentSagaId = "non-existent-saga";
        
        // When
        SagaTransaction result = sagaCoordinator.getSaga(nonExistentSagaId);
        
        // Then
        assertNull(result, "不存在的Saga应该返回null");
    }

    @Test
    @DisplayName("正常场景：处理超时Saga应该标记为失败")
    void should_MarkTimeoutSagasAsFailed_when_HandleTimeoutSagas() {
        // Given
        CommonResult<SagaTransaction> createResult = sagaCoordinator.createSaga(
                testSagaType, testCorrelationId, testInitiatorId, testBusinessContext);
        SagaTransaction saga = createResult.getData();
        
        // 设置短超时时间并启动
        saga.setTimeoutMillis(1L);
        sagaCoordinator.startSaga(saga.getSagaId());
        
        // 等待超时
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // When
        int timeoutCount = sagaCoordinator.handleTimeoutSagas();
        
        // Then
        assertAll("超时处理验证",
                () -> assertEquals(1, timeoutCount, "应该处理1个超时事务"),
                () -> assertEquals(SagaTransaction.SagaStatus.FAILED, saga.getStatus(), "超时Saga应该标记为失败"),
                () -> assertTrue(saga.getFailureReason().contains("事务执行超时"), "失败原因应该包含超时信息")
        );
    }

    @Test
    @DisplayName("正常场景：清理已完成的Saga应该从缓存中移除")
    void should_RemoveFromCache_when_CleanupCompletedSagas() {
        // Given
        CommonResult<SagaTransaction> createResult1 = sagaCoordinator.createSaga(
                testSagaType, testCorrelationId + "1", testInitiatorId, testBusinessContext);
        CommonResult<SagaTransaction> createResult2 = sagaCoordinator.createSaga(
                testSagaType, testCorrelationId + "2", testInitiatorId, testBusinessContext);
        
        SagaTransaction completedSaga = createResult1.getData();
        SagaTransaction runningSaga = createResult2.getData();
        
        // 完成第一个Saga
        completedSaga.complete();
        
        // 启动第二个Saga（保持运行状态）
        sagaCoordinator.startSaga(runningSaga.getSagaId());
        
        // When
        int cleanupCount = sagaCoordinator.cleanupCompletedSagas();
        
        // Then
        assertAll("清理已完成Saga验证",
                () -> assertEquals(1, cleanupCount, "应该清理1个已完成事务"),
                () -> assertNull(sagaCoordinator.getSaga(completedSaga.getSagaId()), "已完成的Saga应该被清理"),
                () -> assertNotNull(sagaCoordinator.getSaga(runningSaga.getSagaId()), "运行中的Saga应该保留")
        );
    }

    @Test
    @DisplayName("正常场景：获取活跃Saga数量应该正确")
    void should_ReturnCorrectCount_when_GetActiveSagaCount() {
        // Given
        CommonResult<SagaTransaction> createResult1 = sagaCoordinator.createSaga(
                testSagaType, testCorrelationId + "1", testInitiatorId, testBusinessContext);
        CommonResult<SagaTransaction> createResult2 = sagaCoordinator.createSaga(
                testSagaType, testCorrelationId + "2", testInitiatorId, testBusinessContext);
        
        SagaTransaction runningSaga = createResult1.getData();
        SagaTransaction completedSaga = createResult2.getData();
        
        // 启动一个Saga
        sagaCoordinator.startSaga(runningSaga.getSagaId());
        
        // 完成另一个Saga
        completedSaga.complete();
        
        // When
        int activeCount = sagaCoordinator.getActiveSagaCount();
        
        // Then
        assertEquals(1, activeCount, "应该有1个活跃的Saga事务");
    }

    @Test
    @DisplayName("边界条件：配置属性设置应该正确工作")
    void should_SetConfigurationCorrectly_when_UsingSetters() {
        // Given
        long newTimeout = 600000L;
        int newMaxRetries = 5;
        long newRetryInterval = 2000L;
        boolean newCompensationEnabled = false;
        long newCleanupInterval = 120000L;
        long newRetentionPeriod = 172800000L;
        int newMaxConcurrentSagas = 200;
        
        // When
        sagaCoordinator.setDefaultTimeoutMillis(newTimeout);
        sagaCoordinator.setMaxRetries(newMaxRetries);
        sagaCoordinator.setRetryIntervalMillis(newRetryInterval);
        sagaCoordinator.setCompensationEnabled(newCompensationEnabled);
        sagaCoordinator.setCleanupInterval(newCleanupInterval);
        sagaCoordinator.setRetentionPeriod(newRetentionPeriod);
        sagaCoordinator.setMaxConcurrentSagas(newMaxConcurrentSagas);
        
        // Then - 通过反射验证配置已设置（实际项目中可能需要getter方法）
        assertAll("配置属性设置验证",
                () -> assertEquals(newTimeout, ReflectionTestUtils.getField(sagaCoordinator, "defaultTimeoutMillis")),
                () -> assertEquals(newMaxRetries, ReflectionTestUtils.getField(sagaCoordinator, "maxRetries")),
                () -> assertEquals(newRetryInterval, ReflectionTestUtils.getField(sagaCoordinator, "retryIntervalMillis")),
                () -> assertEquals(newCompensationEnabled, ReflectionTestUtils.getField(sagaCoordinator, "compensationEnabled")),
                () -> assertEquals(newCleanupInterval, ReflectionTestUtils.getField(sagaCoordinator, "cleanupInterval")),
                () -> assertEquals(newRetentionPeriod, ReflectionTestUtils.getField(sagaCoordinator, "retentionPeriod")),
                () -> assertEquals(newMaxConcurrentSagas, ReflectionTestUtils.getField(sagaCoordinator, "maxConcurrentSagas"))
        );
    }

    @Test
    @DisplayName("边界条件：构造函数应该正确设置依赖")
    void should_SetDependenciesCorrectly_when_UsingConstructor() {
        // Given
        RabbitTemplate testRabbitTemplate = mock(RabbitTemplate.class);
        
        // When
        SagaCoordinator coordinator = new SagaCoordinator(testRabbitTemplate);
        
        // Then
        assertEquals(testRabbitTemplate, ReflectionTestUtils.getField(coordinator, "rabbitTemplate"), 
                "RabbitTemplate应该被正确设置");
    }
}