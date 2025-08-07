package com.example.common.saga;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SagaTransaction 单元测试
 * 
 * 测试Saga事务模型的核心功能：
 * - 事务创建和初始化
 * - 状态流转管理
 * - 步骤管理和导航
 * - 超时和重试机制
 * - 业务上下文管理
 * - 补偿机制控制
 * 
 * @author Event-Driven Architecture Team
 */
@DisplayName("SagaTransaction 单元测试")
class SagaTransactionTest {

    private SagaTransaction sagaTransaction;
    private final String testSagaType = "USER_REGISTRATION";
    private final String testCorrelationId = "test-correlation-123";
    private final Long testInitiatorId = 1001L;

    @BeforeEach
    void setUp() {
        sagaTransaction = SagaTransaction.create(testSagaType, testCorrelationId, testInitiatorId);
    }

    @Test
    @DisplayName("正常场景：创建Saga事务应该设置正确的初始状态")
    void should_SetCorrectInitialState_when_CreateSaga() {
        // When
        SagaTransaction saga = SagaTransaction.create(testSagaType, testCorrelationId, testInitiatorId);
        
        // Then
        assertAll("Saga事务初始状态验证",
                () -> assertNotNull(saga.getSagaId(), "Saga ID不能为空"),
                () -> assertTrue(saga.getSagaId().startsWith("SAGA-"), "Saga ID应该以SAGA-开头"),
                () -> assertEquals(testSagaType, saga.getSagaType(), "Saga类型应该正确设置"),
                () -> assertEquals(testCorrelationId, saga.getCorrelationId(), "关联ID应该正确设置"),
                () -> assertEquals(testInitiatorId, saga.getInitiatorId(), "发起人ID应该正确设置"),
                () -> assertEquals(SagaTransaction.SagaStatus.CREATED, saga.getStatus(), "初始状态应该为CREATED"),
                () -> assertEquals(0, saga.getCurrentStepIndex(), "当前步骤索引应该为0"),
                () -> assertEquals(0, saga.getRetryCount(), "重试次数应该为0"),
                () -> assertEquals(3, saga.getMaxRetries(), "最大重试次数应该为3"),
                () -> assertTrue(saga.isCompensationEnabled(), "补偿应该默认启用"),
                () -> assertNotNull(saga.getCreatedAt(), "创建时间应该被设置"),
                () -> assertNull(saga.getStartedAt(), "开始时间应该为空"),
                () -> assertNotNull(saga.getBusinessContext(), "业务上下文应该被初始化"),
                () -> assertNotNull(saga.getSteps(), "步骤列表应该被初始化")
        );
    }

    @Test
    @DisplayName("正常场景：生成的Saga ID应该唯一且格式正确")
    void should_GenerateUniqueAndValidSagaId_when_GenerateSagaId() {
        // When
        String sagaId1 = SagaTransaction.generateSagaId();
        String sagaId2 = SagaTransaction.generateSagaId();
        
        // Then
        assertAll("Saga ID生成验证",
                () -> assertNotNull(sagaId1, "第一个Saga ID不能为空"),
                () -> assertNotNull(sagaId2, "第二个Saga ID不能为空"),
                () -> assertNotEquals(sagaId1, sagaId2, "两个Saga ID应该不同"),
                () -> assertTrue(sagaId1.startsWith("SAGA-"), "第一个Saga ID应该以SAGA-开头"),
                () -> assertTrue(sagaId2.startsWith("SAGA-"), "第二个Saga ID应该以SAGA-开头"),
                () -> assertTrue(sagaId1.length() > 15, "Saga ID长度应该足够"),
                () -> assertTrue(sagaId1.matches("SAGA-\\d+-[A-F0-9]{8}"), "Saga ID应该符合格式规范")
        );
    }

    @Test
    @DisplayName("正常场景：启动Saga事务应该更新状态和时间")
    void should_UpdateStatusAndTime_when_StartSaga() {
        // When
        sagaTransaction.start();
        
        // Then
        assertAll("Saga启动状态验证",
                () -> assertEquals(SagaTransaction.SagaStatus.RUNNING, sagaTransaction.getStatus(), "状态应该更新为RUNNING"),
                () -> assertNotNull(sagaTransaction.getStartedAt(), "开始时间应该被设置"),
                () -> assertTrue(sagaTransaction.getStartedAt().isAfter(sagaTransaction.getCreatedAt()), "开始时间应该晚于创建时间")
        );
    }

