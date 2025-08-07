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
 * 客资处理业务流程Saga
 * 
 * <p>处理客资从提交到转化的完整业务流程，包括重复检查、分配、
 * 审核、跟进等多个环节的协调操作。确保客资处理的一致性和可靠性。
 * 
 * <p>业务流程步骤：
 * <ol>
 *   <li>执行重复检查（lead-service）</li>
 *   <li>分配给销售人员（assignment-service）</li>
 *   <li>创建客资记录（lead-service）</li>
 *   <li>启动跟进流程（follow-service）</li>
 *   <li>发送分配通知（notification-service）</li>
 *   <li>更新统计数据（statistics-service）</li>
 * </ol>
 * 
 * <p>补偿操作：
 * <ol>
 *   <li>清除统计更新</li>
 *   <li>取消分配通知</li>
 *   <li>停止跟进流程</li>
 *   <li>删除客资记录</li>
 *   <li>取消销售分配</li>
 *   <li>清除重复标记</li>
 * </ol>
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Component
public class LeadProcessingSaga {

    private static final Logger logger = LoggerFactory.getLogger(LeadProcessingSaga.class);
    
    private static final String SAGA_TYPE = "LEAD_PROCESSING";

    @Autowired
    private SagaCoordinator sagaCoordinator;

    /**
     * 启动客资处理流程
     * 
     * @param leadId 客资ID
     * @param customerName 客户姓名
     * @param customerPhone 客户手机号
     * @param customerWechat 客户微信号
     * @param submitterId 提交人ID
     * @param source 客资来源
     * @param remarks 备注信息
     * @param correlationId 关联ID
     * @return Saga启动结果
     */
    public CommonResult<String> startLeadProcessing(Long leadId, String customerName, String customerPhone,
                                                   String customerWechat, Long submitterId, String source,
                                                   String remarks, String correlationId) {
        try {
            logger.info("启动客资处理流程: leadId={}, customerPhone={}, submitterId={}, correlationId={}", 
                leadId, customerPhone, submitterId, correlationId);

            // 1. 准备业务上下文
            Map<String, Object> businessContext = createLeadProcessingContext(
                leadId, customerName, customerPhone, customerWechat, submitterId, source, remarks);

            // 2. 创建Saga事务
            CommonResult<SagaTransaction> createResult = sagaCoordinator.createSaga(
                SAGA_TYPE, correlationId, submitterId, businessContext);
            
            if (!createResult.getSuccess()) {
                return CommonResult.error("创建客资处理Saga失败: " + createResult.getMessage());
            }

            SagaTransaction saga = createResult.getData();

            // 3. 定义执行步骤
            defineLeadProcessingSteps(saga);

            // 4. 启动Saga执行
            CommonResult<Void> startResult = sagaCoordinator.startSaga(saga.getSagaId());
            if (!startResult.getSuccess()) {
                return CommonResult.error("启动客资处理Saga失败: " + startResult.getMessage());
            }

            logger.info("客资处理流程启动成功: sagaId={}, leadId={}", saga.getSagaId(), leadId);
            return CommonResult.success(saga.getSagaId());

        } catch (Exception e) {
            logger.error("启动客资处理流程失败: leadId={}, correlationId={}", leadId, correlationId, e);
            return CommonResult.error("启动客资处理流程失败: " + e.getMessage());
        }
    }

    /**
     * 创建客资处理业务上下文
     * 
     * @param leadId 客资ID
     * @param customerName 客户姓名
     * @param customerPhone 客户手机号
     * @param customerWechat 客户微信号
     * @param submitterId 提交人ID
     * @param source 客资来源
     * @param remarks 备注信息
     * @return 业务上下文
     */
    private Map<String, Object> createLeadProcessingContext(Long leadId, String customerName, 
                                                           String customerPhone, String customerWechat,
                                                           Long submitterId, String source, String remarks) {
        Map<String, Object> context = new HashMap<>();
        context.put("leadId", leadId);
        context.put("customerName", customerName);
        context.put("customerPhone", customerPhone);
        context.put("customerWechat", customerWechat);
        context.put("submitterId", submitterId);
        context.put("source", source);
        context.put("remarks", remarks);
        context.put("submissionTime", System.currentTimeMillis());
        context.put("priority", calculateLeadPriority(source, submitterId));
        
        return context;
    }

