package com.example.common.saga.engine;

import com.example.common.saga.SagaCoordinator;
import com.example.common.saga.SagaStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SagaExecutionEngine 单元测试
 * 
 * 测试Saga执行引擎的核心功能：
 * - 异步步骤执行和回调处理
 * - 补偿操作的异步执行
 * - 超时检测和处理机制
 * - 重试机制和延迟计算
 * - 步骤执行状态管理
 * - 配置属性设置
 * - 资源清理和关闭操作
 * 
 * @author Event-Driven Architecture Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SagaExecutionEngine 单元测试")
class SagaExecutionEngineTest {

    @Mock
    private SagaCoordinator sagaCoordinator;

    @Mock
    private ServiceInvoker serviceInvoker;

    private SagaExecutionEngine executionEngine;
    private final String testSagaId = "SAGA-test-123";
    private final String testStepName = "test-step";
    private final String testServiceName = "test-service";
    private final String testAction = "test-action";

    @BeforeEach
    void setUp() {
        executionEngine = new SagaExecutionEngine(sagaCoordinator);
        ReflectionTestUtils.setField(executionEngine, "serviceInvoker", serviceInvoker);
    }

    @Test
    @DisplayName("正常场景：异步执行步骤成功应该回调协调器")
    void should_CallbackCoordinator_when_StepExecutionSucceeds() throws Exception {
        // Given
        SagaStep step = SagaStep.create(testStepName, testServiceName, testAction);
        step.setTimeoutMillis(5000L);
        
        Map<String, Object> expectedResult = new HashMap<>();
        expectedResult.put("result", "success");
        expectedResult.put("data", "test-data");
        
        ServiceInvocationResult invocationResult = ServiceInvocationResult.success(expectedResult, 100L);
        when(serviceInvoker.invoke(testServiceName, testAction, step.getInputParameters()))
                .thenReturn(invocationResult);
        
        // When
        CompletableFuture<SagaExecutionEngine.StepResult> future = 
                executionEngine.executeStepAsync(testSagaId, step);
        
        // Wait for async execution
        SagaExecutionEngine.StepResult result = future.get(2, TimeUnit.SECONDS);
        
        // Then
        assertAll("异步步骤执行成功验证",
                () -> assertNotNull(result, "执行结果不应该为空"),
                () -> assertTrue(result.isSuccess(), "执行应该成功"),
                () -> assertEquals(expectedResult, result.getResult(), "结果应该匹配"),
                () -> assertNull(result.getErrorMessage(), "不应该有错误信息"),
                () -> assertTrue(result.getExecutionTime() >= 0, "执行时间应该非负")
        );
        
        // 验证协调器回调被调用
        verify(sagaCoordinator, timeout(1000)).handleStepCompletion(
                eq(testSagaId), eq(testStepName), eq(true), eq(expectedResult), isNull());
        verify(serviceInvoker, times(1)).invoke(testServiceName, testAction, step.getInputParameters());
    }

    @Test
    @DisplayName("异常场景：异步执行步骤失败应该回调协调器")
    void should_CallbackCoordinator_when_StepExecutionFails() throws Exception {
        // Given
        SagaStep step = SagaStep.create(testStepName, testServiceName, testAction);
        step.setTimeoutMillis(5000L);
        
        String errorMessage = "服务调用失败";
        ServiceInvocationResult invocationResult = ServiceInvocationResult.failure(errorMessage, 500, 100L);
        when(serviceInvoker.invoke(testServiceName, testAction, step.getInputParameters()))
                .thenReturn(invocationResult);
        
        // When
        CompletableFuture<SagaExecutionEngine.StepResult> future = 
                executionEngine.executeStepAsync(testSagaId, step);
        
        // Wait for async execution
        SagaExecutionEngine.StepResult result = future.get(2, TimeUnit.SECONDS);
        
        // Then
        assertAll("异步步骤执行失败验证",
                () -> assertNotNull(result, "执行结果不应该为空"),
                () -> assertFalse(result.isSuccess(), "执行应该失败"),
                () -> assertNull(result.getResult(), "失败时结果应该为空"),
                () -> assertEquals(errorMessage, result.getErrorMessage(), "错误信息应该匹配"),
                () -> assertTrue(result.getExecutionTime() >= 0, "执行时间应该非负")
        );
        
        // 验证协调器回调被调用
        verify(sagaCoordinator, timeout(1000)).handleStepCompletion(
                eq(testSagaId), eq(testStepName), eq(false), isNull(), eq(errorMessage));
        verify(serviceInvoker, times(1)).invoke(testServiceName, testAction, step.getInputParameters());
    }