    @Test
    @DisplayName("正常场景：添加步骤应该正确设置步骤序号")
    void should_SetCorrectStepOrder_when_AddStep() {
        // Given
        SagaStep step1 = SagaStep.create("step1", "service1", "action1");
        SagaStep step2 = SagaStep.create("step2", "service2", "action2");
        SagaStep step3 = SagaStep.create("step3", "service3", "action3");
        
        // When
        sagaTransaction.addStep(step1);
        sagaTransaction.addStep(step2);
        sagaTransaction.addStep(step3);
        
        // Then
        assertAll("步骤添加验证",
                () -> assertEquals(3, sagaTransaction.getSteps().size(), "应该有3个步骤"),
                () -> assertEquals(0, sagaTransaction.getSteps().get(0).getStepOrder(), "第一个步骤序号应该为0"),
                () -> assertEquals(1, sagaTransaction.getSteps().get(1).getStepOrder(), "第二个步骤序号应该为1"),
                () -> assertEquals(2, sagaTransaction.getSteps().get(2).getStepOrder(), "第三个步骤序号应该为2"),
                () -> assertEquals(step1, sagaTransaction.getCurrentStep(), "当前步骤应该是第一个步骤")
        );
    }

    @Test
    @DisplayName("正常场景：步骤导航应该正确工作")
    void should_NavigateStepsCorrectly_when_MoveToNextStep() {
        // Given
        SagaStep step1 = SagaStep.create("step1", "service1", "action1");
        SagaStep step2 = SagaStep.create("step2", "service2", "action2");
        SagaStep step3 = SagaStep.create("step3", "service3", "action3");
        
        sagaTransaction.addStep(step1);
        sagaTransaction.addStep(step2);
        sagaTransaction.addStep(step3);
        
        // When & Then - 第一个步骤
        assertAll("第一个步骤验证",
                () -> assertEquals(step1, sagaTransaction.getCurrentStep(), "当前步骤应该是step1"),
                () -> assertTrue(sagaTransaction.hasNextStep(), "应该有下一步"),
                () -> assertEquals(0, sagaTransaction.getCurrentStepIndex(), "当前步骤索引应该为0")
        );
        
        // When & Then - 移动到第二个步骤
        assertTrue(sagaTransaction.moveToNextStep(), "移动到下一步应该成功");
        assertAll("第二个步骤验证",
                () -> assertEquals(step2, sagaTransaction.getCurrentStep(), "当前步骤应该是step2"),
                () -> assertTrue(sagaTransaction.hasNextStep(), "应该还有下一步"),
                () -> assertEquals(1, sagaTransaction.getCurrentStepIndex(), "当前步骤索引应该为1")
        );
        
        // When & Then - 移动到第三个步骤
        assertTrue(sagaTransaction.moveToNextStep(), "移动到下一步应该成功");
        assertAll("第三个步骤验证",
                () -> assertEquals(step3, sagaTransaction.getCurrentStep(), "当前步骤应该是step3"),
                () -> assertFalse(sagaTransaction.hasNextStep(), "不应该有下一步"),
                () -> assertEquals(2, sagaTransaction.getCurrentStepIndex(), "当前步骤索引应该为2")
        );
        
        // When & Then - 尝试移动到不存在的步骤
        assertFalse(sagaTransaction.moveToNextStep(), "移动到不存在的步骤应该失败");
        assertEquals(step3, sagaTransaction.getCurrentStep(), "当前步骤应该仍是最后一个步骤");
    }

    @Test
    @DisplayName("正常场景：完成Saga事务应该更新状态和时间")
    void should_UpdateStatusAndTime_when_CompleteSaga() {
        // Given
        sagaTransaction.start();
        
        // When
        sagaTransaction.complete();
        
        // Then
        assertAll("Saga完成状态验证",
                () -> assertEquals(SagaTransaction.SagaStatus.COMPLETED, sagaTransaction.getStatus(), "状态应该更新为COMPLETED"),
                () -> assertNotNull(sagaTransaction.getCompletedAt(), "完成时间应该被设置"),
                () -> assertTrue(sagaTransaction.getCompletedAt().isAfter(sagaTransaction.getStartedAt()), "完成时间应该晚于开始时间")
        );
    }

    @Test
    @DisplayName("正常场景：启动补偿应该更新状态和失败信息")
    void should_UpdateStatusAndFailureInfo_when_StartCompensation() {
        // Given
        String failureReason = "步骤执行失败";
        sagaTransaction.start();
        
        // When
        sagaTransaction.startCompensation(failureReason);
        
        // Then
        assertAll("补偿启动状态验证",
                () -> assertEquals(SagaTransaction.SagaStatus.COMPENSATING, sagaTransaction.getStatus(), "状态应该更新为COMPENSATING"),
                () -> assertEquals(failureReason, sagaTransaction.getFailureReason(), "失败原因应该被记录"),
                () -> assertNotNull(sagaTransaction.getFailedAt(), "失败时间应该被设置")
        );
    }

