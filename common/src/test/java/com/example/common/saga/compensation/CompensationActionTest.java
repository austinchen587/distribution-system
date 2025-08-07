package com.example.common.saga.compensation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CompensationAction 单元测试
 * 
 * 测试补偿操作的核心功能：
 * - 静态工厂方法创建不同类型的补偿操作
 * - 补偿配置的验证和有效性检查
 * - 链式调用和参数设置
 * - 条件补偿的评估机制
 * - 补偿策略的正确应用
 * - 配置参数的边界值处理
 * 
 * @author Event-Driven Architecture Team
 */
@DisplayName("CompensationAction 单元测试")
class CompensationActionTest {

    private CompensationAction compensationAction;
    private final String testActionName = "test-compensation";
    private final String testTargetService = "test-service";
    private final String testCompensationMethod = "compensateAction";

    @BeforeEach
    void setUp() {
        compensationAction = new CompensationAction();
    }

    @Test
    @DisplayName("正常场景：immediate工厂方法应该创建立即补偿操作")
    void should_CreateImmediateCompensation_when_UsingImmediateFactory() {
        // When
        CompensationAction action = CompensationAction.immediate(
                testActionName, testTargetService, testCompensationMethod);

        // Then
        assertAll("立即补偿操作验证",
                () -> assertEquals(testActionName, action.getActionName(), "操作名称应该匹配"),
                () -> assertEquals(testTargetService, action.getTargetService(), "目标服务应该匹配"),
                () -> assertEquals(testCompensationMethod, action.getCompensationMethod(), "补偿方法应该匹配"),
                () -> assertEquals(CompensationAction.CompensationStrategy.IMMEDIATE, action.getStrategy(), "策略应该为立即补偿"),
                () -> assertEquals(0, action.getPriority(), "优先级应该为0"),
                () -> assertEquals(3, action.getMaxRetries(), "最大重试次数应该为3"),
                () -> assertEquals(1000L, action.getRetryInterval(), "重试间隔应该为1000ms"),
                () -> assertFalse(action.isAsync(), "应该为同步执行"),
                () -> assertEquals(30000L, action.getTimeout(), "超时时间应该为30秒"),
                () -> assertTrue(action.isValid(), "配置应该有效")
        );
    }

    @Test
    @DisplayName("正常场景：delayed工厂方法应该创建延迟补偿操作")
    void should_CreateDelayedCompensation_when_UsingDelayedFactory() {
        // Given
        long delayMillis = 5000L;

        // When
        CompensationAction action = CompensationAction.delayed(
                testActionName, testTargetService, testCompensationMethod, delayMillis);

        // Then
        assertAll("延迟补偿操作验证",
                () -> assertEquals(testActionName, action.getActionName(), "操作名称应该匹配"),
                () -> assertEquals(testTargetService, action.getTargetService(), "目标服务应该匹配"),
                () -> assertEquals(testCompensationMethod, action.getCompensationMethod(), "补偿方法应该匹配"),
                () -> assertEquals(CompensationAction.CompensationStrategy.DELAYED, action.getStrategy(), "策略应该为延迟补偿"),
                () -> assertEquals(delayMillis, action.getRetryInterval(), "延迟时间应该匹配"),
                () -> assertTrue(action.isAsync(), "应该为异步执行"),
                () -> assertTrue(action.isValid(), "配置应该有效")
        );
    }

    @Test
    @DisplayName("正常场景：conditional工厂方法应该创建条件补偿操作")
    void should_CreateConditionalCompensation_when_UsingConditionalFactory() {
        // Given
        String condition = "status == 'failed'";

        // When
        CompensationAction action = CompensationAction.conditional(
                testActionName, testTargetService, testCompensationMethod, condition);

        // Then
        assertAll("条件补偿操作验证",
                () -> assertEquals(testActionName, action.getActionName(), "操作名称应该匹配"),
                () -> assertEquals(testTargetService, action.getTargetService(), "目标服务应该匹配"),
                () -> assertEquals(testCompensationMethod, action.getCompensationMethod(), "补偿方法应该匹配"),
                () -> assertEquals(CompensationAction.CompensationStrategy.CONDITIONAL, action.getStrategy(), "策略应该为条件补偿"),
                () -> assertEquals(condition, action.getCondition(), "条件表达式应该匹配"),
                () -> assertFalse(action.isAsync(), "应该为同步执行"),
                () -> assertTrue(action.isValid(), "配置应该有效")
        );
    }