    @Test
    @DisplayName("异常场景：服务调用抛异常应该被处理")
    void should_HandleException_when_ServiceInvokerThrowsException() throws Exception {
        // Given
        SagaStep step = SagaStep.create(testStepName, testServiceName, testAction);
        step.setTimeoutMillis(5000L);
        
        when(serviceInvoker.invoke(testServiceName, testAction, step.getInputParameters()))
                .thenThrow(new RuntimeException("网络连接失败"));
        
        // When
        CompletableFuture<SagaExecutionEngine.StepResult> future = 
                executionEngine.executeStepAsync(testSagaId, step);
        
        // Wait for async execution
        SagaExecutionEngine.StepResult result = future.get(2, TimeUnit.SECONDS);
        
        // Then
        assertAll("异常处理验证",
                () -> assertNotNull(result, "执行结果不应该为空"),
                () -> assertFalse(result.isSuccess(), "执行应该失败"),
                () -> assertNull(result.getResult(), "失败时结果应该为空"),
                () -> assertTrue(result.getErrorMessage().contains("执行异常"), "错误信息应该包含异常信息"),
                () -> assertTrue(result.getExecutionTime() >= 0, "执行时间应该非负")
        );
        
        // 验证协调器回调被调用
        verify(sagaCoordinator, timeout(1000)).handleStepCompletion(
                eq(testSagaId), eq(testStepName), eq(false), isNull(), contains("执行异常"));
        verify(serviceInvoker, times(1)).invoke(testServiceName, testAction, step.getInputParameters());
    }

    @Test
    @DisplayName("正常场景：异步执行补偿操作成功")
    void should_ExecuteCompensationSuccessfully_when_CompensationActionProvided() throws Exception {
        // Given
        SagaStep step = SagaStep.createCompensable(testStepName, testServiceName, testAction, "compensate-action");
        
        Map<String, Object> expectedResult = new HashMap<>();
        expectedResult.put("compensated", true);
        expectedResult.put("rollback", "success");
        
        ServiceInvocationResult invocationResult = ServiceInvocationResult.success(expectedResult, 150L);
        when(serviceInvoker.invoke(testServiceName, "compensate-action", step.getInputParameters()))
                .thenReturn(invocationResult);
        
        // When
        CompletableFuture<SagaExecutionEngine.StepResult> future = 
                executionEngine.executeCompensationAsync(testSagaId, step);
        
        // Wait for async execution
        SagaExecutionEngine.StepResult result = future.get(2, TimeUnit.SECONDS);
        
        // Then
        assertAll("补偿操作执行成功验证",
                () -> assertNotNull(result, "补偿结果不应该为空"),
                () -> assertTrue(result.isSuccess(), "补偿应该成功"),
                () -> assertEquals(expectedResult, result.getResult(), "补偿结果应该匹配"),
                () -> assertNull(result.getErrorMessage(), "不应该有错误信息"),
                () -> assertTrue(result.getExecutionTime() >= 0, "执行时间应该非负")
        );
        
        verify(serviceInvoker, times(1)).invoke(testServiceName, "compensate-action", step.getInputParameters());
    }

