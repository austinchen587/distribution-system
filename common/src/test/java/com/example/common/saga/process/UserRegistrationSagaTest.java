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
 * UserRegistrationSaga 单元测试
 * 
 * 测试用户注册业务流程Saga的核心功能：
 * - 用户注册流程的启动和初始化
 * - 业务上下文的创建和参数设置
 * - 流程步骤的定义和配置
 * - 邀请流程的条件处理
 * - 流程状态查询和监控
 * - 重试机制和执行时间估算
 * - 异常场景的处理和错误回滚
 * 
 * @author Event-Driven Architecture Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserRegistrationSaga 单元测试")
class UserRegistrationSagaTest {

    @Mock
    private SagaCoordinator sagaCoordinator;

    private UserRegistrationSaga userRegistrationSaga;

    // 测试数据
    private final Long testUserId = 12345L;
    private final String testUsername = "testuser";
    private final String testPhone = "13888888888";
    private final String testRole = "SALES";
    private final String testPassword = "password123";
    private final String testInvitationCode = "INV12345";
    private final String testCorrelationId = "CORR-12345";
    private final String testSagaId = "SAGA-USER-REG-001";

    @BeforeEach
    void setUp() {
        userRegistrationSaga = new UserRegistrationSaga();
        ReflectionTestUtils.setField(userRegistrationSaga, "sagaCoordinator", sagaCoordinator);
    }

    @Test
    @DisplayName("正常场景：无邀请码的用户注册流程应该成功启动")
    void should_StartSuccessfully_when_UserRegistrationWithoutInvitation() {
        // Given
        SagaTransaction mockSaga = createMockSagaTransaction();
        when(sagaCoordinator.createSaga(eq("USER_REGISTRATION"), eq(testCorrelationId), 
                eq(testUserId), any(Map.class)))
                .thenReturn(CommonResult.success(mockSaga));
        when(sagaCoordinator.startSaga(testSagaId))
                .thenReturn(CommonResult.success(null));

        // When
        CommonResult<String> result = userRegistrationSaga.startUserRegistration(
                testUserId, testUsername, testPhone, testRole, testPassword, null, testCorrelationId);

        // Then
        assertAll("无邀请码注册流程验证",
                () -> assertTrue(result.getSuccess(), "注册流程应该启动成功"),
                () -> assertEquals(testSagaId, result.getData(), "返回的SagaId应该匹配")
        );

        // 验证协调器调用
        verify(sagaCoordinator, times(1)).createSaga(
                eq("USER_REGISTRATION"), eq(testCorrelationId), eq(testUserId), any(Map.class));
        verify(sagaCoordinator, times(1)).startSaga(testSagaId);
    }

    @Test
    @DisplayName("正常场景：有邀请码的用户注册流程应该成功启动")
    void should_StartSuccessfully_when_UserRegistrationWithInvitation() {
        // Given
        SagaTransaction mockSaga = createMockSagaTransaction();
        when(sagaCoordinator.createSaga(eq("USER_REGISTRATION"), eq(testCorrelationId), 
                eq(testUserId), any(Map.class)))
                .thenReturn(CommonResult.success(mockSaga));
        when(sagaCoordinator.startSaga(testSagaId))
                .thenReturn(CommonResult.success(null));

        // When
        CommonResult<String> result = userRegistrationSaga.startUserRegistration(
                testUserId, testUsername, testPhone, testRole, testPassword, testInvitationCode, testCorrelationId);

        // Then
        assertTrue(result.getSuccess(), "带邀请码的注册流程应该启动成功");
        assertEquals(testSagaId, result.getData(), "返回的SagaId应该匹配");

        // 验证业务上下文包含邀请信息
        verify(sagaCoordinator).createSaga(eq("USER_REGISTRATION"), eq(testCorrelationId), 
                eq(testUserId), argThat(context -> {
                    Map<String, Object> ctx = (Map<String, Object>) context;
                    return testInvitationCode.equals(ctx.get("invitationCode")) &&
                           Boolean.TRUE.equals(ctx.get("hasInvitation"));
                }));
    }