    @Test
    @DisplayName("正常场景：完成补偿应该更新状态和时间")
    void should_UpdateStatusAndTime_when_CompleteCompensation() {
        // Given
        sagaTransaction.start();
        sagaTransaction.startCompensation("测试失败");
        
        // When
        sagaTransaction.completeCompensation();
        
        // Then
        assertAll("补偿完成状态验证",
                () -> assertEquals(SagaTransaction.SagaStatus.COMPENSATED, sagaTransaction.getStatus(), "状态应该更新为COMPENSATED"),
                () -> assertNotNull(sagaTransaction.getCompletedAt(), "完成时间应该被设置")
        );
    }

    @Test
    @DisplayName("正常场景：标记失败应该更新状态和失败信息")
    void should_UpdateStatusAndFailureInfo_when_FailSaga() {
        // Given
        String failureReason = "不可恢复的错误";
        
        // When
        sagaTransaction.fail(failureReason);
        
        // Then
        assertAll("失败状态验证",
                () -> assertEquals(SagaTransaction.SagaStatus.FAILED, sagaTransaction.getStatus(), "状态应该更新为FAILED"),
                () -> assertEquals(failureReason, sagaTransaction.getFailureReason(), "失败原因应该被记录"),
                () -> assertNotNull(sagaTransaction.getFailedAt(), "失败时间应该被设置")
        );
    }

    @Test
    @DisplayName("正常场景：重试机制应该正确工作")
    void should_WorkCorrectly_when_HandleRetries() {
        // Given
        assertEquals(0, sagaTransaction.getRetryCount(), "初始重试次数应该为0");
        assertTrue(sagaTransaction.canRetry(), "初始状态应该可以重试");
        
        // When & Then - 第一次重试
        sagaTransaction.incrementRetryCount();
        assertAll("第一次重试验证",
                () -> assertEquals(1, sagaTransaction.getRetryCount(), "重试次数应该为1"),
                () -> assertTrue(sagaTransaction.canRetry(), "应该还可以重试")
        );
        
        // When & Then - 第二次重试
        sagaTransaction.incrementRetryCount();
        assertAll("第二次重试验证",
                () -> assertEquals(2, sagaTransaction.getRetryCount(), "重试次数应该为2"),
                () -> assertTrue(sagaTransaction.canRetry(), "应该还可以重试")
        );
        
        // When & Then - 第三次重试
        sagaTransaction.incrementRetryCount();
        assertAll("第三次重试验证",
                () -> assertEquals(3, sagaTransaction.getRetryCount(), "重试次数应该为3"),
                () -> assertFalse(sagaTransaction.canRetry(), "不应该再重试")
        );
    }

