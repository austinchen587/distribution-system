package com.example.common.saga.process;

import com.example.common.dto.CommonResult;
import com.example.common.saga.SagaCoordinator;
import com.example.common.saga.SagaStep;
import com.example.common.saga.SagaTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * LeadProcessingSaga 单元测试
 * 
 * 测试客资处理业务流程Saga的核心功能：
 * - 客资处理流程的启动和初始化
 * - 业务上下文的创建和参数设置
 * - 流程步骤的定义和配置（6步流程）
 * - 优先级计算逻辑
 * - 客资审核子流程处理
 * - 流程状态查询和监控
 * - 多种客资来源的处理
 * - 异常场景的处理和错误回滚
 * 
 * @author Event-Driven Architecture Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LeadProcessingSaga 单元测试")
class LeadProcessingSagaTest {

    @Mock
    private SagaCoordinator sagaCoordinator;

    private LeadProcessingSaga leadProcessingSaga;

    // 测试数据
    private final Long testLeadId = 98765L;
    private final String testCustomerName = "张三";
    private final String testCustomerPhone = "13999999999";
    private final String testCustomerWechat = "zhangsan_wx";
    private final Long testSubmitterId = 12345L;
    private final String testSource = "DIRECT_REFERRAL";
    private final String testRemarks = "高价值客户，优先跟进";
    private final String testCorrelationId = "LEAD-CORR-98765";
    private final String testSagaId = "SAGA-LEAD-PROC-001";

    @BeforeEach
    void setUp() {
        leadProcessingSaga = new LeadProcessingSaga();
        ReflectionTestUtils.setField(leadProcessingSaga, "sagaCoordinator", sagaCoordinator);
    }

    @Test
    @DisplayName("正常场景：客资处理流程应该成功启动")
    void should_StartSuccessfully_when_LeadProcessingWithAllParameters() {
        // Given
        SagaTransaction mockSaga = createMockLeadProcessingSaga();
        when(sagaCoordinator.createSaga(eq("LEAD_PROCESSING"), eq(testCorrelationId), 
                eq(testSubmitterId), any(Map.class)))
                .thenReturn(CommonResult.success(mockSaga));
        when(sagaCoordinator.startSaga(testSagaId))
                .thenReturn(CommonResult.success(null));

        // When
        CommonResult<String> result = leadProcessingSaga.startLeadProcessing(
                testLeadId, testCustomerName, testCustomerPhone, testCustomerWechat, 
                testSubmitterId, testSource, testRemarks, testCorrelationId);

        // Then
        assertAll("客资处理流程启动验证",
                () -> assertTrue(result.getSuccess(), "客资处理流程应该启动成功"),
                () -> assertEquals(testSagaId, result.getData(), "返回的SagaId应该匹配")
        );

        // 验证协调器调用
        verify(sagaCoordinator, times(1)).createSaga(
                eq("LEAD_PROCESSING"), eq(testCorrelationId), eq(testSubmitterId), any(Map.class));
        verify(sagaCoordinator, times(1)).startSaga(testSagaId);
    }

    @Test
    @DisplayName("正常场景：不同来源的客资应该计算正确的优先级")
    void should_CalculateCorrectPriority_when_DifferentLeadSources() {
        // Given
        SagaTransaction mockSaga = createMockLeadProcessingSaga();
        when(sagaCoordinator.createSaga(anyString(), anyString(), anyLong(), any(Map.class)))
                .thenReturn(CommonResult.success(mockSaga));
        when(sagaCoordinator.startSaga(anyString())).thenReturn(CommonResult.success(null));

        // Test DIRECT_REFERRAL source
        leadProcessingSaga.startLeadProcessing(
                testLeadId, testCustomerName, testCustomerPhone, testCustomerWechat,
                testSubmitterId, "DIRECT_REFERRAL", testRemarks, testCorrelationId);

        // Then - 验证直接推荐优先级最高（1）
        verify(sagaCoordinator).createSaga(eq("LEAD_PROCESSING"), eq(testCorrelationId), 
                eq(testSubmitterId), argThat(context -> {
                    Map<String, Object> ctx = (Map<String, Object>) context;
                    return Integer.valueOf(1).equals(ctx.get("priority"));
                }));

        reset(sagaCoordinator);
        when(sagaCoordinator.createSaga(anyString(), anyString(), anyLong(), any(Map.class)))
                .thenReturn(CommonResult.success(mockSaga));
        when(sagaCoordinator.startSaga(anyString())).thenReturn(CommonResult.success(null));

        // Test PARTNER_CHANNEL source
        leadProcessingSaga.startLeadProcessing(
                testLeadId, testCustomerName, testCustomerPhone, testCustomerWechat,
                testSubmitterId, "PARTNER_CHANNEL", testRemarks, testCorrelationId);

        // Then - 验证合作渠道优先级较高（2）
        verify(sagaCoordinator).createSaga(eq("LEAD_PROCESSING"), eq(testCorrelationId), 
                eq(testSubmitterId), argThat(context -> {
                    Map<String, Object> ctx = (Map<String, Object>) context;
                    return Integer.valueOf(2).equals(ctx.get("priority"));
                }));
    }