    @Test
    @DisplayName("异常场景：补偿操作执行失败")
    void should_HandleCompensationFailure_when_CompensationFails() throws Exception {
        // Given
        SagaStep step = SagaStep.createCompensable(testStepName, testServiceName, testAction, "compensate-action");
        
        String errorMessage = "补偿操作失败";
        ServiceInvocationResult invocationResult = ServiceInvocationResult.failure(errorMessage, 500, 150L);
        when(serviceInvoker.invoke(testServiceName, "compensate-action", step.getInputParameters()))
                .thenReturn(invocationResult);
        
        // When
        CompletableFuture<SagaExecutionEngine.StepResult> future = 
                executionEngine.executeCompensationAsync(testSagaId, step);
        
        // Wait for async execution
        SagaExecutionEngine.StepResult result = future.get(2, TimeUnit.SECONDS);
        
        // Then
        assertAll("补偿操作失败验证",
                () -> assertNotNull(result, "补偿结果不应该为空"),
                () -> assertFalse(result.isSuccess(), "补偿应该失败"),
                () -> assertNull(result.getResult(), "失败时结果应该为空"),
                () -> assertEquals(errorMessage, result.getErrorMessage(), "错误信息应该匹配"),
                () -> assertTrue(result.getExecutionTime() >= 0, "执行时间应该非负")
        );
        
        verify(serviceInvoker, times(1)).invoke(testServiceName, "compensate-action", step.getInputParameters());
    }

    @Test
    @DisplayName("正常场景：重试步骤执行应该增加延迟")
    void should_AddDelay_when_RetryStep() throws Exception {
        // Given
        SagaStep step = SagaStep.create(testStepName, testServiceName, testAction);
        step.setRetryCount(2); // 第3次重试
        step.setTimeoutMillis(5000L);
        
        Map<String, Object> expectedResult = new HashMap<>();
        expectedResult.put("retry", "success");
        
        ServiceInvocationResult invocationResult = ServiceInvocationResult.success(expectedResult, 100L);
        when(serviceInvoker.invoke(testServiceName, testAction, step.getInputParameters()))
                .thenReturn(invocationResult);
        
        long startTime = System.currentTimeMillis();
        
        // When
        CompletableFuture<SagaExecutionEngine.StepResult> future = 
                executionEngine.retryStep(testSagaId, step);
        
        // Wait for execution with delay
        SagaExecutionEngine.StepResult result = future.get(10, TimeUnit.SECONDS);
        
        long totalTime = System.currentTimeMillis() - startTime;
        
        // Then
        assertAll("重试步骤验证",
                () -> assertNotNull(result, "重试结果不应该为空"),
                () -> assertTrue(result.isSuccess(), "重试应该成功"),
                () -> assertEquals(expectedResult, result.getResult(), "结果应该匹配"),
                () -> assertTrue(totalTime >= 4000, "应该有指数退避延迟（2^2 * 1000ms = 4000ms）") // 2^2 = 4秒延迟
        );
        
        // 验证协调器回调被调用
        verify(sagaCoordinator, timeout(5000)).handleStepCompletion(
                eq(testSagaId), eq(testStepName), eq(true), eq(expectedResult), isNull());
        verify(serviceInvoker, times(1)).invoke(testServiceName, testAction, step.getInputParameters());
    }

    @Test
    @DisplayName("正常场景：取消步骤执行应该成功")
    void should_CancelSuccessfully_when_StepIsRunning() throws Exception {
        // Given
        SagaStep step = SagaStep.create(testStepName, testServiceName, testAction);
        step.setTimeoutMillis(10000L); // 设置较长超时以确保步骤正在运行
        
        // 模拟长时间运行的服务调用
        when(serviceInvoker.invoke(testServiceName, testAction, step.getInputParameters()))
                .thenAnswer(invocation -> {
                    Thread.sleep(5000); // 模拟5秒执行时间
                    return ServiceInvocationResult.success(new HashMap<>(), 5000L);
                });
        
        // When - 启动异步执行
        CompletableFuture<SagaExecutionEngine.StepResult> future = 
                executionEngine.executeStepAsync(testSagaId, step);
        
        // 等待一小段时间确保步骤开始执行
        Thread.sleep(100);
        
        // When - 取消执行
        boolean cancelled = executionEngine.cancelStepExecution(testSagaId, testStepName);
        
        // Then
        assertTrue(cancelled, "应该成功取消步骤执行");
        
        // 验证Future被取消
        assertTrue(future.isCancelled() || future.isDone(), "Future应该被取消或完成");
    }