    @Test
    @DisplayName("正常场景：manual工厂方法应该创建手动补偿操作")
    void should_CreateManualCompensation_when_UsingManualFactory() {
        // Given
        String description = "需要人工处理的补偿操作";

        // When
        CompensationAction action = CompensationAction.manual(testActionName, description);

        // Then
        assertAll("手动补偿操作验证",
                () -> assertEquals(testActionName, action.getActionName(), "操作名称应该匹配"),
                () -> assertEquals(description, action.getDescription(), "描述应该匹配"),
                () -> assertEquals("manual-service", action.getTargetService(), "目标服务应该为manual-service"),
                () -> assertEquals("manualCompensation", action.getCompensationMethod(), "补偿方法应该为manualCompensation"),
                () -> assertEquals(CompensationAction.CompensationStrategy.MANUAL, action.getStrategy(), "策略应该为手动补偿"),
                () -> assertEquals(Integer.MAX_VALUE, action.getPriority(), "优先级应该为最低"),
                () -> assertEquals(0, action.getMaxRetries(), "最大重试次数应该为0"),
                () -> assertFalse(action.isAsync(), "应该为同步执行"),
                () -> assertTrue(action.isValid(), "配置应该有效")
        );
    }

    @Test
    @DisplayName("正常场景：链式调用设置参数应该正确工作")
    void should_SetParametersCorrectly_when_UsingChainedCalls() {
        // Given
        CompensationAction action = CompensationAction.immediate(
                testActionName, testTargetService, testCompensationMethod);

        // When
        CompensationAction result = action
                .withParameter("userId", 123L)
                .withParameter("reason", "test")
                .withPriority(5)
                .withRetry(2, 2000L)
                .asAsync()
                .withTimeout(60000L);

        // Then
        assertSame(action, result, "链式调用应该返回同一个对象");
        
        assertAll("链式调用参数设置验证",
                () -> assertEquals(123L, action.getInputParameters().get("userId"), "用户ID参数应该匹配"),
                () -> assertEquals("test", action.getInputParameters().get("reason"), "原因参数应该匹配"),
                () -> assertEquals(5, action.getPriority(), "优先级应该为5"),
                () -> assertEquals(2, action.getMaxRetries(), "最大重试次数应该为2"),
                () -> assertEquals(2000L, action.getRetryInterval(), "重试间隔应该为2000ms"),
                () -> assertTrue(action.isAsync(), "应该为异步执行"),
                () -> assertEquals(60000L, action.getTimeout(), "超时时间应该为60秒")
        );
    }

    @Test
    @DisplayName("边界条件：空参数创建补偿操作应该无效")
    void should_BeInvalid_when_CreatedWithEmptyParameters() {
        // Given
        CompensationAction action = new CompensationAction();

        // When & Then
        assertFalse(action.isValid(), "空配置的补偿操作应该无效");
    }

    @Test
    @DisplayName("边界条件：部分参数缺失的补偿操作应该无效")
    void should_BeInvalid_when_MissingRequiredParameters() {
        // Given
        CompensationAction action = new CompensationAction();
        action.setActionName(testActionName);
        // 缺少targetService和compensationMethod

        // When & Then
        assertFalse(action.isValid(), "缺少必需参数的补偿操作应该无效");
    }

    @Test
    @DisplayName("边界条件：手动补偿只需要actionName就有效")
    void should_BeValid_when_ManualCompensationWithOnlyActionName() {
        // Given
        CompensationAction action = new CompensationAction();
        action.setActionName(testActionName);
        action.setStrategy(CompensationAction.CompensationStrategy.MANUAL);

        // When & Then
        assertTrue(action.isValid(), "手动补偿只需要操作名称就应该有效");
    }