    @Test
    @DisplayName("正常场景：业务上下文创建应该包含所有必要信息")
    void should_CreateCompleteContext_when_CreatingLeadProcessingContext() {
        // Given
        SagaTransaction mockSaga = createMockLeadProcessingSaga();
        when(sagaCoordinator.createSaga(anyString(), anyString(), anyLong(), any(Map.class)))
                .thenReturn(CommonResult.success(mockSaga));
        when(sagaCoordinator.startSaga(anyString())).thenReturn(CommonResult.success(null));

        // When
        leadProcessingSaga.startLeadProcessing(
                testLeadId, testCustomerName, testCustomerPhone, testCustomerWechat,
                testSubmitterId, testSource, testRemarks, testCorrelationId);

        // Then
        verify(sagaCoordinator).createSaga(eq("LEAD_PROCESSING"), eq(testCorrelationId), 
                eq(testSubmitterId), argThat(context -> {
                    Map<String, Object> ctx = (Map<String, Object>) context;
                    return testLeadId.equals(ctx.get("leadId")) &&
                           testCustomerName.equals(ctx.get("customerName")) &&
                           testCustomerPhone.equals(ctx.get("customerPhone")) &&
                           testCustomerWechat.equals(ctx.get("customerWechat")) &&
                           testSubmitterId.equals(ctx.get("submitterId")) &&
                           testSource.equals(ctx.get("source")) &&
                           testRemarks.equals(ctx.get("remarks")) &&
                           ctx.containsKey("submissionTime") &&
                           ctx.containsKey("priority");
                }));
    }

    @Test
    @DisplayName("异常场景：创建Saga失败应该返回错误结果")
    void should_ReturnError_when_CreateSagaFails() {
        // Given
        when(sagaCoordinator.createSaga(anyString(), anyString(), anyLong(), any(Map.class)))
                .thenReturn(CommonResult.error("创建Saga失败"));

        // When
        CommonResult<String> result = leadProcessingSaga.startLeadProcessing(
                testLeadId, testCustomerName, testCustomerPhone, testCustomerWechat,
                testSubmitterId, testSource, testRemarks, testCorrelationId);

        // Then
        assertAll("创建Saga失败验证",
                () -> assertFalse(result.getSuccess(), "结果应该失败"),
                () -> assertNull(result.getData(), "数据应该为空"),
                () -> assertTrue(result.getMessage().contains("创建客资处理Saga失败"), "错误信息应该包含创建失败")
        );

        verify(sagaCoordinator, never()).startSaga(anyString());
    }

    @Test
    @DisplayName("异常场景：启动Saga失败应该返回错误结果")
    void should_ReturnError_when_StartSagaFails() {
        // Given
        SagaTransaction mockSaga = createMockLeadProcessingSaga();
        when(sagaCoordinator.createSaga(anyString(), anyString(), anyLong(), any(Map.class)))
                .thenReturn(CommonResult.success(mockSaga));
        when(sagaCoordinator.startSaga(testSagaId))
                .thenReturn(CommonResult.error("启动Saga失败"));

        // When
        CommonResult<String> result = leadProcessingSaga.startLeadProcessing(
                testLeadId, testCustomerName, testCustomerPhone, testCustomerWechat,
                testSubmitterId, testSource, testRemarks, testCorrelationId);

        // Then
        assertFalse(result.getSuccess(), "启动Saga失败时应该返回失败结果");
        assertTrue(result.getMessage().contains("启动客资处理Saga失败"), "错误信息应该包含启动失败");
    }

