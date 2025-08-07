package com.example.common.event.listener;

import com.example.common.event.domain.LeadCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 客资事件监听器
 * 
 * <p>监听客资相关的领域事件，处理客资生命周期中的各种业务逻辑。
 * 实现客资的自动化处理流程，包括重复检查、分配、审核等。
 * 
 * <p>处理的事件类型：
 * <ul>
 *   <li>客资创建事件：触发重复检查和分配流程</li>
 *   <li>客资审核事件：处理审核结果和后续流程</li>
 *   <li>客资转换事件：处理客资转化为客户的业务逻辑</li>
 * </ul>
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Component
public class LeadEventListener {

    private static final Logger logger = LoggerFactory.getLogger(LeadEventListener.class);

    /**
     * 处理客资创建事件
     * 
     * <p>当新客资提交后，执行以下业务逻辑：
     * <ul>
     *   <li>执行重复检查逻辑</li>
     *   <li>分配给相应的销售人员</li>
     *   <li>启动客资审核流程</li>
     *   <li>更新销售人员的客资统计</li>
     * </ul>
     * 
     * @param event 客资创建事件
     */
    @RabbitListener(queues = "lead.queue")
    public void handleLeadCreatedEvent(LeadCreatedEvent event) {
        try {
            logger.info("接收到客资创建事件: leadId={}, customerPhone={}, submitterId={}, correlationId={}", 
                event.getLeadId(), event.getCustomerPhone(), event.getSubmitterId(), event.getCorrelationId());

            // 1. 执行重复检查逻辑
            boolean isDuplicate = checkDuplicateLead(event);
            if (isDuplicate) {
                handleDuplicateLead(event);
                return;
            }

            // 2. 分配给相应的销售人员
            assignLeadToSales(event);

            // 3. 启动客资审核流程
            startLeadApprovalProcess(event);

            // 4. 更新销售人员的客资统计
            updateSubmitterStatistics(event);

            logger.info("客资创建事件处理完成: leadId={}", event.getLeadId());

        } catch (Exception e) {
            logger.error("处理客资创建事件失败: leadId={}, correlationId={}", 
                event.getLeadId(), event.getCorrelationId(), e);
            // TODO: 发送到死信队列或重试机制
        }
    }

    /**
     * 检查客资重复
     * 
     * @param event 客资创建事件
     * @return 是否重复
     */
    private boolean checkDuplicateLead(LeadCreatedEvent event) {
        try {
            logger.debug("检查客资重复: customerPhone={}, customerWechat={}", 
                event.getCustomerPhone(), event.getCustomerWechat());
            
            // TODO: 调用客资服务检查重复
            // boolean isDuplicate = leadService.checkDuplicate(event.getCustomerPhone(), event.getCustomerWechat());
            
            // 模拟重复检查逻辑
            boolean isDuplicate = false; // 简化实现
            
            logger.info("客资重复检查完成: leadId={}, isDuplicate={}", event.getLeadId(), isDuplicate);
            return isDuplicate;
            
        } catch (Exception e) {
            logger.error("客资重复检查失败: leadId={}", event.getLeadId(), e);
            // 检查失败时默认不重复，继续后续流程
            return false;
        }
    }

    /**
     * 处理重复客资
     * 
     * @param event 客资创建事件
     */
    private void handleDuplicateLead(LeadCreatedEvent event) {
        try {
            logger.warn("发现重复客资: leadId={}, customerPhone={}", 
                event.getLeadId(), event.getCustomerPhone());
            
            // TODO: 调用客资服务处理重复逻辑
            // leadService.markAsDuplicate(event.getLeadId(), "重复客资");
            
            // TODO: 发送重复通知给提交人
            // notificationService.sendDuplicateNotification(event.getSubmitterId(), event.getLeadId());
            
            logger.info("重复客资处理完成: leadId={}", event.getLeadId());
            
        } catch (Exception e) {
            logger.error("处理重复客资失败: leadId={}", event.getLeadId(), e);
        }
    }