    @Test
    @DisplayName("异常场景：创建Saga失败应该返回错误结果")
    void should_ReturnError_when_CreateSagaFails() {
        // Given
        when(sagaCoordinator.createSaga(anyString(), anyString(), anyLong(), any(Map.class)))
                .thenReturn(CommonResult.error("创建Saga失败"));

        // When
        CommonResult<String> result = userRegistrationSaga.startUserRegistration(
                testUserId, testUsername, testPhone, testRole, testPassword, null, testCorrelationId);

        // Then
        assertAll("创建Saga失败验证",
                () -> assertFalse(result.getSuccess(), "结果应该失败"),
                () -> assertNull(result.getData(), "数据应该为空"),
                () -> assertTrue(result.getMessage().contains("创建用户注册Saga失败"), "错误信息应该包含创建失败")
        );

        verify(sagaCoordinator, never()).startSaga(anyString());
    }

    @Test
    @DisplayName("异常场景：启动Saga失败应该返回错误结果")
    void should_ReturnError_when_StartSagaFails() {
        // Given
        SagaTransaction mockSaga = createMockSagaTransaction();
        when(sagaCoordinator.createSaga(anyString(), anyString(), anyLong(), any(Map.class)))
                .thenReturn(CommonResult.success(mockSaga));
        when(sagaCoordinator.startSaga(testSagaId))
                .thenReturn(CommonResult.error("启动Saga失败"));

        // When
        CommonResult<String> result = userRegistrationSaga.startUserRegistration(
                testUserId, testUsername, testPhone, testRole, testPassword, null, testCorrelationId);

        // Then
        assertFalse(result.getSuccess(), "启动Saga失败时应该返回失败结果");
        assertTrue(result.getMessage().contains("启动用户注册Saga失败"), "错误信息应该包含启动失败");
    }

    @Test
    @DisplayName("异常场景：流程启动时抛出异常应该被捕获")
    void should_CatchException_when_StartUserRegistrationThrowsException() {
        // Given
        when(sagaCoordinator.createSaga(anyString(), anyString(), anyLong(), any(Map.class)))
                .thenThrow(new RuntimeException("网络连接失败"));

        // When
        CommonResult<String> result = userRegistrationSaga.startUserRegistration(
                testUserId, testUsername, testPhone, testRole, testPassword, null, testCorrelationId);

        // Then
        assertAll("异常捕获验证",
                () -> assertFalse(result.getSuccess(), "应该返回失败结果"),
                () -> assertNull(result.getData(), "数据应该为空"),
                () -> assertTrue(result.getMessage().contains("启动用户注册流程失败"), "错误信息应该包含启动失败"),
                () -> assertTrue(result.getMessage().contains("网络连接失败"), "错误信息应该包含具体异常")
        );
    }

    @Test
    @DisplayName("正常场景：查询用户注册流程状态应该返回完整信息")
    void should_ReturnCompleteStatus_when_GetUserRegistrationStatus() {
        // Given
        SagaTransaction mockSaga = createMockSagaTransactionWithStatus();
        when(sagaCoordinator.getSaga(testSagaId)).thenReturn(mockSaga);

        // When
        CommonResult<Map<String, Object>> result = userRegistrationSaga.getUserRegistrationStatus(testSagaId);

        // Then
        assertTrue(result.getSuccess(), "查询状态应该成功");
        Map<String, Object> status = result.getData();
        
        assertAll("状态信息验证",
                () -> assertEquals(testSagaId, status.get("sagaId"), "SagaId应该匹配"),
                () -> assertEquals("USER_REGISTRATION", status.get("sagaType"), "Saga类型应该匹配"),
                () -> assertEquals("RUNNING", status.get("status"), "状态应该匹配"),
                () -> assertNotNull(status.get("statusDescription"), "状态描述不应该为空"),
                () -> assertEquals(1, status.get("currentStepIndex"), "当前步骤索引应该匹配"),
                () -> assertEquals(4, status.get("totalSteps"), "总步骤数应该匹配"),
                () -> assertEquals(testUserId, status.get("userId"), "用户ID应该匹配"),
                () -> assertEquals(testUsername, status.get("username"), "用户名应该匹配")
        );
    }