    @Test
    @DisplayName("异常场景：流程启动时抛出异常应该被捕获")
    void should_CatchException_when_StartLeadProcessingThrowsException() {
        // Given
        when(sagaCoordinator.createSaga(anyString(), anyString(), anyLong(), any(Map.class)))
                .thenThrow(new RuntimeException("数据库连接失败"));

        // When
        CommonResult<String> result = leadProcessingSaga.startLeadProcessing(
                testLeadId, testCustomerName, testCustomerPhone, testCustomerWechat,
                testSubmitterId, testSource, testRemarks, testCorrelationId);

        // Then
        assertAll("异常捕获验证",
                () -> assertFalse(result.getSuccess(), "应该返回失败结果"),
                () -> assertNull(result.getData(), "数据应该为空"),
                () -> assertTrue(result.getMessage().contains("启动客资处理流程失败"), "错误信息应该包含启动失败"),
                () -> assertTrue(result.getMessage().contains("数据库连接失败"), "错误信息应该包含具体异常")
        );
    }

    @Test
    @DisplayName("正常场景：客资审核流程应该成功启动")
    void should_StartApprovalSuccessfully_when_LeadApprovalFlow() {
        // Given
        Long reviewerId = 67890L;
        String approvalCorrelationId = "APPROVAL-CORR-98765";
        SagaTransaction mockApprovalSaga = createMockApprovalSaga();
        
        when(sagaCoordinator.createSaga(eq("LEAD_APPROVAL"), eq(approvalCorrelationId), 
                eq(reviewerId), any(Map.class)))
                .thenReturn(CommonResult.success(mockApprovalSaga));
        when(sagaCoordinator.startSaga(mockApprovalSaga.getSagaId()))
                .thenReturn(CommonResult.success(null));

        // When
        CommonResult<String> result = leadProcessingSaga.startLeadApproval(
                testLeadId, reviewerId, approvalCorrelationId);

        // Then
        assertAll("客资审核流程启动验证",
                () -> assertTrue(result.getSuccess(), "客资审核流程应该启动成功"),
                () -> assertEquals("SAGA-APPROVAL-001", result.getData(), "返回的SagaId应该匹配")
        );

        // 验证审核上下文参数
        verify(sagaCoordinator).createSaga(eq("LEAD_APPROVAL"), eq(approvalCorrelationId), 
                eq(reviewerId), argThat(context -> {
                    Map<String, Object> ctx = (Map<String, Object>) context;
                    return testLeadId.equals(ctx.get("leadId")) &&
                           reviewerId.equals(ctx.get("reviewerId")) &&
                           "LEAD_REVIEW".equals(ctx.get("approvalType")) &&
                           ctx.containsKey("reviewStartTime");
                }));
    }

    @Test
    @DisplayName("异常场景：客资审核流程启动失败应该返回错误")
    void should_ReturnError_when_LeadApprovalStartFails() {
        // Given
        Long reviewerId = 67890L;
        when(sagaCoordinator.createSaga(eq("LEAD_APPROVAL"), anyString(), eq(reviewerId), any(Map.class)))
                .thenReturn(CommonResult.error("创建审核Saga失败"));

        // When
        CommonResult<String> result = leadProcessingSaga.startLeadApproval(
                testLeadId, reviewerId, "correlation-id");

        // Then
        assertFalse(result.getSuccess(), "审核流程启动失败时应该返回失败结果");
        assertTrue(result.getMessage().contains("创建客资审核Saga失败"), "错误信息应该包含创建失败");
    }

