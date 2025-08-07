package com.example.common.saga;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SagaStep 单元测试
 * 
 * 测试Saga步骤的核心功能：
 * - 步骤创建和初始化
 * - 状态流转管理
 * - 正向操作和补偿操作
 * - 参数和结果管理
 * - 重试和超时机制
 * - 执行时长计算
 * 
 * @author Event-Driven Architecture Team
 */
@DisplayName("SagaStep 单元测试")
class SagaStepTest {

    private SagaStep sagaStep;
    private final String testStepName = "test-step";
    private final String testServiceName = "test-service";
    private final String testForwardAction = "forward-action";
    private final String testCompensationAction = "compensation-action";

    @BeforeEach
    void setUp() {
        sagaStep = SagaStep.create(testStepName, testServiceName, testForwardAction);
    }

    @Test
    @DisplayName("正常场景：创建Saga步骤应该设置正确的初始状态")
    void should_SetCorrectInitialState_when_CreateSagaStep() {
        // When
        SagaStep step = SagaStep.create(testStepName, testServiceName, testForwardAction);
        
        // Then
        assertAll("Saga步骤初始状态验证",
                () -> assertEquals(testStepName, step.getStepName(), "步骤名称应该正确设置"),
                () -> assertEquals(testServiceName, step.getServiceName(), "服务名称应该正确设置"),
                () -> assertEquals(testForwardAction, step.getForwardAction(), "正向操作应该正确设置"),
                () -> assertEquals(SagaStep.StepStatus.PENDING, step.getStatus(), "初始状态应该为PENDING"),
                () -> assertNull(step.getStepOrder(), "步骤序号应该为空"),
                () -> assertEquals(0, step.getRetryCount(), "重试次数应该为0"),
                () -> assertEquals(3, step.getMaxRetries(), "最大重试次数应该为3"),
                () -> assertEquals(30000L, step.getTimeoutMillis(), "超时时间应该为30秒"),
                () -> assertFalse(step.isParallel(), "默认应该不并行执行"),
                () -> assertTrue(step.isCompensable(), "默认应该可补偿"),
                () -> assertNotNull(step.getCreatedAt(), "创建时间应该被设置"),
                () -> assertNull(step.getStartedAt(), "开始时间应该为空"),
                () -> assertNotNull(step.getInputParameters(), "输入参数应该被初始化"),
                () -> assertNotNull(step.getOutputResult(), "输出结果应该被初始化"),
                () -> assertEquals(0, step.getInputParameters().size(), "初始输入参数应该为空"),
                () -> assertEquals(0, step.getOutputResult().size(), "初始输出结果应该为空")
        );
    }

    @Test
    @DisplayName("正常场景：创建可补偿步骤应该包含补偿操作")
    void should_IncludeCompensationAction_when_CreateCompensableStep() {
        // When
        SagaStep step = SagaStep.createCompensable(testStepName, testServiceName, testForwardAction, testCompensationAction);
        
        // Then
        assertAll("可补偿步骤创建验证",
                () -> assertEquals(testStepName, step.getStepName(), "步骤名称应该正确设置"),
                () -> assertEquals(testServiceName, step.getServiceName(), "服务名称应该正确设置"),
                () -> assertEquals(testForwardAction, step.getForwardAction(), "正向操作应该正确设置"),
                () -> assertEquals(testCompensationAction, step.getCompensationAction(), "补偿操作应该正确设置"),
                () -> assertTrue(step.isCompensable(), "应该可补偿"),
                () -> assertEquals(SagaStep.StepStatus.PENDING, step.getStatus(), "初始状态应该为PENDING")
        );
    }