    /**
     * 定义客资处理流程步骤
     * 
     * @param saga Saga事务
     */
    private void defineLeadProcessingSteps(SagaTransaction saga) {
        // 步骤1: 执行重复检查
        SagaStep duplicateCheckStep = SagaStep.createCompensable(
            "DUPLICATE_CHECK",
            "lead-service",
            "checkDuplicateLead",
            "clearDuplicateCheck"
        );
        duplicateCheckStep.setDescription("检查客资是否重复提交");
        duplicateCheckStep.setTimeoutMillis(20000L);
        
        Map<String, Object> duplicateCheckParams = new HashMap<>();
        duplicateCheckParams.put("customerPhone", saga.getContextData("customerPhone"));
        duplicateCheckParams.put("customerWechat", saga.getContextData("customerWechat"));
        duplicateCheckParams.put("submitterId", saga.getContextData("submitterId"));
        duplicateCheckStep.setInputParameters(duplicateCheckParams);
        
        saga.addStep(duplicateCheckStep);

        // 步骤2: 分配给销售人员
        SagaStep assignmentStep = SagaStep.createCompensable(
            "LEAD_ASSIGNMENT",
            "assignment-service",
            "assignLeadToSales",
            "cancelLeadAssignment"
        );
        assignmentStep.setDescription("将客资分配给合适的销售人员");
        assignmentStep.setTimeoutMillis(15000L);
        
        Map<String, Object> assignmentParams = new HashMap<>();
        assignmentParams.put("leadId", saga.getContextData("leadId"));
        assignmentParams.put("submitterId", saga.getContextData("submitterId"));
        assignmentParams.put("priority", saga.getContextData("priority"));
        assignmentParams.put("source", saga.getContextData("source"));
        assignmentStep.setInputParameters(assignmentParams);
        
        saga.addStep(assignmentStep);

        // 步骤3: 创建客资记录
        SagaStep createLeadStep = SagaStep.createCompensable(
            "CREATE_LEAD_RECORD",
            "lead-service",
            "createLeadRecord",
            "deleteLeadRecord"
        );
        createLeadStep.setDescription("在系统中创建正式的客资记录");
        createLeadStep.setTimeoutMillis(25000L);
        
        Map<String, Object> createLeadParams = new HashMap<>();
        createLeadParams.put("leadId", saga.getContextData("leadId"));
        createLeadParams.put("customerName", saga.getContextData("customerName"));
        createLeadParams.put("customerPhone", saga.getContextData("customerPhone"));
        createLeadParams.put("customerWechat", saga.getContextData("customerWechat"));
        createLeadParams.put("submitterId", saga.getContextData("submitterId"));
        createLeadParams.put("source", saga.getContextData("source"));
        createLeadParams.put("remarks", saga.getContextData("remarks"));
        createLeadParams.put("status", "PENDING_APPROVAL");
        createLeadStep.setInputParameters(createLeadParams);
        
        saga.addStep(createLeadStep);

        // 步骤4: 启动跟进流程
        SagaStep followUpStep = SagaStep.createCompensable(
            "START_FOLLOW_UP",
            "follow-service",
            "startLeadFollowUp",
            "stopLeadFollowUp"
        );
        followUpStep.setDescription("启动客资的自动跟进流程");
        followUpStep.setTimeoutMillis(10000L);
        
        Map<String, Object> followUpParams = new HashMap<>();
        followUpParams.put("leadId", saga.getContextData("leadId"));
        followUpParams.put("customerPhone", saga.getContextData("customerPhone"));
        followUpParams.put("priority", saga.getContextData("priority"));
        followUpParams.put("followUpTemplate", "LEAD_INITIAL_CONTACT");
        followUpStep.setInputParameters(followUpParams);
        
        saga.addStep(followUpStep);

        // 步骤5: 发送分配通知
        SagaStep notificationStep = SagaStep.create(
            "SEND_ASSIGNMENT_NOTIFICATION",
            "notification-service",
            "sendLeadAssignmentNotification"
        );
        notificationStep.setDescription("发送客资分配通知给相关人员");
        notificationStep.setTimeoutMillis(15000L);
        notificationStep.setCompensable(false); // 通知不需要补偿
        
        Map<String, Object> notificationParams = new HashMap<>();
        notificationParams.put("leadId", saga.getContextData("leadId"));
        notificationParams.put("customerName", saga.getContextData("customerName"));
        notificationParams.put("submitterId", saga.getContextData("submitterId"));
        notificationParams.put("notificationType", "LEAD_ASSIGNMENT");
        notificationStep.setInputParameters(notificationParams);
        
        saga.addStep(notificationStep);

        // 步骤6: 更新统计数据
        SagaStep statisticsStep = SagaStep.createCompensable(
            "UPDATE_LEAD_STATISTICS",
            "statistics-service",
            "updateLeadSubmissionStats",
            "revertLeadSubmissionStats"
        );
        statisticsStep.setDescription("更新提交人和分配人的统计数据");
        statisticsStep.setTimeoutMillis(10000L);
        
        Map<String, Object> statisticsParams = new HashMap<>();
        statisticsParams.put("submitterId", saga.getContextData("submitterId"));
        statisticsParams.put("leadId", saga.getContextData("leadId"));
        statisticsParams.put("source", saga.getContextData("source"));
        statisticsParams.put("submissionTime", saga.getContextData("submissionTime"));
        statisticsStep.setInputParameters(statisticsParams);
        
        saga.addStep(statisticsStep);

        logger.info("客资处理流程步骤定义完成: sagaId={}, stepCount={}", 
            saga.getSagaId(), saga.getSteps().size());
    }

