package com.example.common.saga.process;

import com.example.common.dto.CommonResult;
import com.example.common.saga.SagaCoordinator;
import com.example.common.saga.SagaStep;
import com.example.common.saga.SagaTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户注册业务流程Saga
 * 
 * <p>处理用户注册的完整业务流程，包括多个相关服务的协调操作。
 * 确保用户注册的所有相关数据和状态的最终一致性。
 * 
 * <p>业务流程步骤：
 * <ol>
 *   <li>创建用户账户（auth-service）</li>
 *   <li>生成用户邀请码（invitation-service）</li>
 *   <li>建立邀请关系（invitation-service，如果通过邀请）</li>
 *   <li>初始化用户统计（statistics-service）</li>
 *   <li>发送欢迎通知（notification-service）</li>
 * </ol>
 * 
 * <p>补偿操作：
 * <ol>
 *   <li>删除通知记录</li>
 *   <li>清除统计数据</li>
 *   <li>移除邀请关系</li>
 *   <li>删除邀请码</li>
 *   <li>删除用户账户</li>
 * </ol>
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Component
public class UserRegistrationSaga {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationSaga.class);
    
    private static final String SAGA_TYPE = "USER_REGISTRATION";

    @Autowired
    private SagaCoordinator sagaCoordinator;

    /**
     * 启动用户注册流程
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @param phone 手机号
     * @param role 用户角色
     * @param password 密码
     * @param invitationCode 邀请码（可选）
     * @param correlationId 关联ID
     * @return Saga启动结果
     */
    public CommonResult<String> startUserRegistration(Long userId, String username, String phone, 
                                                     String role, String password, String invitationCode, 
                                                     String correlationId) {
        try {
            logger.info("启动用户注册流程: userId={}, username={}, role={}, correlationId={}", 
                userId, username, role, correlationId);

            // 1. 准备业务上下文
            Map<String, Object> businessContext = createBusinessContext(
                userId, username, phone, role, password, invitationCode);

            // 2. 创建Saga事务
            CommonResult<SagaTransaction> createResult = sagaCoordinator.createSaga(
                SAGA_TYPE, correlationId, userId, businessContext);
            
            if (!createResult.getSuccess()) {
                return CommonResult.error("创建用户注册Saga失败: " + createResult.getMessage());
            }

            SagaTransaction saga = createResult.getData();

            // 3. 定义执行步骤
            defineUserRegistrationSteps(saga, invitationCode != null);

            // 4. 启动Saga执行
            CommonResult<Void> startResult = sagaCoordinator.startSaga(saga.getSagaId());
            if (!startResult.getSuccess()) {
                return CommonResult.error("启动用户注册Saga失败: " + startResult.getMessage());
            }

            logger.info("用户注册流程启动成功: sagaId={}, userId={}", saga.getSagaId(), userId);
            return CommonResult.success(saga.getSagaId());

        } catch (Exception e) {
            logger.error("启动用户注册流程失败: userId={}, correlationId={}", userId, correlationId, e);
            return CommonResult.error("启动用户注册流程失败: " + e.getMessage());
        }
    }

    /**
     * 创建业务上下文
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @param phone 手机号
     * @param role 用户角色
     * @param password 密码
     * @param invitationCode 邀请码
     * @return 业务上下文
     */
    private Map<String, Object> createBusinessContext(Long userId, String username, String phone, 
                                                     String role, String password, String invitationCode) {
        Map<String, Object> context = new HashMap<>();
        context.put("userId", userId);
        context.put("username", username);
        context.put("phone", phone);
        context.put("role", role);
        context.put("password", password);
        
        if (invitationCode != null) {
            context.put("invitationCode", invitationCode);
            context.put("hasInvitation", true);
        } else {
            context.put("hasInvitation", false);
        }
        
        context.put("registrationTime", System.currentTimeMillis());
        return context;
    }

    /**
     * 定义用户注册流程步骤
     * 
     * @param saga Saga事务
     * @param hasInvitation 是否有邀请信息
     */
    private void defineUserRegistrationSteps(SagaTransaction saga, boolean hasInvitation) {
        // 步骤1: 创建用户账户
        SagaStep createUserStep = SagaStep.createCompensable(
            "CREATE_USER_ACCOUNT",
            "auth-service",
            "createUserAccount",
            "deleteUserAccount"
        );
        createUserStep.setDescription("创建用户账户和认证信息");
        createUserStep.setTimeoutMillis(30000L);
        
        // 设置输入参数
        Map<String, Object> createUserParams = new HashMap<>();
        createUserParams.put("userId", saga.getContextData("userId"));
        createUserParams.put("username", saga.getContextData("username"));
        createUserParams.put("phone", saga.getContextData("phone"));
        createUserParams.put("role", saga.getContextData("role"));
        createUserParams.put("password", saga.getContextData("password"));
        createUserStep.setInputParameters(createUserParams);
        
        saga.addStep(createUserStep);

        // 步骤2: 生成用户邀请码
        SagaStep generateInvitationStep = SagaStep.createCompensable(
            "GENERATE_INVITATION_CODE",
            "invitation-service",
            "generateInvitationCode",
            "deleteInvitationCode"
        );
        generateInvitationStep.setDescription("为用户生成专属邀请码");
        generateInvitationStep.setTimeoutMillis(15000L);
        
        Map<String, Object> generateInvitationParams = new HashMap<>();
        generateInvitationParams.put("userId", saga.getContextData("userId"));
        generateInvitationParams.put("username", saga.getContextData("username"));
        generateInvitationStep.setInputParameters(generateInvitationParams);
        
        saga.addStep(generateInvitationStep);

        // 步骤3: 建立邀请关系（仅当有邀请信息时）
        if (hasInvitation) {
            SagaStep establishRelationshipStep = SagaStep.createCompensable(
                "ESTABLISH_INVITATION_RELATIONSHIP",
                "invitation-service",
                "establishInvitationRelationship",
                "removeInvitationRelationship"
            );
            establishRelationshipStep.setDescription("建立邀请人和被邀请人的关系");
            establishRelationshipStep.setTimeoutMillis(20000L);
            
            Map<String, Object> relationshipParams = new HashMap<>();
            relationshipParams.put("userId", saga.getContextData("userId"));
            relationshipParams.put("invitationCode", saga.getContextData("invitationCode"));
            establishRelationshipStep.setInputParameters(relationshipParams);
            
            saga.addStep(establishRelationshipStep);
        }

        // 步骤4: 初始化用户统计数据
        SagaStep initStatisticsStep = SagaStep.createCompensable(
            "INITIALIZE_USER_STATISTICS",
            "statistics-service",
            "initializeUserStatistics",
            "clearUserStatistics"
        );
        initStatisticsStep.setDescription("初始化用户的统计数据和指标");
        initStatisticsStep.setTimeoutMillis(10000L);
        
        Map<String, Object> statisticsParams = new HashMap<>();
        statisticsParams.put("userId", saga.getContextData("userId"));
        statisticsParams.put("role", saga.getContextData("role"));
        statisticsParams.put("registrationTime", saga.getContextData("registrationTime"));
        initStatisticsStep.setInputParameters(statisticsParams);
        
        saga.addStep(initStatisticsStep);

        // 步骤5: 发送欢迎通知
        SagaStep sendWelcomeStep = SagaStep.create(
            "SEND_WELCOME_NOTIFICATION",
            "notification-service",
            "sendWelcomeNotification"
        );
        sendWelcomeStep.setDescription("发送欢迎通知给新用户");
        sendWelcomeStep.setTimeoutMillis(15000L);
        sendWelcomeStep.setCompensable(false); // 通知不需要补偿
        
        Map<String, Object> notificationParams = new HashMap<>();
        notificationParams.put("userId", saga.getContextData("userId"));
        notificationParams.put("username", saga.getContextData("username"));
        notificationParams.put("phone", saga.getContextData("phone"));
        notificationParams.put("welcomeTemplate", "USER_REGISTRATION_WELCOME");
        sendWelcomeStep.setInputParameters(notificationParams);
        
        saga.addStep(sendWelcomeStep);

        logger.info("用户注册流程步骤定义完成: sagaId={}, stepCount={}", 
            saga.getSagaId(), saga.getSteps().size());
    }

    /**
     * 查询用户注册流程状态
     * 
     * @param sagaId Saga事务ID
     * @return 流程状态
     */
    public CommonResult<Map<String, Object>> getUserRegistrationStatus(String sagaId) {
        try {
            SagaTransaction saga = sagaCoordinator.getSaga(sagaId);
            if (saga == null) {
                return CommonResult.error("用户注册流程不存在: " + sagaId);
            }

            Map<String, Object> status = new HashMap<>();
            status.put("sagaId", sagaId);
            status.put("sagaType", saga.getSagaType());
            status.put("status", saga.getStatus().name());
            status.put("statusDescription", saga.getStatus().getDescription());
            status.put("currentStepIndex", saga.getCurrentStepIndex());
            status.put("totalSteps", saga.getSteps().size());
            status.put("createdAt", saga.getCreatedAt());
            status.put("startedAt", saga.getStartedAt());
            status.put("completedAt", saga.getCompletedAt());
            
            if (saga.getCurrentStep() != null) {
                status.put("currentStepName", saga.getCurrentStep().getStepName());
                status.put("currentStepStatus", saga.getCurrentStep().getStatus().name());
            }

            // 添加业务上下文中的用户信息
            if (saga.getBusinessContext() != null) {
                status.put("userId", saga.getBusinessContext().get("userId"));
                status.put("username", saga.getBusinessContext().get("username"));
            }

            return CommonResult.success(status);

        } catch (Exception e) {
            logger.error("查询用户注册流程状态失败: sagaId={}", sagaId, e);
            return CommonResult.error("查询流程状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取支持的Saga类型
     * 
     * @return Saga类型
     */
    public String getSagaType() {
        return SAGA_TYPE;
    }

    /**
     * 检查用户注册流程是否支持重试
     * 
     * @param sagaId Saga事务ID
     * @param stepName 步骤名称
     * @return 是否支持重试
     */
    public boolean canRetryStep(String sagaId, String stepName) {
        try {
            SagaTransaction saga = sagaCoordinator.getSaga(sagaId);
            if (saga == null) {
                return false;
            }

            SagaStep currentStep = saga.getCurrentStep();
            if (currentStep == null || !currentStep.getStepName().equals(stepName)) {
                return false;
            }

            // 检查步骤的重试策略
            return currentStep.canRetry() && 
                   currentStep.getStatus() == SagaStep.StepStatus.FAILED;

        } catch (Exception e) {
            logger.error("检查步骤重试能力失败: sagaId={}, stepName={}", sagaId, stepName, e);
            return false;
        }
    }

    /**
     * 估算用户注册流程的执行时间
     * 
     * @param hasInvitation 是否包含邀请流程
     * @return 估算时间（秒）
     */
    public int estimateExecutionTime(boolean hasInvitation) {
        // 基础流程：创建账户(30s) + 生成邀请码(15s) + 统计初始化(10s) + 发送通知(15s)
        int baseTime = 30 + 15 + 10 + 15; // 70秒
        
        // 如果有邀请流程，增加建立关系的时间
        if (hasInvitation) {
            baseTime += 20; // 增加20秒
        }
        
        return baseTime;
    }
}