    @Test
    @DisplayName("正常场景：查询客资处理流程状态应该返回完整信息")
    void should_ReturnCompleteStatus_when_GetLeadProcessingStatus() {
        // Given
        SagaTransaction mockSaga = createMockLeadProcessingSagaWithStatus();
        when(sagaCoordinator.getSaga(testSagaId)).thenReturn(mockSaga);

        // When
        CommonResult<Map<String, Object>> result = leadProcessingSaga.getLeadProcessingStatus(testSagaId);

        // Then
        assertTrue(result.getSuccess(), "查询状态应该成功");
        Map<String, Object> status = result.getData();
        
        assertAll("状态信息验证",
                () -> assertEquals(testSagaId, status.get("sagaId"), "SagaId应该匹配"),
                () -> assertEquals("LEAD_PROCESSING", status.get("sagaType"), "Saga类型应该匹配"),
                () -> assertEquals("RUNNING", status.get("status"), "状态应该匹配"),
                () -> assertNotNull(status.get("statusDescription"), "状态描述不应该为空"),
                () -> assertEquals(2, status.get("currentStepIndex"), "当前步骤索引应该匹配"),
                () -> assertEquals(6, status.get("totalSteps"), "总步骤数应该匹配"),
                () -> assertEquals(33.33, (Double) status.get("progress"), 0.01, "进度应该匹配"),
                () -> assertEquals(testLeadId, status.get("leadId"), "客资ID应该匹配"),
                () -> assertEquals(testCustomerName, status.get("customerName"), "客户名应该匹配")
        );
    }

    @Test
    @DisplayName("异常场景：查询不存在的Saga状态应该返回错误")
    void should_ReturnError_when_GetStatusOfNonExistentSaga() {
        // Given
        when(sagaCoordinator.getSaga(testSagaId)).thenReturn(null);

        // When
        CommonResult<Map<String, Object>> result = leadProcessingSaga.getLeadProcessingStatus(testSagaId);

        // Then
        assertFalse(result.getSuccess(), "查询不存在Saga应该返回失败结果");
        assertTrue(result.getMessage().contains("客资处理流程不存在"), "错误信息应该包含不存在提示");
    }

    @Test
    @DisplayName("异常场景：查询状态时抛出异常应该被捕获")
    void should_CatchException_when_GetStatusThrowsException() {
        // Given
        when(sagaCoordinator.getSaga(testSagaId)).thenThrow(new RuntimeException("系统异常"));

        // When
        CommonResult<Map<String, Object>> result = leadProcessingSaga.getLeadProcessingStatus(testSagaId);

        // Then
        assertFalse(result.getSuccess(), "异常时应该返回失败结果");
        assertTrue(result.getMessage().contains("查询流程状态失败"), "错误信息应该包含查询失败");
    }

    @Test
    @DisplayName("正常场景：获取支持的Saga类型应该返回正确列表")
    void should_ReturnCorrectTypes_when_GetSagaTypes() {
        // When
        String[] sagaTypes = leadProcessingSaga.getSagaTypes();

        // Then
        assertAll("Saga类型列表验证",
                () -> assertNotNull(sagaTypes, "类型列表不应该为null"),
                () -> assertEquals(2, sagaTypes.length, "应该支持2种Saga类型"),
                () -> assertEquals("LEAD_PROCESSING", sagaTypes[0], "第一个类型应该为LEAD_PROCESSING"),
                () -> assertEquals("LEAD_APPROVAL", sagaTypes[1], "第二个类型应该为LEAD_APPROVAL")
        );
    }

    @Test
    @DisplayName("边界条件：流程步骤定义应该包含6个步骤")
    void should_Define6Steps_when_DefiningLeadProcessingSteps() {
        // Given
        SagaTransaction mockSaga = createMockLeadProcessingSaga();
        when(sagaCoordinator.createSaga(anyString(), anyString(), anyLong(), any(Map.class)))
                .thenReturn(CommonResult.success(mockSaga));
        when(sagaCoordinator.startSaga(anyString())).thenReturn(CommonResult.success(null));

        // When
        leadProcessingSaga.startLeadProcessing(
                testLeadId, testCustomerName, testCustomerPhone, testCustomerWechat,
                testSubmitterId, testSource, testRemarks, testCorrelationId);

        // Then - 验证定义了6个步骤
        verify(mockSaga, times(6)).addStep(any(SagaStep.class));
    }

    @Test
    @DisplayName("边界条件：审核流程步骤定义应该包含3个步骤")
    void should_Define3Steps_when_DefiningLeadApprovalSteps() {
        // Given
        SagaTransaction mockApprovalSaga = createMockApprovalSaga();
        when(sagaCoordinator.createSaga(eq("LEAD_APPROVAL"), anyString(), anyLong(), any(Map.class)))
                .thenReturn(CommonResult.success(mockApprovalSaga));
        when(sagaCoordinator.startSaga(anyString())).thenReturn(CommonResult.success(null));

        // When
        leadProcessingSaga.startLeadApproval(testLeadId, 67890L, "correlation-id");

        // Then - 验证定义了3个审核步骤
        verify(mockApprovalSaga, times(3)).addStep(any(SagaStep.class));
    }