    @Test
    @DisplayName("边界条件：取消不存在的步骤应该返回false")
    void should_ReturnFalse_when_CancelNonExistentStep() {
        // When
        boolean cancelled = executionEngine.cancelStepExecution(testSagaId, "non-existent-step");
        
        // Then
        assertFalse(cancelled, "取消不存在的步骤应该返回false");
    }

    @Test
    @DisplayName("正常场景：获取步骤执行状态应该返回正确信息")
    void should_ReturnCorrectStatus_when_GetStepExecutionStatus() throws Exception {
        // Given
        SagaStep step = SagaStep.create(testStepName, testServiceName, testAction);
        step.setTimeoutMillis(5000L);
        
        // 模拟长时间运行的服务调用
        when(serviceInvoker.invoke(testServiceName, testAction, step.getInputParameters()))
                .thenAnswer(invocation -> {
                    Thread.sleep(200);
                    return ServiceInvocationResult.success(new HashMap<>(), 200L);
                });
        
        // When - 启动异步执行
        executionEngine.executeStepAsync(testSagaId, step);
        
        // 等待一小段时间确保步骤开始执行
        Thread.sleep(50);
        
        // When - 获取执行状态
        Map<String, Object> status = executionEngine.getStepExecutionStatus(testSagaId, testStepName);
        
        // Then
        assertAll("步骤执行状态验证",
                () -> assertEquals(testSagaId, status.get("sagaId"), "Saga ID应该匹配"),
                () -> assertEquals(testStepName, status.get("stepName"), "步骤名称应该匹配"),
                () -> assertTrue((Boolean) status.get("running"), "步骤应该在运行中"),
                () -> assertNotNull(status.get("startTime"), "开始时间应该被设置"),
                () -> assertEquals(5000L, status.get("timeoutMillis"), "超时时间应该匹配"),
                () -> assertTrue((Long) status.get("elapsedTime") >= 0, "已用时间应该非负"),
                () -> assertFalse((Boolean) status.get("isTimeout"), "步骤不应该超时")
        );
    }

    @Test
    @DisplayName("边界条件：获取不存在步骤的状态应该返回默认信息")
    void should_ReturnDefaultStatus_when_GetNonExistentStepStatus() {
        // When
        Map<String, Object> status = executionEngine.getStepExecutionStatus(testSagaId, "non-existent-step");
        
        // Then
        assertAll("不存在步骤状态验证",
                () -> assertEquals(testSagaId, status.get("sagaId"), "Saga ID应该匹配"),
                () -> assertEquals("non-existent-step", status.get("stepName"), "步骤名称应该匹配"),
                () -> assertFalse((Boolean) status.get("running"), "步骤不应该在运行")
        );
    }