    @Test
    @DisplayName("正常场景：超时检查应该正确工作")
    void should_DetectTimeoutCorrectly_when_CheckTimeout() {
        // Given - 未启动的Saga不应该超时
        assertFalse(sagaTransaction.isTimeout(), "未启动的Saga不应该超时");
        
        // When - 设置一个很短的超时时间并启动
        sagaTransaction.setTimeoutMillis(1L); // 1毫秒
        sagaTransaction.start();
        
        // Then - 等待一小段时间后应该超时
        try {
            Thread.sleep(10); // 等待10毫秒
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        assertTrue(sagaTransaction.isTimeout(), "应该检测到超时");
    }

    @Test
    @DisplayName("正常场景：业务上下文管理应该正确工作")
    void should_ManageBusinessContextCorrectly_when_SetAndGetContextData() {
        // Given
        String key1 = "userId";
        Long value1 = 12345L;
        String key2 = "orderInfo";
        Map<String, Object> value2 = new HashMap<>();
        value2.put("orderId", "ORDER-001");
        value2.put("amount", 100.0);
        
        // When
        sagaTransaction.setContextData(key1, value1);
        sagaTransaction.setContextData(key2, value2);
        
        // Then
        assertAll("业务上下文验证",
                () -> assertEquals(value1, sagaTransaction.getContextData(key1), "应该能获取用户ID"),
                () -> assertEquals(value2, sagaTransaction.getContextData(key2), "应该能获取订单信息"),
                () -> assertNull(sagaTransaction.getContextData("nonexistent"), "不存在的键应该返回null"),
                () -> assertEquals(value1, sagaTransaction.getContextData(key1, Long.class), "应该能进行类型转换"),
                () -> assertEquals(2, sagaTransaction.getBusinessContext().size(), "上下文应该包含两个项目")
        );
    }

    @Test
    @DisplayName("异常场景：类型转换错误应该抛出异常")
    void should_ThrowException_when_InvalidTypeCast() {
        // Given
        sagaTransaction.setContextData("stringValue", "test");
        
        // When & Then
        assertThrows(ClassCastException.class, () -> {
            sagaTransaction.getContextData("stringValue", Integer.class);
        }, "错误的类型转换应该抛出ClassCastException");
    }

    @Test
    @DisplayName("正常场景：获取已完成步骤应该正确工作")
    void should_ReturnCorrectCompletedSteps_when_GetCompletedSteps() {
        // Given
        SagaStep step1 = SagaStep.create("step1", "service1", "action1");
        SagaStep step2 = SagaStep.create("step2", "service2", "action2");
        SagaStep step3 = SagaStep.create("step3", "service3", "action3");
        
        sagaTransaction.addStep(step1);
        sagaTransaction.addStep(step2);
        sagaTransaction.addStep(step3);
        
        // When & Then - 初始状态
        assertEquals(1, sagaTransaction.getCompletedSteps().size(), "初始应该包含第一个步骤");
        
        // When & Then - 移动到第二个步骤
        sagaTransaction.moveToNextStep();
        assertEquals(2, sagaTransaction.getCompletedSteps().size(), "移动后应该包含两个步骤");
        
        // When & Then - 移动到第三个步骤
        sagaTransaction.moveToNextStep();
        assertEquals(3, sagaTransaction.getCompletedSteps().size(), "最终应该包含所有三个步骤");
    }

    @Test
    @DisplayName("边界条件：空步骤列表应该正确处理")
    void should_HandleEmptyStepListCorrectly_when_NoStepsAdded() {
        // When & Then
        assertAll("空步骤列表处理验证",
                () -> assertNull(sagaTransaction.getCurrentStep(), "当前步骤应该为空"),
                () -> assertFalse(sagaTransaction.hasNextStep(), "不应该有下一步"),
                () -> assertFalse(sagaTransaction.moveToNextStep(), "移动到下一步应该失败"),
                () -> assertEquals(0, sagaTransaction.getCompletedSteps().size(), "已完成步骤应该为空")
        );
    }

    @Test
    @DisplayName("正常场景：事务验证应该检查所有必需字段")
    void should_ValidateAllRequiredFields_when_CheckIsValid() {
        // Given - 完整的事务
        assertTrue(sagaTransaction.isValid(), "完整的事务应该有效");
        
        // When & Then - 测试各个字段
        SagaTransaction invalidSaga1 = SagaTransaction.builder()
                .sagaId("") // 空ID
                .sagaType(testSagaType)
                .correlationId(testCorrelationId)
                .initiatorId(testInitiatorId)
                .status(SagaTransaction.SagaStatus.CREATED)
                .build();
        assertFalse(invalidSaga1.isValid(), "空Saga ID应该无效");
        
        SagaTransaction invalidSaga2 = SagaTransaction.builder()
                .sagaId("SAGA-123")
                .sagaType("") // 空类型
                .correlationId(testCorrelationId)
                .initiatorId(testInitiatorId)
                .status(SagaTransaction.SagaStatus.CREATED)
                .build();
        assertFalse(invalidSaga2.isValid(), "空Saga类型应该无效");
        
        SagaTransaction invalidSaga3 = SagaTransaction.builder()
                .sagaId("SAGA-123")
                .sagaType(testSagaType)
                .correlationId("") // 空关联ID
                .initiatorId(testInitiatorId)
                .status(SagaTransaction.SagaStatus.CREATED)
                .build();
        assertFalse(invalidSaga3.isValid(), "空关联ID应该无效");
        
        SagaTransaction invalidSaga4 = SagaTransaction.builder()
                .sagaId("SAGA-123")
                .sagaType(testSagaType)
                .correlationId(testCorrelationId)
                .initiatorId(null) // 空发起人ID
                .status(SagaTransaction.SagaStatus.CREATED)
                .build();
        assertFalse(invalidSaga4.isValid(), "空发起人ID应该无效");
    }

    @Test
    @DisplayName("边界条件：null业务上下文应该正确处理")
    void should_HandleNullBusinessContextCorrectly_when_ContextIsNull() {
        // Given
        SagaTransaction sagaWithNullContext = SagaTransaction.builder()
                .sagaId("SAGA-123")
                .sagaType(testSagaType)
                .correlationId(testCorrelationId)
                .initiatorId(testInitiatorId)
                .status(SagaTransaction.SagaStatus.CREATED)
                .businessContext(null)
                .build();
        
        // When & Then
        assertAll("null上下文处理验证",
                () -> assertNull(sagaWithNullContext.getContextData("anyKey"), "null上下文应该返回null"),
                () -> assertDoesNotThrow(() -> sagaWithNullContext.setContextData("key", "value"), "设置上下文数据不应该抛异常"),
                () -> assertNotNull(sagaWithNullContext.getBusinessContext(), "上下文应该被初始化"),
                () -> assertEquals("value", sagaWithNullContext.getContextData("key"), "应该能获取设置的值")
        );
    }
}