    @Test
    @DisplayName("正常场景：启动步骤应该更新状态和时间")
    void should_UpdateStatusAndTime_when_StartStep() {
        // Given
        LocalDateTime beforeStart = LocalDateTime.now();
        
        // When
        sagaStep.start();
        
        // Then
        assertAll("步骤启动状态验证",
                () -> assertEquals(SagaStep.StepStatus.RUNNING, sagaStep.getStatus(), "状态应该更新为RUNNING"),
                () -> assertNotNull(sagaStep.getStartedAt(), "开始时间应该被设置"),
                () -> assertTrue(sagaStep.getStartedAt().isAfter(beforeStart) || sagaStep.getStartedAt().equals(beforeStart), 
                        "开始时间应该在启动时间之后或相等")
        );
    }

    @Test
    @DisplayName("正常场景：完成步骤应该更新状态、结果和时间")
    void should_UpdateStatusResultAndTime_when_CompleteStep() {
        // Given
        Map<String, Object> outputResult = new HashMap<>();
        outputResult.put("result", "success");
        outputResult.put("data", "test-data");
        sagaStep.start();
        
        // When
        sagaStep.complete(outputResult);
        
        // Then
        assertAll("步骤完成状态验证",
                () -> assertEquals(SagaStep.StepStatus.COMPLETED, sagaStep.getStatus(), "状态应该更新为COMPLETED"),
                () -> assertEquals(outputResult, sagaStep.getOutputResult(), "输出结果应该被设置"),
                () -> assertNotNull(sagaStep.getCompletedAt(), "完成时间应该被设置"),
                () -> assertTrue(sagaStep.getCompletedAt().isAfter(sagaStep.getStartedAt()), "完成时间应该晚于开始时间"),
                () -> assertNotNull(sagaStep.getExecutionDuration(), "执行时长应该被计算"),
                () -> assertTrue(sagaStep.getExecutionDuration() >= 0, "执行时长应该非负")
        );
    }

    @Test
    @DisplayName("正常场景：完成步骤时空结果应该被处理")
    void should_HandleNullResult_when_CompleteStepWithNullResult() {
        // Given
        sagaStep.start();
        
        // When
        sagaStep.complete(null);
        
        // Then
        assertAll("空结果完成验证",
                () -> assertEquals(SagaStep.StepStatus.COMPLETED, sagaStep.getStatus(), "状态应该更新为COMPLETED"),
                () -> assertNotNull(sagaStep.getOutputResult(), "输出结果不应该为null"),
                () -> assertEquals(0, sagaStep.getOutputResult().size(), "输出结果应该为空Map"),
                () -> assertNotNull(sagaStep.getCompletedAt(), "完成时间应该被设置")
        );
    }

    @Test
    @DisplayName("正常场景：失败步骤应该更新状态和错误信息")
    void should_UpdateStatusAndErrorInfo_when_FailStep() {
        // Given
        String errorMessage = "步骤执行失败";
        sagaStep.start();
        
        // When
        sagaStep.fail(errorMessage);
        
        // Then
        assertAll("步骤失败状态验证",
                () -> assertEquals(SagaStep.StepStatus.FAILED, sagaStep.getStatus(), "状态应该更新为FAILED"),
                () -> assertEquals(errorMessage, sagaStep.getErrorMessage(), "错误信息应该被设置"),
                () -> assertNotNull(sagaStep.getCompletedAt(), "完成时间应该被设置"),
                () -> assertTrue(sagaStep.getCompletedAt().isAfter(sagaStep.getStartedAt()), "完成时间应该晚于开始时间"),
                () -> assertNotNull(sagaStep.getExecutionDuration(), "执行时长应该被计算")
        );
    }

    @Test
    @DisplayName("正常场景：开始补偿应该更新状态和时间")
    void should_UpdateStatusAndTime_when_StartCompensation() {
        // Given
        sagaStep.start();
        sagaStep.complete(new HashMap<>());
        
        // When
        sagaStep.startCompensation();
        
        // Then
        assertAll("补偿开始状态验证",
                () -> assertEquals(SagaStep.StepStatus.COMPENSATING, sagaStep.getStatus(), "状态应该更新为COMPENSATING"),
                () -> assertNotNull(sagaStep.getCompensationStartedAt(), "补偿开始时间应该被设置"),
                () -> assertTrue(sagaStep.getCompensationStartedAt().isAfter(sagaStep.getCompletedAt()), "补偿开始时间应该晚于完成时间")
        );
    }