    /**
     * 分配客资给销售人员
     * 
     * @param event 客资创建事件
     */
    private void assignLeadToSales(LeadCreatedEvent event) {
        try {
            logger.debug("分配客资给销售人员: leadId={}, submitterId={}", 
                event.getLeadId(), event.getSubmitterId());
            
            // TODO: 调用分配服务执行分配逻辑
            // assignmentService.assignLeadToSales(event.getLeadId(), event.getSubmitterId());
            
            logger.info("客资分配成功: leadId={}, submitterId={}", event.getLeadId(), event.getSubmitterId());
            
        } catch (Exception e) {
            logger.error("客资分配失败: leadId={}, submitterId={}", event.getLeadId(), event.getSubmitterId(), e);
            throw e;
        }
    }

    /**
     * 启动客资审核流程
     * 
     * @param event 客资创建事件
     */
    private void startLeadApprovalProcess(LeadCreatedEvent event) {
        try {
            logger.debug("启动客资审核流程: leadId={}", event.getLeadId());
            
            // TODO: 调用审核服务启动审核流程
            // approvalService.startLeadApproval(event.getLeadId(), event.getSubmitterId());
            
            logger.info("客资审核流程启动成功: leadId={}", event.getLeadId());
            
        } catch (Exception e) {
            logger.error("启动客资审核流程失败: leadId={}", event.getLeadId(), e);
            // 审核流程启动失败不影响客资创建，记录日志
        }
    }

    /**
     * 更新提交人统计数据
     * 
     * @param event 客资创建事件
     */
    private void updateSubmitterStatistics(LeadCreatedEvent event) {
        try {
            logger.debug("更新提交人统计数据: submitterId={}", event.getSubmitterId());
            
            // TODO: 调用统计服务更新数据
            // statisticsService.updateLeadSubmissionStats(event.getSubmitterId());
            
            logger.info("提交人统计数据更新成功: submitterId={}", event.getSubmitterId());
            
        } catch (Exception e) {
            logger.error("更新提交人统计数据失败: submitterId={}", event.getSubmitterId(), e);
            // 统计更新失败不影响主流程
        }
    }

    /**
     * 处理客资审核通过事件
     * 
     * @param eventJson 客资审核通过事件JSON
     */
    @RabbitListener(queues = "lead.queue")
    public void handleLeadApprovedEvent(String eventJson) {
        try {
            logger.info("接收到客资审核通过事件: {}", eventJson);
            
            // TODO: 解析事件并处理审核通过逻辑
            // 1. 更新客资状态
            // 2. 通知相关人员
            // 3. 启动后续业务流程
            
        } catch (Exception e) {
            logger.error("处理客资审核通过事件失败", e);
        }
    }

    /**
     * 处理客资转换事件
     * 
     * @param eventJson 客资转换事件JSON
     */
    @RabbitListener(queues = "lead.queue")
    public void handleLeadConvertedEvent(String eventJson) {
        try {
            logger.info("接收到客资转换事件: {}", eventJson);
            
            // TODO: 解析事件并处理转换逻辑
            // 1. 创建客户记录
            // 2. 计算转换奖励
            // 3. 更新转换统计
            // 4. 发送转换通知
            
        } catch (Exception e) {
            logger.error("处理客资转换事件失败", e);
        }
    }

    /**
     * 处理客资重复检测事件
     * 
     * @param eventJson 客资重复检测事件JSON
     */
    @RabbitListener(queues = "lead.queue")
    public void handleLeadDuplicateDetectedEvent(String eventJson) {
        try {
            logger.info("接收到客资重复检测事件: {}", eventJson);
            
            // TODO: 解析事件并处理重复检测逻辑
            // 1. 标记客资为重复
            // 2. 退还给提交人
            // 3. 记录重复日志
            // 4. 发送重复通知
            
        } catch (Exception e) {
            logger.error("处理客资重复检测事件失败", e);
        }
    }
}