    @Test
    @DisplayName("异常场景：查询不存在的Saga状态应该返回错误")
    void should_ReturnError_when_GetStatusOfNonExistentSaga() {
        // Given
        when(sagaCoordinator.getSaga(testSagaId)).thenReturn(null);

        // When
        CommonResult<Map<String, Object>> result = userRegistrationSaga.getUserRegistrationStatus(testSagaId);

        // Then
        assertFalse(result.getSuccess(), "查询不存在Saga应该返回失败结果");
        assertTrue(result.getMessage().contains("用户注册流程不存在"), "错误信息应该包含不存在提示");
    }

    @Test
    @DisplayName("异常场景：查询状态时抛出异常应该被捕获")
    void should_CatchException_when_GetStatusThrowsException() {
        // Given
        when(sagaCoordinator.getSaga(testSagaId)).thenThrow(new RuntimeException("数据库连接失败"));

        // When
        CommonResult<Map<String, Object>> result = userRegistrationSaga.getUserRegistrationStatus(testSagaId);

        // Then
        assertFalse(result.getSuccess(), "异常时应该返回失败结果");
        assertTrue(result.getMessage().contains("查询流程状态失败"), "错误信息应该包含查询失败");
    }

    @Test
    @DisplayName("正常场景：获取Saga类型应该返回正确值")
    void should_ReturnCorrectType_when_GetSagaType() {
        // When
        String sagaType = userRegistrationSaga.getSagaType();

        // Then
        assertEquals("USER_REGISTRATION", sagaType, "Saga类型应该为USER_REGISTRATION");
    }

    @Test
    @DisplayName("正常场景：检查可重试步骤应该返回正确结果")
    void should_ReturnTrue_when_CanRetryFailedStep() {
        // Given
        SagaTransaction mockSaga = createMockSagaWithFailedStep();
        when(sagaCoordinator.getSaga(testSagaId)).thenReturn(mockSaga);

        // When
        boolean canRetry = userRegistrationSaga.canRetryStep(testSagaId, "CREATE_USER_ACCOUNT");

        // Then
        assertTrue(canRetry, "失败的步骤应该可以重试");
    }

    @Test
    @DisplayName("边界条件：检查不可重试步骤应该返回false")
    void should_ReturnFalse_when_CannotRetryStep() {
        // Given
        SagaTransaction mockSaga = createMockSagaWithSuccessStep();
        when(sagaCoordinator.getSaga(testSagaId)).thenReturn(mockSaga);

        // When
        boolean canRetry = userRegistrationSaga.canRetryStep(testSagaId, "CREATE_USER_ACCOUNT");

        // Then
        assertFalse(canRetry, "成功的步骤不应该可以重试");
    }

    @Test
    @DisplayName("边界条件：检查不存在Saga的重试能力应该返回false")
    void should_ReturnFalse_when_CheckRetryForNonExistentSaga() {
        // Given
        when(sagaCoordinator.getSaga(testSagaId)).thenReturn(null);

        // When
        boolean canRetry = userRegistrationSaga.canRetryStep(testSagaId, "CREATE_USER_ACCOUNT");

        // Then
        assertFalse(canRetry, "不存在的Saga不应该可以重试");
    }

    @Test
    @DisplayName("正常场景：无邀请码的执行时间估算应该正确")
    void should_EstimateCorrectTime_when_WithoutInvitation() {
        // When
        int estimatedTime = userRegistrationSaga.estimateExecutionTime(false);

        // Then
        assertEquals(70, estimatedTime, "无邀请码流程的估算时间应该为70秒");
    }