    @Test
    @DisplayName("正常场景：shouldCompensate应该根据策略正确判断")
    void should_EvaluateCompensationNeed_when_CheckingShouldCompensate() {
        // Given
        Map<String, Object> context = new HashMap<>();
        context.put("status", "failed");

        // Test IGNORE strategy
        CompensationAction ignoreAction = new CompensationAction();
        ignoreAction.setStrategy(CompensationAction.CompensationStrategy.IGNORE);
        assertFalse(ignoreAction.shouldCompensate(context), "IGNORE策略应该不需要补偿");

        // Test IMMEDIATE strategy
        CompensationAction immediateAction = new CompensationAction();
        immediateAction.setStrategy(CompensationAction.CompensationStrategy.IMMEDIATE);
        assertTrue(immediateAction.shouldCompensate(context), "IMMEDIATE策略应该需要补偿");

        // Test CONDITIONAL strategy with matching condition
        CompensationAction conditionalAction = new CompensationAction();
        conditionalAction.setStrategy(CompensationAction.CompensationStrategy.CONDITIONAL);
        conditionalAction.setCondition("status == 'failed'");
        assertTrue(conditionalAction.shouldCompensate(context), "匹配条件的CONDITIONAL策略应该需要补偿");

        // Test CONDITIONAL strategy with non-matching condition
        CompensationAction nonMatchingAction = new CompensationAction();
        nonMatchingAction.setStrategy(CompensationAction.CompensationStrategy.CONDITIONAL);
        nonMatchingAction.setCondition("status == 'success'");
        assertFalse(nonMatchingAction.shouldCompensate(context), "不匹配条件的CONDITIONAL策略应该不需要补偿");
    }

    @Test
    @DisplayName("正常场景：条件求值应该正确处理等于操作")
    void should_EvaluateConditionCorrectly_when_UsingEqualsOperator() {
        // Given
        CompensationAction action = CompensationAction.conditional(
                testActionName, testTargetService, testCompensationMethod, "status == 'failed'");
        
        Map<String, Object> matchingContext = new HashMap<>();
        matchingContext.put("status", "failed");
        
        Map<String, Object> nonMatchingContext = new HashMap<>();
        nonMatchingContext.put("status", "success");

        // When & Then
        assertTrue(action.shouldCompensate(matchingContext), "匹配的状态应该需要补偿");
        assertFalse(action.shouldCompensate(nonMatchingContext), "不匹配的状态应该不需要补偿");
    }

    @Test
    @DisplayName("正常场景：条件求值应该正确处理不等于操作")
    void should_EvaluateConditionCorrectly_when_UsingNotEqualsOperator() {
        // Given
        CompensationAction action = CompensationAction.conditional(
                testActionName, testTargetService, testCompensationMethod, "status != 'success'");
        
        Map<String, Object> matchingContext = new HashMap<>();
        matchingContext.put("status", "failed");
        
        Map<String, Object> nonMatchingContext = new HashMap<>();
        nonMatchingContext.put("status", "success");

        // When & Then
        assertTrue(action.shouldCompensate(matchingContext), "不等于成功的状态应该需要补偿");
        assertFalse(action.shouldCompensate(nonMatchingContext), "等于成功的状态应该不需要补偿");
    }

    @Test
    @DisplayName("边界条件：无效条件表达式应该默认返回true")
    void should_DefaultToTrue_when_InvalidConditionExpression() {
        // Given
        CompensationAction action = CompensationAction.conditional(
                testActionName, testTargetService, testCompensationMethod, "invalid expression");
        
        Map<String, Object> context = new HashMap<>();
        context.put("status", "failed");

        // When & Then
        assertTrue(action.shouldCompensate(context), "无效条件表达式应该默认返回true");
    }

    @Test
    @DisplayName("边界条件：null条件应该返回true")
    void should_ReturnTrue_when_NullCondition() {
        // Given
        CompensationAction action = new CompensationAction();
        action.setStrategy(CompensationAction.CompensationStrategy.CONDITIONAL);
        action.setCondition(null);
        
        Map<String, Object> context = new HashMap<>();

        // When & Then
        assertTrue(action.shouldCompensate(context), "null条件应该返回true");
    }

    @Test
    @DisplayName("正常场景：withParameter应该正确处理null参数映射")
    void should_InitializeParametersMap_when_ParametersIsNull() {
        // Given
        CompensationAction action = new CompensationAction();
        action.setInputParameters(null);

        // When
        action.withParameter("key", "value");

        // Then
        assertNotNull(action.getInputParameters(), "参数映射应该被初始化");
        assertEquals("value", action.getInputParameters().get("key"), "参数值应该正确设置");
    }