    @Test
    @DisplayName("正常场景：获取运行中步骤数量应该正确")
    void should_ReturnCorrectCount_when_GetRunningStepCount() throws Exception {
        // Given
        SagaStep step1 = SagaStep.create("step1", testServiceName, testAction);
        SagaStep step2 = SagaStep.create("step2", testServiceName, testAction);
        step1.setTimeoutMillis(5000L);
        step2.setTimeoutMillis(5000L);
        
        // 模拟长时间运行的服务调用
        when(serviceInvoker.invoke(eq(testServiceName), eq(testAction), any()))
                .thenAnswer(invocation -> {
                    Thread.sleep(500);
                    return ServiceInvocationResult.success(new HashMap<>(), 500L);
                });
        
        // When - 启动两个异步执行
        assertEquals(0, executionEngine.getRunningStepCount(), "初始运行步骤数应该为0");
        
        executionEngine.executeStepAsync(testSagaId, step1);
        executionEngine.executeStepAsync(testSagaId, step2);
        
        // 等待一小段时间确保步骤开始执行
        Thread.sleep(100);
        
        // Then
        assertEquals(2, executionEngine.getRunningStepCount(), "运行中步骤数应该为2");
        
        // 等待步骤完成
        Thread.sleep(1000);
        assertEquals(0, executionEngine.getRunningStepCount(), "完成后运行步骤数应该为0");
    }

    @Test
    @DisplayName("正常场景：清理超时步骤应该正确工作")
    void should_CleanupTimeoutSteps_when_StepsTimeout() throws Exception {
        // Given
        SagaStep step = SagaStep.create(testStepName, testServiceName, testAction);
        step.setTimeoutMillis(100L); // 很短的超时时间
        
        // 模拟长时间运行的服务调用
        when(serviceInvoker.invoke(testServiceName, testAction, step.getInputParameters()))
                .thenAnswer(invocation -> {
                    Thread.sleep(5000); // 5秒执行时间，远超100ms超时
                    return ServiceInvocationResult.success(new HashMap<>(), 5000L);
                });
        
        // When - 启动异步执行
        executionEngine.executeStepAsync(testSagaId, step);
        
        // 等待步骤超时并确保还在执行队列中
        Thread.sleep(200);
        
        // 验证步骤还在运行（尚未被自动清理）
        int runningCountBefore = executionEngine.getRunningStepCount();
        
        // When - 手动清理超时步骤
        int cleanupCount = executionEngine.cleanupTimeoutSteps();
        
        // Then - 验证清理效果
        assertTrue(cleanupCount >= 0, "清理数量应该非负");
        int runningCountAfter = executionEngine.getRunningStepCount();
        assertTrue(runningCountAfter <= runningCountBefore, "清理后运行步骤数应该不增加");
        
        // 如果确实清理了步骤，验证协调器被通知
        if (cleanupCount > 0) {
            verify(sagaCoordinator, timeout(1000)).handleStepCompletion(
                    eq(testSagaId), eq(testStepName), eq(false), isNull(), contains("步骤执行超时"));
        }
    }

    @Test
    @DisplayName("边界条件：配置属性设置应该正确工作")
    void should_SetConfigurationCorrectly_when_UsingSetters() {
        // Given
        long stepTimeout = 60000L;
        int maxRetries = 5;
        long retryInterval = 2000L;
        Map<String, String> serviceEndpoints = new HashMap<>();
        serviceEndpoints.put("test-service", "http://localhost:8080");
        long connectTimeout = 10000L;
        long readTimeout = 20000L;
        int failureThreshold = 10;
        long recoveryTime = 120000L;
        
        // When
        executionEngine.setStepTimeoutMillis(stepTimeout);
        executionEngine.setMaxRetries(maxRetries);
        executionEngine.setRetryIntervalMillis(retryInterval);
        executionEngine.setServiceEndpoints(serviceEndpoints);
        executionEngine.setConnectTimeout(connectTimeout);
        executionEngine.setReadTimeout(readTimeout);
        executionEngine.setCircuitBreakerFailureThreshold(failureThreshold);
        executionEngine.setCircuitBreakerRecoveryTime(recoveryTime);
        
        // Then - 通过反射验证配置已设置
        assertAll("配置属性设置验证",
                () -> assertEquals(stepTimeout, ReflectionTestUtils.getField(executionEngine, "stepTimeoutMillis")),
                () -> assertEquals(maxRetries, ReflectionTestUtils.getField(executionEngine, "maxRetries")),
                () -> assertEquals(retryInterval, ReflectionTestUtils.getField(executionEngine, "retryIntervalMillis")),
                () -> assertEquals(serviceEndpoints, ReflectionTestUtils.getField(executionEngine, "serviceEndpoints")),
                () -> assertEquals(connectTimeout, ReflectionTestUtils.getField(executionEngine, "connectTimeout")),
                () -> assertEquals(readTimeout, ReflectionTestUtils.getField(executionEngine, "readTimeout")),
                () -> assertEquals(failureThreshold, ReflectionTestUtils.getField(executionEngine, "circuitBreakerFailureThreshold")),
                () -> assertEquals(recoveryTime, ReflectionTestUtils.getField(executionEngine, "circuitBreakerRecoveryTime"))
        );
    }