    @Test
    @DisplayName("正常场景：有邀请码的执行时间估算应该正确")
    void should_EstimateCorrectTime_when_WithInvitation() {
        // When
        int estimatedTime = userRegistrationSaga.estimateExecutionTime(true);

        // Then
        assertEquals(90, estimatedTime, "有邀请码流程的估算时间应该为90秒");
    }

    @Test
    @DisplayName("正常场景：业务上下文创建应该包含所有必要信息")
    void should_CreateCompleteContext_when_CreatingBusinessContext() {
        // Given
        SagaTransaction mockSaga = createMockSagaTransaction();
        when(sagaCoordinator.createSaga(anyString(), anyString(), anyLong(), any(Map.class)))
                .thenReturn(CommonResult.success(mockSaga));
        when(sagaCoordinator.startSaga(anyString())).thenReturn(CommonResult.success(null));

        // When
        userRegistrationSaga.startUserRegistration(
                testUserId, testUsername, testPhone, testRole, testPassword, testInvitationCode, testCorrelationId);

        // Then
        verify(sagaCoordinator).createSaga(eq("USER_REGISTRATION"), eq(testCorrelationId), 
                eq(testUserId), argThat(context -> {
                    Map<String, Object> ctx = (Map<String, Object>) context;
                    return testUserId.equals(ctx.get("userId")) &&
                           testUsername.equals(ctx.get("username")) &&
                           testPhone.equals(ctx.get("phone")) &&
                           testRole.equals(ctx.get("role")) &&
                           testPassword.equals(ctx.get("password")) &&
                           testInvitationCode.equals(ctx.get("invitationCode")) &&
                           Boolean.TRUE.equals(ctx.get("hasInvitation")) &&
                           ctx.containsKey("registrationTime");
                }));
    }

    @Test
    @DisplayName("边界条件：流程步骤定义应该根据邀请情况调整")
    void should_AdjustSteps_when_DefiningStepsBasedOnInvitation() {
        // Given
        SagaTransaction mockSagaWithInvitation = createMockSagaTransaction();
        SagaTransaction mockSagaWithoutInvitation = createMockSagaTransaction();
        
        when(sagaCoordinator.createSaga(anyString(), anyString(), anyLong(), any(Map.class)))
                .thenReturn(CommonResult.success(mockSagaWithInvitation))
                .thenReturn(CommonResult.success(mockSagaWithoutInvitation));
        when(sagaCoordinator.startSaga(anyString())).thenReturn(CommonResult.success(null));

        // When - 有邀请码的情况
        userRegistrationSaga.startUserRegistration(
                testUserId, testUsername, testPhone, testRole, testPassword, testInvitationCode, testCorrelationId);

        // When - 无邀请码的情况
        userRegistrationSaga.startUserRegistration(
                testUserId, testUsername, testPhone, testRole, testPassword, null, testCorrelationId);

        // Then
        verify(mockSagaWithInvitation, atLeast(4)).addStep(any(SagaStep.class)); // 包含邀请关系步骤
        verify(mockSagaWithoutInvitation, atLeast(4)).addStep(any(SagaStep.class)); // 不包含邀请关系步骤
    }

    @Test
    @DisplayName("异常场景：检查重试时抛出异常应该返回false")
    void should_ReturnFalse_when_CanRetryThrowsException() {
        // Given
        when(sagaCoordinator.getSaga(testSagaId)).thenThrow(new RuntimeException("数据访问异常"));

        // When
        boolean canRetry = userRegistrationSaga.canRetryStep(testSagaId, "CREATE_USER_ACCOUNT");

        // Then
        assertFalse(canRetry, "异常时应该返回false");
    }