    @Test
    @DisplayName("边界条件：优先级计算默认值应该正确")
    void should_CalculateDefaultPriority_when_UnknownSource() {
        // Given
        SagaTransaction mockSaga = createMockLeadProcessingSaga();
        when(sagaCoordinator.createSaga(anyString(), anyString(), anyLong(), any(Map.class)))
                .thenReturn(CommonResult.success(mockSaga));
        when(sagaCoordinator.startSaga(anyString())).thenReturn(CommonResult.success(null));

        // When - 使用未知来源
        leadProcessingSaga.startLeadProcessing(
                testLeadId, testCustomerName, testCustomerPhone, testCustomerWechat,
                testSubmitterId, "UNKNOWN_SOURCE", testRemarks, testCorrelationId);

        // Then - 验证默认优先级（3）
        verify(sagaCoordinator).createSaga(eq("LEAD_PROCESSING"), eq(testCorrelationId), 
                eq(testSubmitterId), argThat(context -> {
                    Map<String, Object> ctx = (Map<String, Object>) context;
                    return Integer.valueOf(3).equals(ctx.get("priority"));
                }));
    }

    @Test
    @DisplayName("正常场景：在线表单来源应该计算较低优先级")
    void should_CalculateLowerPriority_when_OnlineFormSource() {
        // Given
        SagaTransaction mockSaga = createMockLeadProcessingSaga();
        when(sagaCoordinator.createSaga(anyString(), anyString(), anyLong(), any(Map.class)))
                .thenReturn(CommonResult.success(mockSaga));
        when(sagaCoordinator.startSaga(anyString())).thenReturn(CommonResult.success(null));

        // When
        leadProcessingSaga.startLeadProcessing(
                testLeadId, testCustomerName, testCustomerPhone, testCustomerWechat,
                testSubmitterId, "ONLINE_FORM", testRemarks, testCorrelationId);

        // Then - 验证在线表单优先级较低（4）
        verify(sagaCoordinator).createSaga(eq("LEAD_PROCESSING"), eq(testCorrelationId), 
                eq(testSubmitterId), argThat(context -> {
                    Map<String, Object> ctx = (Map<String, Object>) context;
                    return Integer.valueOf(4).equals(ctx.get("priority"));
                }));
    }

    @Test
    @DisplayName("边界条件：客资处理流程步骤超时配置应该合理")
    void should_ConfigureReasonableTimeouts_when_DefiningSteps() {
        // Given
        SagaTransaction mockSaga = createMockLeadProcessingSaga();
        when(sagaCoordinator.createSaga(anyString(), anyString(), anyLong(), any(Map.class)))
                .thenReturn(CommonResult.success(mockSaga));
        when(sagaCoordinator.startSaga(anyString())).thenReturn(CommonResult.success(null));

        // When
        leadProcessingSaga.startLeadProcessing(
                testLeadId, testCustomerName, testCustomerPhone, testCustomerWechat,
                testSubmitterId, testSource, testRemarks, testCorrelationId);

        // Then - 验证步骤被添加（间接验证超时配置）
        verify(mockSaga, times(6)).addStep(argThat(step -> {
            // 验证所有步骤都设置了合理的超时时间（大于0且小于60秒）
            return step.getTimeoutMillis() > 0 && step.getTimeoutMillis() <= 60000L;
        }));
    }

    @Test
    @DisplayName("异常场景：客资审核流程抛出异常应该被捕获")
    void should_CatchException_when_StartLeadApprovalThrowsException() {
        // Given
        when(sagaCoordinator.createSaga(eq("LEAD_APPROVAL"), anyString(), anyLong(), any(Map.class)))
                .thenThrow(new RuntimeException("系统故障"));

        // When
        CommonResult<String> result = leadProcessingSaga.startLeadApproval(
                testLeadId, 67890L, "correlation-id");

        // Then
        assertAll("审核流程异常捕获验证",
                () -> assertFalse(result.getSuccess(), "应该返回失败结果"),
                () -> assertNull(result.getData(), "数据应该为空"),
                () -> assertTrue(result.getMessage().contains("启动客资审核流程失败"), "错误信息应该包含启动失败"),
                () -> assertTrue(result.getMessage().contains("系统故障"), "错误信息应该包含具体异常")
        );
    }