    @Test
    @DisplayName("边界条件：补偿策略枚举应该包含所有预期值")
    void should_ContainAllExpectedValues_when_CheckingCompensationStrategy() {
        // When & Then
        CompensationAction.CompensationStrategy[] strategies = CompensationAction.CompensationStrategy.values();
        
        assertEquals(5, strategies.length, "应该有5种补偿策略");
        
        assertAll("补偿策略枚举验证",
                () -> assertNotNull(CompensationAction.CompensationStrategy.valueOf("IMMEDIATE"), "应该包含IMMEDIATE策略"),
                () -> assertNotNull(CompensationAction.CompensationStrategy.valueOf("DELAYED"), "应该包含DELAYED策略"),
                () -> assertNotNull(CompensationAction.CompensationStrategy.valueOf("CONDITIONAL"), "应该包含CONDITIONAL策略"),
                () -> assertNotNull(CompensationAction.CompensationStrategy.valueOf("MANUAL"), "应该包含MANUAL策略"),
                () -> assertNotNull(CompensationAction.CompensationStrategy.valueOf("IGNORE"), "应该包含IGNORE策略")
        );
        
        // 验证描述信息
        assertEquals("立即补偿", CompensationAction.CompensationStrategy.IMMEDIATE.getDescription());
        assertEquals("延迟补偿", CompensationAction.CompensationStrategy.DELAYED.getDescription());
        assertEquals("条件补偿", CompensationAction.CompensationStrategy.CONDITIONAL.getDescription());
        assertEquals("手动补偿", CompensationAction.CompensationStrategy.MANUAL.getDescription());
        assertEquals("忽略补偿", CompensationAction.CompensationStrategy.IGNORE.getDescription());
    }

    @Test
    @DisplayName("边界条件：默认构造函数应该设置合理默认值")
    void should_SetReasonableDefaults_when_UsingDefaultConstructor() {
        // Given
        CompensationAction action = new CompensationAction();

        // Then
        assertAll("默认值验证",
                () -> assertEquals(0, action.getPriority(), "默认优先级应该为0"),
                () -> assertNotNull(action.getInputParameters(), "参数映射不应该为null"),
                () -> assertTrue(action.getInputParameters().isEmpty(), "参数映射应该为空"),
                () -> assertEquals(3, action.getMaxRetries(), "默认最大重试次数应该为3"),
                () -> assertEquals(1000L, action.getRetryInterval(), "默认重试间隔应该为1000ms"),
                () -> assertFalse(action.isAsync(), "默认应该为同步执行"),
                () -> assertEquals(30000L, action.getTimeout(), "默认超时时间应该为30秒"),
                () -> assertEquals(CompensationAction.CompensationStrategy.IMMEDIATE, action.getStrategy(), "默认策略应该为立即补偿")
        );
    }

    @Test
    @DisplayName("正常场景：Builder模式应该正确工作")
    void should_WorkCorrectly_when_UsingBuilderPattern() {
        // When
        CompensationAction action = CompensationAction.builder()
                .actionName(testActionName)
                .description("Test description")
                .targetService(testTargetService)
                .compensationMethod(testCompensationMethod)
                .priority(1)
                .maxRetries(5)
                .retryInterval(2000L)
                .async(true)
                .timeout(60000L)
                .strategy(CompensationAction.CompensationStrategy.DELAYED)
                .condition("status == 'error'")
                .build();

        // Then
        assertAll("Builder模式验证",
                () -> assertEquals(testActionName, action.getActionName(), "操作名称应该匹配"),
                () -> assertEquals("Test description", action.getDescription(), "描述应该匹配"),
                () -> assertEquals(testTargetService, action.getTargetService(), "目标服务应该匹配"),
                () -> assertEquals(testCompensationMethod, action.getCompensationMethod(), "补偿方法应该匹配"),
                () -> assertEquals(1, action.getPriority(), "优先级应该为1"),
                () -> assertEquals(5, action.getMaxRetries(), "最大重试次数应该为5"),
                () -> assertEquals(2000L, action.getRetryInterval(), "重试间隔应该为2000ms"),
                () -> assertTrue(action.isAsync(), "应该为异步执行"),
                () -> assertEquals(60000L, action.getTimeout(), "超时时间应该为60秒"),
                () -> assertEquals(CompensationAction.CompensationStrategy.DELAYED, action.getStrategy(), "策略应该为延迟补偿"),
                () -> assertEquals("status == 'error'", action.getCondition(), "条件应该匹配"),
                () -> assertTrue(action.isValid(), "配置应该有效")
        );
    }
}