    // Helper methods for creating mock objects
    private SagaTransaction createMockSagaTransaction() {
        SagaTransaction mockSaga = mock(SagaTransaction.class);
        lenient().when(mockSaga.getSagaId()).thenReturn(testSagaId);
        lenient().when(mockSaga.getSagaType()).thenReturn("USER_REGISTRATION");
        lenient().when(mockSaga.getSteps()).thenReturn(new ArrayList<>());
        lenient().when(mockSaga.getContextData("userId")).thenReturn(testUserId);
        lenient().when(mockSaga.getContextData("username")).thenReturn(testUsername);
        lenient().when(mockSaga.getContextData("phone")).thenReturn(testPhone);
        lenient().when(mockSaga.getContextData("role")).thenReturn(testRole);
        lenient().when(mockSaga.getContextData("password")).thenReturn(testPassword);
        lenient().when(mockSaga.getContextData("invitationCode")).thenReturn(testInvitationCode);
        lenient().when(mockSaga.getContextData("registrationTime")).thenReturn(System.currentTimeMillis());
        return mockSaga;
    }

    private SagaTransaction createMockSagaTransactionWithStatus() {
        SagaTransaction mockSaga = mock(SagaTransaction.class);
        lenient().when(mockSaga.getSagaId()).thenReturn(testSagaId);
        lenient().when(mockSaga.getSagaType()).thenReturn("USER_REGISTRATION");
        lenient().when(mockSaga.getStatus()).thenReturn(SagaTransaction.SagaStatus.RUNNING);
        lenient().when(mockSaga.getCurrentStepIndex()).thenReturn(1);
        
        List<SagaStep> steps = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            steps.add(mock(SagaStep.class));
        }
        lenient().when(mockSaga.getSteps()).thenReturn(steps);
        
        lenient().when(mockSaga.getCreatedAt()).thenReturn(java.time.LocalDateTime.now().minusSeconds(10));
        lenient().when(mockSaga.getStartedAt()).thenReturn(java.time.LocalDateTime.now().minusSeconds(5));
        lenient().when(mockSaga.getCompletedAt()).thenReturn(null);
        
        SagaStep currentStep = mock(SagaStep.class);
        lenient().when(currentStep.getStepName()).thenReturn("CREATE_USER_ACCOUNT");
        lenient().when(currentStep.getStatus()).thenReturn(SagaStep.StepStatus.RUNNING);
        lenient().when(mockSaga.getCurrentStep()).thenReturn(currentStep);
        
        Map<String, Object> businessContext = new HashMap<>();
        businessContext.put("userId", testUserId);
        businessContext.put("username", testUsername);
        lenient().when(mockSaga.getBusinessContext()).thenReturn(businessContext);
        
        return mockSaga;
    }

    private SagaTransaction createMockSagaWithFailedStep() {
        SagaTransaction mockSaga = mock(SagaTransaction.class);
        lenient().when(mockSaga.getSagaId()).thenReturn(testSagaId);
        
        SagaStep failedStep = mock(SagaStep.class);
        lenient().when(failedStep.getStepName()).thenReturn("CREATE_USER_ACCOUNT");
        lenient().when(failedStep.getStatus()).thenReturn(SagaStep.StepStatus.FAILED);
        lenient().when(failedStep.canRetry()).thenReturn(true);
        lenient().when(mockSaga.getCurrentStep()).thenReturn(failedStep);
        
        return mockSaga;
    }

    private SagaTransaction createMockSagaWithSuccessStep() {
        SagaTransaction mockSaga = mock(SagaTransaction.class);
        lenient().when(mockSaga.getSagaId()).thenReturn(testSagaId);
        
        SagaStep successStep = mock(SagaStep.class);
        lenient().when(successStep.getStepName()).thenReturn("CREATE_USER_ACCOUNT");
        lenient().when(successStep.getStatus()).thenReturn(SagaStep.StepStatus.COMPLETED);
        lenient().when(successStep.canRetry()).thenReturn(false);
        lenient().when(mockSaga.getCurrentStep()).thenReturn(successStep);
        
        return mockSaga;
    }
}