    @Test
    @DisplayName("正常场景：完成补偿应该更新状态和时间")
    void should_UpdateStatusAndTime_when_CompleteCompensation() {
        // Given
        sagaStep.start();
        sagaStep.complete(new HashMap<>());
        sagaStep.startCompensation();
        
        // When
        sagaStep.completeCompensation();
        
        // Then
        assertAll("补偿完成状态验证",
                () -> assertEquals(SagaStep.StepStatus.COMPENSATED, sagaStep.getStatus(), "状态应该更新为COMPENSATED"),
                () -> assertNotNull(sagaStep.getCompensationCompletedAt(), "补偿完成时间应该被设置"),
                () -> assertTrue(sagaStep.getCompensationCompletedAt().isAfter(sagaStep.getCompensationStartedAt()) ||
                               sagaStep.getCompensationCompletedAt().equals(sagaStep.getCompensationStartedAt()), 
                        "补偿完成时间应该晚于或等于补偿开始时间")
        );
    }

    @Test
    @DisplayName("正常场景：补偿失败应该更新状态和错误信息")
    void should_UpdateStatusAndErrorInfo_when_FailCompensation() {
        // Given
        String errorMessage = "补偿操作失败";
        sagaStep.start();
        sagaStep.complete(new HashMap<>());
        sagaStep.startCompensation();
        
        // When
        sagaStep.failCompensation(errorMessage);
        
        // Then
        assertAll("补偿失败状态验证",
                () -> assertEquals(SagaStep.StepStatus.COMPENSATION_FAILED, sagaStep.getStatus(), "状态应该更新为COMPENSATION_FAILED"),
                () -> assertEquals(errorMessage, sagaStep.getErrorMessage(), "错误信息应该被设置"),
                () -> assertNotNull(sagaStep.getCompensationCompletedAt(), "补偿完成时间应该被设置")
        );
    }

    @Test
    @DisplayName("正常场景：跳过步骤应该更新状态和时间")
    void should_UpdateStatusAndTime_when_SkipStep() {
        // When
        sagaStep.skip();
        
        // Then
        assertAll("步骤跳过状态验证",
                () -> assertEquals(SagaStep.StepStatus.SKIPPED, sagaStep.getStatus(), "状态应该更新为SKIPPED"),
                () -> assertNotNull(sagaStep.getCompletedAt(), "完成时间应该被设置")
        );
    }

    @Test
    @DisplayName("正常场景：重试机制应该正确工作")
    void should_WorkCorrectly_when_HandleRetries() {
        // Given
        assertEquals(0, sagaStep.getRetryCount(), "初始重试次数应该为0");
        assertTrue(sagaStep.canRetry(), "初始状态应该可以重试");
        
        // When & Then - 第一次重试
        sagaStep.incrementRetryCount();
        assertAll("第一次重试验证",
                () -> assertEquals(1, sagaStep.getRetryCount(), "重试次数应该为1"),
                () -> assertTrue(sagaStep.canRetry(), "应该还可以重试")
        );
        
        // When & Then - 第二次重试
        sagaStep.incrementRetryCount();
        assertAll("第二次重试验证",
                () -> assertEquals(2, sagaStep.getRetryCount(), "重试次数应该为2"),
                () -> assertTrue(sagaStep.canRetry(), "应该还可以重试")
        );
        
        // When & Then - 第三次重试
        sagaStep.incrementRetryCount();
        assertAll("第三次重试验证",
                () -> assertEquals(3, sagaStep.getRetryCount(), "重试次数应该为3"),
                () -> assertFalse(sagaStep.canRetry(), "不应该再重试")
        );
    }

