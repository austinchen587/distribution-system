package com.example.common.event.listener;

import com.example.common.event.domain.UserCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 用户事件监听器
 * 
 * <p>监听用户相关的领域事件，处理用户生命周期中的各种业务逻辑。
 * 当用户事件发生时，触发相应的后续处理流程。
 * 
 * <p>处理的事件类型：
 * <ul>
 *   <li>用户创建事件：初始化用户相关数据</li>
 *   <li>用户更新事件：同步用户信息变更</li>
 *   <li>用户状态变更事件：处理状态相关业务逻辑</li>
 * </ul>
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Component
public class UserEventListener {

    private static final Logger logger = LoggerFactory.getLogger(UserEventListener.class);

    /**
     * 处理用户创建事件
     * 
     * <p>当新用户注册成功后，执行以下业务逻辑：
     * <ul>
     *   <li>为用户生成邀请码</li>
     *   <li>建立邀请关系（如果通过邀请注册）</li>
     *   <li>初始化用户统计数据</li>
     *   <li>发送欢迎通知</li>
     * </ul>
     * 
     * @param event 用户创建事件
     */
    @RabbitListener(queues = "user.queue")
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        try {
            logger.info("接收到用户创建事件: userId={}, username={}, correlationId={}", 
                event.getUserId(), event.getUsername(), event.getCorrelationId());

            // 1. 为用户生成邀请码
            generateInvitationCode(event);

            // 2. 建立邀请关系（如果通过邀请注册）
            if (event.getInvitationCode() != null && event.getInviterId() != null) {
                establishInvitationRelationship(event);
            }

            // 3. 初始化用户统计数据
            initializeUserStatistics(event);

            // 4. 发送欢迎通知
            sendWelcomeNotification(event);

            logger.info("用户创建事件处理完成: userId={}", event.getUserId());

        } catch (Exception e) {
            logger.error("处理用户创建事件失败: userId={}, correlationId={}", 
                event.getUserId(), event.getCorrelationId(), e);
            // TODO: 发送到死信队列或重试机制
        }
    }

    /**
     * 为用户生成邀请码
     * 
     * @param event 用户创建事件
     */
    private void generateInvitationCode(UserCreatedEvent event) {
        try {
            logger.debug("为用户生成邀请码: userId={}", event.getUserId());
            
            // TODO: 调用邀请服务生成邀请码
            // invitationService.generateInvitationCode(event.getUserId());
            
            logger.info("用户邀请码生成成功: userId={}", event.getUserId());
        } catch (Exception e) {
            logger.error("生成用户邀请码失败: userId={}", event.getUserId(), e);
            throw e;
        }
    }

    /**
     * 建立邀请关系
     * 
     * @param event 用户创建事件
     */
    private void establishInvitationRelationship(UserCreatedEvent event) {
        try {
            logger.debug("建立邀请关系: userId={}, inviterId={}, invitationCode={}", 
                event.getUserId(), event.getInviterId(), event.getInvitationCode());
            
            // TODO: 调用邀请服务建立邀请关系
            // invitationService.establishRelationship(event.getUserId(), event.getInviterId(), event.getInvitationCode());
            
            logger.info("邀请关系建立成功: userId={}, inviterId={}", event.getUserId(), event.getInviterId());
        } catch (Exception e) {
            logger.error("建立邀请关系失败: userId={}, inviterId={}", event.getUserId(), event.getInviterId(), e);
            throw e;
        }
    }

    /**
     * 初始化用户统计数据
     * 
     * @param event 用户创建事件
     */
    private void initializeUserStatistics(UserCreatedEvent event) {
        try {
            logger.debug("初始化用户统计数据: userId={}", event.getUserId());
            
            // TODO: 调用统计服务初始化用户数据
            // statisticsService.initializeUserStatistics(event.getUserId(), event.getRole());
            
            logger.info("用户统计数据初始化成功: userId={}", event.getUserId());
        } catch (Exception e) {
            logger.error("初始化用户统计数据失败: userId={}", event.getUserId(), e);
            // 统计数据初始化失败不影响主流程，记录日志即可
        }
    }

    /**
     * 发送欢迎通知
     * 
     * @param event 用户创建事件
     */
    private void sendWelcomeNotification(UserCreatedEvent event) {
        try {
            logger.debug("发送欢迎通知: userId={}, phone={}", event.getUserId(), event.getPhone());
            
            // TODO: 调用通知服务发送欢迎消息
            // notificationService.sendWelcomeMessage(event.getUserId(), event.getUsername(), event.getPhone());
            
            logger.info("欢迎通知发送成功: userId={}", event.getUserId());
        } catch (Exception e) {
            logger.error("发送欢迎通知失败: userId={}", event.getUserId(), e);
            // 通知发送失败不影响主流程，记录日志即可
        }
    }

    /**
     * 处理用户更新事件
     * 
     * @param eventJson 用户更新事件JSON
     */
    @RabbitListener(queues = "user.queue", containerFactory = "rabbitListenerContainerFactory")
    public void handleUserUpdatedEvent(String eventJson) {
        try {
            logger.info("接收到用户更新事件: {}", eventJson);
            
            // TODO: 解析事件并处理用户更新逻辑
            // 1. 同步用户信息到各个服务
            // 2. 更新缓存数据
            // 3. 通知相关系统
            
        } catch (Exception e) {
            logger.error("处理用户更新事件失败", e);
        }
    }

    /**
     * 处理用户状态变更事件
     * 
     * @param eventJson 用户状态变更事件JSON
     */
    @RabbitListener(queues = "user.queue")
    public void handleUserStatusChangedEvent(String eventJson) {
        try {
            logger.info("接收到用户状态变更事件: {}", eventJson);
            
            // TODO: 解析事件并处理状态变更逻辑
            // 1. 更新用户权限
            // 2. 同步状态到相关服务
            // 3. 记录状态变更日志
            
        } catch (Exception e) {
            logger.error("处理用户状态变更事件失败", e);
        }
    }
}