    // Helper methods for creating mock objects
    private SagaTransaction createMockLeadProcessingSaga() {
        SagaTransaction mockSaga = mock(SagaTransaction.class);
        lenient().when(mockSaga.getSagaId()).thenReturn(testSagaId);
        lenient().when(mockSaga.getSagaType()).thenReturn("LEAD_PROCESSING");
        lenient().when(mockSaga.getSteps()).thenReturn(new ArrayList<>());
        lenient().when(mockSaga.getContextData("leadId")).thenReturn(testLeadId);
        lenient().when(mockSaga.getContextData("customerName")).thenReturn(testCustomerName);
        lenient().when(mockSaga.getContextData("customerPhone")).thenReturn(testCustomerPhone);
        lenient().when(mockSaga.getContextData("customerWechat")).thenReturn(testCustomerWechat);
        lenient().when(mockSaga.getContextData("submitterId")).thenReturn(testSubmitterId);
        lenient().when(mockSaga.getContextData("source")).thenReturn(testSource);
        lenient().when(mockSaga.getContextData("remarks")).thenReturn(testRemarks);
        lenient().when(mockSaga.getContextData("submissionTime")).thenReturn(System.currentTimeMillis());
        lenient().when(mockSaga.getContextData("priority")).thenReturn(1); // DIRECT_REFERRAL优先级
        return mockSaga;
    }

    private SagaTransaction createMockApprovalSaga() {
        SagaTransaction mockSaga = mock(SagaTransaction.class);
        lenient().when(mockSaga.getSagaId()).thenReturn("SAGA-APPROVAL-001");
        lenient().when(mockSaga.getSagaType()).thenReturn("LEAD_APPROVAL");
        lenient().when(mockSaga.getSteps()).thenReturn(new ArrayList<>());
        lenient().when(mockSaga.getContextData("leadId")).thenReturn(testLeadId);
        lenient().when(mockSaga.getContextData("reviewerId")).thenReturn(67890L);
        lenient().when(mockSaga.getContextData("approvalType")).thenReturn("LEAD_REVIEW");
        lenient().when(mockSaga.getContextData("reviewStartTime")).thenReturn(System.currentTimeMillis());
        return mockSaga;
    }

    private SagaTransaction createMockLeadProcessingSagaWithStatus() {
        SagaTransaction mockSaga = mock(SagaTransaction.class);
        lenient().when(mockSaga.getSagaId()).thenReturn(testSagaId);
        lenient().when(mockSaga.getSagaType()).thenReturn("LEAD_PROCESSING");
        lenient().when(mockSaga.getStatus()).thenReturn(SagaTransaction.SagaStatus.RUNNING);
        lenient().when(mockSaga.getCurrentStepIndex()).thenReturn(2);
        
        List<SagaStep> steps = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            steps.add(mock(SagaStep.class));
        }
        lenient().when(mockSaga.getSteps()).thenReturn(steps);
        
        lenient().when(mockSaga.getCreatedAt()).thenReturn(java.time.LocalDateTime.now().minusSeconds(20));
        lenient().when(mockSaga.getStartedAt()).thenReturn(java.time.LocalDateTime.now().minusSeconds(15));
        lenient().when(mockSaga.getCompletedAt()).thenReturn(null);
        
        SagaStep currentStep = mock(SagaStep.class);
        lenient().when(currentStep.getStepName()).thenReturn("LEAD_ASSIGNMENT");
        lenient().when(currentStep.getStatus()).thenReturn(SagaStep.StepStatus.RUNNING);
        lenient().when(mockSaga.getCurrentStep()).thenReturn(currentStep);
        
        Map<String, Object> businessContext = new HashMap<>();
        businessContext.put("leadId", testLeadId);
        businessContext.put("customerName", testCustomerName);
        businessContext.put("customerPhone", testCustomerPhone);
        businessContext.put("submitterId", testSubmitterId);
        lenient().when(mockSaga.getBusinessContext()).thenReturn(businessContext);
        
        return mockSaga;
    }
}