    @Test
    @DisplayName("正常场景：超时检查应该正确工作")
    void should_DetectTimeoutCorrectly_when_CheckTimeout() {
        // Given - 未启动的步骤不应该超时
        assertFalse(sagaStep.isTimeout(), "未启动的步骤不应该超时");
        
        // When - 设置一个很短的超时时间并启动
        sagaStep.setTimeoutMillis(1L); // 1毫秒
        sagaStep.start();
        
        // Then - 等待一小段时间后应该超时
        try {
            Thread.sleep(10); // 等待10毫秒
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        assertTrue(sagaStep.isTimeout(), "应该检测到超时");
    }

    @Test
    @DisplayName("正常场景：输入参数管理应该正确工作")
    void should_ManageInputParametersCorrectly_when_SetAndGetParameters() {
        // Given
        String key1 = "param1";
        String value1 = "value1";
        String key2 = "param2";
        Integer value2 = 123;
        
        // When
        sagaStep.setInputParameter(key1, value1);
        sagaStep.setInputParameter(key2, value2);
        
        // Then
        assertAll("输入参数管理验证",
                () -> assertEquals(value1, sagaStep.getInputParameter(key1), "应该能获取字符串参数"),
                () -> assertEquals(value2, sagaStep.getInputParameter(key2), "应该能获取整数参数"),
                () -> assertNull(sagaStep.getInputParameter("nonexistent"), "不存在的参数应该返回null"),
                () -> assertEquals(2, sagaStep.getInputParameters().size(), "参数集合应该包含两个项目")
        );
    }

    @Test
    @DisplayName("正常场景：输出结果管理应该正确工作")
    void should_ManageOutputResultsCorrectly_when_SetAndGetResults() {
        // Given
        String key1 = "result1";
        String value1 = "success";
        String key2 = "result2";
        Double value2 = 99.99;
        
        // When
        sagaStep.setOutputResult(key1, value1);
        sagaStep.setOutputResult(key2, value2);
        
        // Then
        assertAll("输出结果管理验证",
                () -> assertEquals(value1, sagaStep.getOutputResult(key1), "应该能获取字符串结果"),
                () -> assertEquals(value2, sagaStep.getOutputResult(key2), "应该能获取数值结果"),
                () -> assertNull(sagaStep.getOutputResult("nonexistent"), "不存在的结果应该返回null"),
                () -> assertEquals(2, sagaStep.getOutputResult().size(), "结果集合应该包含两个项目")
        );
    }

    @Test
    @DisplayName("边界条件：null输入参数应该正确处理")
    void should_HandleNullInputParametersCorrectly_when_ParametersAreNull() {
        // Given
        SagaStep stepWithNullParams = SagaStep.builder()
                .stepName(testStepName)
                .serviceName(testServiceName)
                .forwardAction(testForwardAction)
                .status(SagaStep.StepStatus.PENDING)
                .inputParameters(null)
                .build();
        
        // When & Then
        assertAll("null输入参数处理验证",
                () -> assertNull(stepWithNullParams.getInputParameter("anyKey"), "null参数集合应该返回null"),
                () -> assertDoesNotThrow(() -> stepWithNullParams.setInputParameter("key", "value"), "设置参数不应该抛异常"),
                () -> assertNotNull(stepWithNullParams.getInputParameters(), "参数集合应该被初始化"),
                () -> assertEquals("value", stepWithNullParams.getInputParameter("key"), "应该能获取设置的值")
        );
    }

    @Test
    @DisplayName("边界条件：null输出结果应该正确处理")
    void should_HandleNullOutputResultCorrectly_when_ResultIsNull() {
        // Given
        SagaStep stepWithNullResult = SagaStep.builder()
                .stepName(testStepName)
                .serviceName(testServiceName)
                .forwardAction(testForwardAction)
                .status(SagaStep.StepStatus.PENDING)
                .outputResult(null)
                .build();
        
        // When & Then
        assertAll("null输出结果处理验证",
                () -> assertNull(stepWithNullResult.getOutputResult("anyKey"), "null结果集合应该返回null"),
                () -> assertDoesNotThrow(() -> stepWithNullResult.setOutputResult("key", "value"), "设置结果不应该抛异常"),
                () -> assertNotNull(stepWithNullResult.getOutputResult(), "结果集合应该被初始化"),
                () -> assertEquals("value", stepWithNullResult.getOutputResult("key"), "应该能获取设置的值")
        );
    }

    @Test
    @DisplayName("正常场景：步骤完成状态检查应该正确工作")
    void should_CheckCompletionStatusCorrectly_when_StepInDifferentStates() {
        // Given & When & Then - PENDING状态
        assertAll("PENDING状态检查",
                () -> assertFalse(sagaStep.isCompleted(), "PENDING状态不应该完成"),
                () -> assertFalse(sagaStep.isSuccess(), "PENDING状态不应该成功")
        );
        
        // When & Then - RUNNING状态
        sagaStep.start();
        assertAll("RUNNING状态检查",
                () -> assertFalse(sagaStep.isCompleted(), "RUNNING状态不应该完成"),
                () -> assertFalse(sagaStep.isSuccess(), "RUNNING状态不应该成功")
        );
        
        // When & Then - COMPLETED状态
        sagaStep.complete(new HashMap<>());
        assertAll("COMPLETED状态检查",
                () -> assertTrue(sagaStep.isCompleted(), "COMPLETED状态应该完成"),
                () -> assertTrue(sagaStep.isSuccess(), "COMPLETED状态应该成功")
        );
        
        // When & Then - FAILED状态
        SagaStep failedStep = SagaStep.create("failed-step", testServiceName, testForwardAction);
        failedStep.start();
        failedStep.fail("测试失败");
        assertAll("FAILED状态检查",
                () -> assertTrue(failedStep.isCompleted(), "FAILED状态应该完成"),
                () -> assertFalse(failedStep.isSuccess(), "FAILED状态不应该成功")
        );
        
        // When & Then - SKIPPED状态
        SagaStep skippedStep = SagaStep.create("skipped-step", testServiceName, testForwardAction);
        skippedStep.skip();
        assertAll("SKIPPED状态检查",
                () -> assertTrue(skippedStep.isCompleted(), "SKIPPED状态应该完成"),
                () -> assertFalse(skippedStep.isSuccess(), "SKIPPED状态不应该成功")
        );
    }

    @Test
    @DisplayName("正常场景：补偿需求检查应该正确工作")
    void should_CheckCompensationNeedCorrectly_when_StepInDifferentStates() {
        // Given - 创建可补偿步骤
        SagaStep compensableStep = SagaStep.createCompensable(testStepName, testServiceName, testForwardAction, testCompensationAction);
        
        // When & Then - 未完成状态不需要补偿
        assertFalse(compensableStep.needsCompensation(), "未完成的步骤不需要补偿");
        
        // When & Then - 完成状态需要补偿
        compensableStep.start();
        compensableStep.complete(new HashMap<>());
        assertTrue(compensableStep.needsCompensation(), "成功完成的可补偿步骤需要补偿");
        
        // When & Then - 失败状态不需要补偿
        SagaStep failedStep = SagaStep.createCompensable("failed-step", testServiceName, testForwardAction, testCompensationAction);
        failedStep.start();
        failedStep.fail("测试失败");
        assertFalse(failedStep.needsCompensation(), "失败的步骤不需要补偿");
        
        // When & Then - 不可补偿步骤不需要补偿
        SagaStep nonCompensableStep = SagaStep.create("non-compensable", testServiceName, testForwardAction);
        nonCompensableStep.setCompensable(false);
        nonCompensableStep.start();
        nonCompensableStep.complete(new HashMap<>());
        assertFalse(nonCompensableStep.needsCompensation(), "不可补偿步骤不需要补偿");
        
        // When & Then - 没有补偿操作的步骤不需要补偿
        SagaStep noCompensationActionStep = SagaStep.create("no-compensation", testServiceName, testForwardAction);
        noCompensationActionStep.start();
        noCompensationActionStep.complete(new HashMap<>());
        assertFalse(noCompensationActionStep.needsCompensation(), "没有补偿操作的步骤不需要补偿");
    }

    @Test
    @DisplayName("正常场景：步骤验证应该检查所有必需字段")
    void should_ValidateAllRequiredFields_when_CheckIsValid() {
        // Given - 完整的步骤，设置stepOrder
        sagaStep.setStepOrder(0);
        assertTrue(sagaStep.isValid(), "完整的步骤应该有效");
        
        // When & Then - 测试各个字段
        SagaStep invalidStep1 = SagaStep.builder()
                .stepName("") // 空名称
                .serviceName(testServiceName)
                .forwardAction(testForwardAction)
                .stepOrder(0)
                .status(SagaStep.StepStatus.PENDING)
                .build();
        assertFalse(invalidStep1.isValid(), "空步骤名称应该无效");
        
        SagaStep invalidStep2 = SagaStep.builder()
                .stepName(testStepName)
                .serviceName("") // 空服务名称
                .forwardAction(testForwardAction)
                .stepOrder(0)
                .status(SagaStep.StepStatus.PENDING)
                .build();
        assertFalse(invalidStep2.isValid(), "空服务名称应该无效");
        
        SagaStep invalidStep3 = SagaStep.builder()
                .stepName(testStepName)
                .serviceName(testServiceName)
                .forwardAction("") // 空正向操作
                .stepOrder(0)
                .status(SagaStep.StepStatus.PENDING)
                .build();
        assertFalse(invalidStep3.isValid(), "空正向操作应该无效");
        
        SagaStep invalidStep4 = SagaStep.builder()
                .stepName(testStepName)
                .serviceName(testServiceName)
                .forwardAction(testForwardAction)
                .stepOrder(null) // 空步骤序号
                .status(SagaStep.StepStatus.PENDING)
                .build();
        assertFalse(invalidStep4.isValid(), "空步骤序号应该无效");
        
        SagaStep invalidStep5 = SagaStep.builder()
                .stepName(testStepName)
                .serviceName(testServiceName)
                .forwardAction(testForwardAction)
                .stepOrder(-1) // 负数步骤序号
                .status(SagaStep.StepStatus.PENDING)
                .build();
        assertFalse(invalidStep5.isValid(), "负数步骤序号应该无效");
        
        SagaStep invalidStep6 = SagaStep.builder()
                .stepName(testStepName)
                .serviceName(testServiceName)
                .forwardAction(testForwardAction)
                .stepOrder(0)
                .status(null) // 空状态
                .build();
        assertFalse(invalidStep6.isValid(), "空状态应该无效");
    }

    @Test
    @DisplayName("边界条件：执行时长计算边界情况")
    void should_HandleExecutionDurationCalculation_when_TimestampsAreEdgeCases() {
        // Given & When - 只有开始时间没有完成时间
        sagaStep.start();
        
        // Then - 执行时长应该为null（因为没有完成时间）
        assertNull(sagaStep.getExecutionDuration(), "没有完成时间时执行时长应该为null");
        
        // When - 通过完成操作触发执行时长计算
        sagaStep.complete(new HashMap<>());
        
        // Then - 执行时长应该被计算并且非负
        assertNotNull(sagaStep.getExecutionDuration(), "完成后执行时长应该被计算");
        assertTrue(sagaStep.getExecutionDuration() >= 0, "执行时长应该非负");
        
        // 测试失败场景的执行时长计算
        SagaStep failedStep = SagaStep.create("failed-test", "service", "action");
        failedStep.start();
        failedStep.fail("测试失败");
        
        assertNotNull(failedStep.getExecutionDuration(), "失败步骤的执行时长应该被计算");
        assertTrue(failedStep.getExecutionDuration() >= 0, "失败步骤执行时长应该非负");
    }
}