    /**
     * 计算客资优先级
     * 
     * @param source 客资来源
     * @param submitterId 提交人ID
     * @return 优先级（1-5，1最高）
     */
    private int calculateLeadPriority(String source, Long submitterId) {
        int priority = 3; // 默认优先级
        
        // 根据来源调整优先级
        if ("DIRECT_REFERRAL".equals(source)) {
            priority = 1; // 直接推荐优先级最高
        } else if ("PARTNER_CHANNEL".equals(source)) {
            priority = 2; // 合作渠道优先级较高
        } else if ("ONLINE_FORM".equals(source)) {
            priority = 4; // 在线表单优先级较低
        }
        
        // 可以根据提交人的等级进一步调整优先级
        // TODO: 查询提交人等级并调整优先级
        
        return priority;
    }

    /**
     * 启动客资审核流程
     * 
     * @param leadId 客资ID
     * @param reviewerId 审核人ID
     * @param correlationId 关联ID
     * @return 审核流程启动结果
     */
    public CommonResult<String> startLeadApproval(Long leadId, Long reviewerId, String correlationId) {
        try {
            logger.info("启动客资审核流程: leadId={}, reviewerId={}, correlationId={}", 
                leadId, reviewerId, correlationId);

            // 创建审核流程的业务上下文
            Map<String, Object> approvalContext = new HashMap<>();
            approvalContext.put("leadId", leadId);
            approvalContext.put("reviewerId", reviewerId);
            approvalContext.put("approvalType", "LEAD_REVIEW");
            approvalContext.put("reviewStartTime", System.currentTimeMillis());

            // 创建审核Saga事务
            CommonResult<SagaTransaction> createResult = sagaCoordinator.createSaga(
                "LEAD_APPROVAL", correlationId, reviewerId, approvalContext);
            
            if (!createResult.getSuccess()) {
                return CommonResult.error("创建客资审核Saga失败: " + createResult.getMessage());
            }

            SagaTransaction approvalSaga = createResult.getData();
            
            // 定义审核步骤
            defineLeadApprovalSteps(approvalSaga);

            // 启动审核流程
            CommonResult<Void> startResult = sagaCoordinator.startSaga(approvalSaga.getSagaId());
            if (!startResult.getSuccess()) {
                return CommonResult.error("启动客资审核流程失败: " + startResult.getMessage());
            }

            return CommonResult.success(approvalSaga.getSagaId());

        } catch (Exception e) {
            logger.error("启动客资审核流程失败: leadId={}, correlationId={}", leadId, correlationId, e);
            return CommonResult.error("启动客资审核流程失败: " + e.getMessage());
        }
    }