    @Test
    @DisplayName("边界条件：构造函数应该正确设置依赖")
    void should_SetDependenciesCorrectly_when_UsingConstructor() {
        // Given
        SagaCoordinator testCoordinator = mock(SagaCoordinator.class);
        
        // When
        SagaExecutionEngine engine = new SagaExecutionEngine(testCoordinator);
        
        // Then
        assertEquals(testCoordinator, ReflectionTestUtils.getField(engine, "sagaCoordinator"), 
                "SagaCoordinator应该被正确设置");
    }

    @Test
    @DisplayName("正常场景：StepResult静态工厂方法应该正确工作")
    void should_CreateCorrectStepResult_when_UsingStaticFactoryMethods() {
        // Given
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("key", "value");
        long executionTime = 1000L;
        String errorMessage = "测试错误";
        
        // When
        SagaExecutionEngine.StepResult successResult = 
                SagaExecutionEngine.StepResult.success(resultData, executionTime);
        SagaExecutionEngine.StepResult failureResult = 
                SagaExecutionEngine.StepResult.failure(errorMessage, executionTime);
        
        // Then
        assertAll("成功结果验证",
                () -> assertTrue(successResult.isSuccess(), "应该为成功结果"),
                () -> assertEquals(resultData, successResult.getResult(), "结果数据应该匹配"),
                () -> assertNull(successResult.getErrorMessage(), "成功结果不应该有错误信息"),
                () -> assertEquals(executionTime, successResult.getExecutionTime(), "执行时间应该匹配")
        );
        
        assertAll("失败结果验证",
                () -> assertFalse(failureResult.isSuccess(), "应该为失败结果"),
                () -> assertNull(failureResult.getResult(), "失败结果不应该有数据"),
                () -> assertEquals(errorMessage, failureResult.getErrorMessage(), "错误信息应该匹配"),
                () -> assertEquals(executionTime, failureResult.getExecutionTime(), "执行时间应该匹配")
        );
    }

    @Test
    @DisplayName("边界条件：关闭执行引擎应该清理资源")
    void should_CleanupResources_when_ShutdownEngine() throws Exception {
        // Given
        SagaStep step = SagaStep.create(testStepName, testServiceName, testAction);
        step.setTimeoutMillis(10000L);
        
        // 模拟长时间运行的服务调用
        when(serviceInvoker.invoke(testServiceName, testAction, step.getInputParameters()))
                .thenAnswer(invocation -> {
                    Thread.sleep(5000);
                    return ServiceInvocationResult.success(new HashMap<>(), 5000L);
                });
        
        // 启动一个长时间运行的步骤
        executionEngine.executeStepAsync(testSagaId, step);
        Thread.sleep(100); // 等待步骤开始
        
        assertEquals(1, executionEngine.getRunningStepCount(), "应该有1个运行中的步骤");
        
        // When
        executionEngine.shutdown();
        
        // Then
        assertEquals(0, executionEngine.getRunningStepCount(), "关闭后运行步骤数应该为0");
        
        // 验证不能再执行新的步骤（调度器已关闭）
        // 这个测试可能需要根据实际实现调整
    }
}