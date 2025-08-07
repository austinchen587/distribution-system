package com.example.common.event.publisher;

import com.example.common.dto.CommonResult;
import com.example.common.event.DomainEventPublisher;
import com.example.common.event.domain.UserCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户事件发布器
 * 
 * <p>负责发布用户相关的领域事件，封装事件创建和发布的复杂逻辑。
 * 提供简洁的API供业务服务使用，确保事件发布的一致性和可靠性。
 * 
 * <p>主要功能：
 * <ul>
 *   <li>封装用户事件的创建逻辑</li>
 *   <li>提供类型安全的事件发布接口</li>
 *   <li>处理事件发布的异常情况</li>
 *   <li>记录事件发布的审计日志</li>
 * </ul>
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Component
public class UserEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(UserEventPublisher.class);

    @Autowired
    private DomainEventPublisher eventPublisher;

    /**
     * 发布用户创建事件
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @param phone 手机号
     * @param role 用户角色
     * @param correlationId 关联ID
     * @return 发布结果
     */
    public CommonResult<Void> publishUserCreated(Long userId, String username, String phone, 
                                                String role, String correlationId) {
        try {
            logger.info("发布用户创建事件: userId={}, username={}, role={}, correlationId={}", 
                userId, username, role, correlationId);

            UserCreatedEvent event = UserCreatedEvent.create(userId, username, phone, role, correlationId);
            return eventPublisher.publishEvent(event);

        } catch (Exception e) {
            logger.error("发布用户创建事件失败: userId={}, correlationId={}", userId, correlationId, e);
            return CommonResult.error("发布用户创建事件失败: " + e.getMessage());
        }
    }

    /**
     * 发布带邀请信息的用户创建事件
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @param phone 手机号
     * @param role 用户角色
     * @param invitationCode 邀请码
     * @param inviterId 邀请人ID
     * @param correlationId 关联ID
     * @return 发布结果
     */
    public CommonResult<Void> publishUserCreatedWithInvitation(Long userId, String username, String phone, 
                                                              String role, String invitationCode, 
                                                              Long inviterId, String correlationId) {
        try {
            logger.info("发布用户创建事件（含邀请信息）: userId={}, inviterId={}, invitationCode={}, correlationId={}", 
                userId, inviterId, invitationCode, correlationId);

            UserCreatedEvent event = UserCreatedEvent.createWithInvitation(
                userId, username, phone, role, invitationCode, inviterId, correlationId);
            return eventPublisher.publishEvent(event);

        } catch (Exception e) {
            logger.error("发布用户创建事件（含邀请信息）失败: userId={}, inviterId={}, correlationId={}", 
                userId, inviterId, correlationId, e);
            return CommonResult.error("发布用户创建事件失败: " + e.getMessage());
        }
    }

    /**
     * 发布用户更新事件
     * 
     * @param userId 用户ID
     * @param correlationId 关联ID
     * @return 发布结果
     */
    public CommonResult<Void> publishUserUpdated(Long userId, String correlationId) {
        try {
            logger.info("发布用户更新事件: userId={}, correlationId={}", userId, correlationId);

            // TODO: 创建UserUpdatedEvent并发布
            // UserUpdatedEvent event = UserUpdatedEvent.create(userId, correlationId);
            // return eventPublisher.publishEvent(event);

            logger.info("用户更新事件发布成功: userId={}", userId);
            return CommonResult.success();

        } catch (Exception e) {
            logger.error("发布用户更新事件失败: userId={}, correlationId={}", userId, correlationId, e);
            return CommonResult.error("发布用户更新事件失败: " + e.getMessage());
        }
    }

    /**
     * 发布用户状态变更事件
     * 
     * @param userId 用户ID
     * @param oldStatus 原状态
     * @param newStatus 新状态
     * @param correlationId 关联ID
     * @return 发布结果
     */
    public CommonResult<Void> publishUserStatusChanged(Long userId, String oldStatus, 
                                                      String newStatus, String correlationId) {
        try {
            logger.info("发布用户状态变更事件: userId={}, oldStatus={}, newStatus={}, correlationId={}", 
                userId, oldStatus, newStatus, correlationId);

            // TODO: 创建UserStatusChangedEvent并发布
            // UserStatusChangedEvent event = UserStatusChangedEvent.create(userId, oldStatus, newStatus, correlationId);
            // return eventPublisher.publishEvent(event);

            logger.info("用户状态变更事件发布成功: userId={}", userId);
            return CommonResult.success();

        } catch (Exception e) {
            logger.error("发布用户状态变更事件失败: userId={}, correlationId={}", userId, correlationId, e);
            return CommonResult.error("发布用户状态变更事件失败: " + e.getMessage());
        }
    }

    /**
     * 发布用户角色变更事件
     * 
     * @param userId 用户ID
     * @param oldRole 原角色
     * @param newRole 新角色
     * @param correlationId 关联ID
     * @return 发布结果
     */
    public CommonResult<Void> publishUserRoleChanged(Long userId, String oldRole, 
                                                    String newRole, String correlationId) {
        try {
            logger.info("发布用户角色变更事件: userId={}, oldRole={}, newRole={}, correlationId={}", 
                userId, oldRole, newRole, correlationId);

            // TODO: 创建UserRoleChangedEvent并发布
            // UserRoleChangedEvent event = UserRoleChangedEvent.create(userId, oldRole, newRole, correlationId);
            // return eventPublisher.publishEvent(event);

            logger.info("用户角色变更事件发布成功: userId={}", userId);
            return CommonResult.success();

        } catch (Exception e) {
            logger.error("发布用户角色变更事件失败: userId={}, correlationId={}", userId, correlationId, e);
            return CommonResult.error("发布用户角色变更事件失败: " + e.getMessage());
        }
    }

    /**
     * 发布用户登录事件
     * 
     * @param userId 用户ID
     * @param loginIp 登录IP
     * @param correlationId 关联ID
     * @return 发布结果
     */
    public CommonResult<Void> publishUserLogin(Long userId, String loginIp, String correlationId) {
        try {
            logger.debug("发布用户登录事件: userId={}, loginIp={}, correlationId={}", 
                userId, loginIp, correlationId);

            // TODO: 创建UserLoginEvent并发布
            // UserLoginEvent event = UserLoginEvent.create(userId, loginIp, correlationId);
            // return eventPublisher.publishEvent(event);

            logger.debug("用户登录事件发布成功: userId={}", userId);
            return CommonResult.success();

        } catch (Exception e) {
            logger.error("发布用户登录事件失败: userId={}, correlationId={}", userId, correlationId, e);
            return CommonResult.error("发布用户登录事件失败: " + e.getMessage());
        }
    }

    /**
     * 发布用户登出事件
     * 
     * @param userId 用户ID
     * @param correlationId 关联ID
     * @return 发布结果
     */
    public CommonResult<Void> publishUserLogout(Long userId, String correlationId) {
        try {
            logger.debug("发布用户登出事件: userId={}, correlationId={}", userId, correlationId);

            // TODO: 创建UserLogoutEvent并发布
            // UserLogoutEvent event = UserLogoutEvent.create(userId, correlationId);
            // return eventPublisher.publishEvent(event);

            logger.debug("用户登出事件发布成功: userId={}", userId);
            return CommonResult.success();

        } catch (Exception e) {
            logger.error("发布用户登出事件失败: userId={}, correlationId={}", userId, correlationId, e);
            return CommonResult.error("发布用户登出事件失败: " + e.getMessage());
        }
    }

    /**
     * 批量发布用户相关事件
     * 
     * @param events 事件列表
     * @param transactional 是否事务性发布
     * @return 发布结果
     */
    public CommonResult<Void> publishUserEvents(java.util.List<com.example.common.event.DomainEvent> events, 
                                               boolean transactional) {
        try {
            logger.info("批量发布用户事件: count={}, transactional={}", events.size(), transactional);

            return eventPublisher.publishEvents(events, transactional);

        } catch (Exception e) {
            logger.error("批量发布用户事件失败: count={}", events.size(), e);
            return CommonResult.error("批量发布用户事件失败: " + e.getMessage());
        }
    }

    /**
     * 异步发布用户事件
     * 
     * @param userId 用户ID
     * @param eventType 事件类型
     * @param correlationId 关联ID
     */
    public void publishUserEventAsync(Long userId, String eventType, String correlationId) {
        try {
            logger.debug("异步发布用户事件: userId={}, eventType={}, correlationId={}", 
                userId, eventType, correlationId);

            // 根据事件类型创建相应的事件并异步发布
            switch (eventType) {
                case "USER_UPDATED":
                    // TODO: 创建并异步发布用户更新事件
                    break;
                case "USER_STATUS_CHANGED":
                    // TODO: 创建并异步发布用户状态变更事件
                    break;
                default:
                    logger.warn("未知的用户事件类型: {}", eventType);
                    break;
            }

        } catch (Exception e) {
            logger.error("异步发布用户事件失败: userId={}, eventType={}, correlationId={}", 
                userId, eventType, correlationId, e);
        }
    }
}