    /**
     * 定义客资审核流程步骤
     * 
     * @param saga Saga事务
     */
    private void defineLeadApprovalSteps(SagaTransaction saga) {
        // 步骤1: 执行自动审核
        SagaStep autoReviewStep = SagaStep.create(
            "AUTO_LEAD_REVIEW",
            "lead-service",
            "executeAutoLeadReview"
        );
        autoReviewStep.setDescription("执行客资的自动审核检查");
        autoReviewStep.setTimeoutMillis(20000L);
        
        Map<String, Object> autoReviewParams = new HashMap<>();
        autoReviewParams.put("leadId", saga.getContextData("leadId"));
        autoReviewParams.put("reviewType", "AUTO_REVIEW");
        autoReviewStep.setInputParameters(autoReviewParams);
        
        saga.addStep(autoReviewStep);

        // 步骤2: 人工审核（如果需要）
        SagaStep manualReviewStep = SagaStep.create(
            "MANUAL_LEAD_REVIEW",
            "lead-service",
            "executeManualLeadReview"
        );
        manualReviewStep.setDescription("执行客资的人工审核");
        manualReviewStep.setTimeoutMillis(300000L); // 5分钟超时，给人工审核更多时间
        
        Map<String, Object> manualReviewParams = new HashMap<>();
        manualReviewParams.put("leadId", saga.getContextData("leadId"));
        manualReviewParams.put("reviewerId", saga.getContextData("reviewerId"));
        manualReviewParams.put("reviewType", "MANUAL_REVIEW");
        manualReviewStep.setInputParameters(manualReviewParams);
        
        saga.addStep(manualReviewStep);

        // 步骤3: 发送审核结果通知
        SagaStep reviewNotificationStep = SagaStep.create(
            "SEND_REVIEW_NOTIFICATION",
            "notification-service",
            "sendLeadReviewNotification"
        );
        reviewNotificationStep.setDescription("发送审核结果通知");
        reviewNotificationStep.setTimeoutMillis(15000L);
        reviewNotificationStep.setCompensable(false);
        
        Map<String, Object> reviewNotificationParams = new HashMap<>();
        reviewNotificationParams.put("leadId", saga.getContextData("leadId"));
        reviewNotificationParams.put("reviewerId", saga.getContextData("reviewerId"));
        reviewNotificationParams.put("notificationType", "LEAD_REVIEW_RESULT");
        reviewNotificationStep.setInputParameters(reviewNotificationParams);
        
        saga.addStep(reviewNotificationStep);
    }

    /**
     * 查询客资处理流程状态
     * 
     * @param sagaId Saga事务ID
     * @return 流程状态
     */
    public CommonResult<Map<String, Object>> getLeadProcessingStatus(String sagaId) {
        try {
            SagaTransaction saga = sagaCoordinator.getSaga(sagaId);
            if (saga == null) {
                return CommonResult.error("客资处理流程不存在: " + sagaId);
            }

            Map<String, Object> status = new HashMap<>();
            status.put("sagaId", sagaId);
            status.put("sagaType", saga.getSagaType());
            status.put("status", saga.getStatus().name());
            status.put("statusDescription", saga.getStatus().getDescription());
            status.put("currentStepIndex", saga.getCurrentStepIndex());
            status.put("totalSteps", saga.getSteps().size());
            status.put("progress", (double) saga.getCurrentStepIndex() / saga.getSteps().size() * 100);
            
            // 添加业务上下文信息
            if (saga.getBusinessContext() != null) {
                status.put("leadId", saga.getBusinessContext().get("leadId"));
                status.put("customerName", saga.getBusinessContext().get("customerName"));
                status.put("customerPhone", saga.getBusinessContext().get("customerPhone"));
                status.put("submitterId", saga.getBusinessContext().get("submitterId"));
            }

            return CommonResult.success(status);

        } catch (Exception e) {
            logger.error("查询客资处理流程状态失败: sagaId={}", sagaId, e);
            return CommonResult.error("查询流程状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取支持的Saga类型
     * 
     * @return Saga类型列表
     */
    public String[] getSagaTypes() {
        return new String[]{SAGA_TYPE, "LEAD_APPROVAL"};
    }
}