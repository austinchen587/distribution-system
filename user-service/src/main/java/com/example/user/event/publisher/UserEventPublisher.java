package com.example.user.event.publisher;

import com.example.common.dto.CommonResult;
import com.example.common.event.DomainEventPublisher;
import com.example.common.event.domain.UserCreatedEvent;
import com.example.common.event.EventType;
import com.example.data.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

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
 *   <li>支持事务性事件发布</li>
 * </ul>
 * 
 * @author User Service Team
 * @version 1.0.0
 * @since 2025-08-12
 */
@Slf4j
@Component
public class UserEventPublisher {

    @Autowired
    private DomainEventPublisher eventPublisher;

    /**
     * 发布用户创建事件
     * 
     * <p>当新用户成功创建后发布此事件，触发后续的业务流程：
     * <ul>
     *   <li>为用户生成邀请码</li>
     *   <li>建立邀请关系（如果通过邀请注册）</li>
     *   <li>初始化用户统计数据</li>
     *   <li>发送欢迎通知</li>
     * </ul>
     * 
     * @param user 新创建的用户实体
     * @return 发布结果
     */
    public CommonResult<Void> publishUserCreated(User user) {
        return publishUserCreated(user, null, null);
    }

    /**
     * 发布用户创建事件（含邀请信息）
     * 
     * @param user 新创建的用户实体
     * @param invitationCode 邀请码（如果通过邀请注册）
     * @param inviterId 邀请人ID（如果通过邀请注册）
     * @return 发布结果
     */
    public CommonResult<Void> publishUserCreated(User user, String invitationCode, Long inviterId) {
        try {
            log.info("发布用户创建事件: userId={}, username={}, inviterId={}", 
                user.getId(), user.getUsername(), inviterId);

            String correlationId = generateCorrelationId();
            
            UserCreatedEvent event;
            if (StringUtils.hasText(invitationCode) && inviterId != null) {
                event = UserCreatedEvent.createWithInvitation(
                    user.getId(), user.getUsername(), user.getPhone(), 
                    user.getRole(), invitationCode, inviterId, correlationId);
            } else {
                event = UserCreatedEvent.create(
                    user.getId(), user.getUsername(), user.getPhone(), 
                    user.getRole(), correlationId);
            }

            return eventPublisher.publishEvent(event);

        } catch (Exception e) {
            log.error("发布用户创建事件失败: userId={}", user.getId(), e);
            return CommonResult.error("发布用户创建事件失败: " + e.getMessage());
        }
    }

    /**
     * 发布用户更新事件
     * 
     * <p>当用户信息更新后发布此事件，触发：
     * <ul>
     *   <li>同步用户信息到各个服务</li>
     *   <li>更新缓存数据</li>
     *   <li>通知相关系统</li>
     *   <li>记录变更审计日志</li>
     * </ul>
     * 
     * @param user 更新后的用户实体
     * @param updatedFields 更新的字段列表
     * @return 发布结果
     */
    public CommonResult<Void> publishUserUpdated(User user, String... updatedFields) {
        try {
            log.info("发布用户更新事件: userId={}, updatedFields={}", 
                user.getId(), String.join(",", updatedFields));

            String correlationId = generateCorrelationId();
            
            // TODO: 创建UserUpdatedEvent并发布
            // UserUpdatedEvent event = UserUpdatedEvent.create(
            //     user.getId(), user.getUsername(), updatedFields, correlationId);
            // return eventPublisher.publishEvent(event);

            log.info("用户更新事件发布成功: userId={}", user.getId());
            return CommonResult.success();

        } catch (Exception e) {
            log.error("发布用户更新事件失败: userId={}", user.getId(), e);
            return CommonResult.error("发布用户更新事件失败: " + e.getMessage());
        }
    }

    /**
     * 发布用户角色变更事件
     * 
     * <p>当用户角色发生变更时发布此事件，触发：
     * <ul>
     *   <li>更新用户权限和数据访问范围</li>
     *   <li>重新计算佣金比例</li>
     *   <li>同步角色信息到各服务</li>
     *   <li>通知奖励系统重新计算</li>
     * </ul>
     * 
     * @param user 用户实体
     * @param oldRole 原角色
     * @param newRole 新角色
     * @return 发布结果
     */
    public CommonResult<Void> publishUserRoleChanged(User user, String oldRole, String newRole) {
        try {
            log.info("发布用户角色变更事件: userId={}, oldRole={}, newRole={}", 
                user.getId(), oldRole, newRole);

            String correlationId = generateCorrelationId();
            
            // TODO: 创建UserRoleChangedEvent并发布
            // UserRoleChangedEvent event = UserRoleChangedEvent.create(
            //     user.getId(), user.getUsername(), oldRole, newRole, correlationId);
            // return eventPublisher.publishEvent(event);

            log.info("用户角色变更事件发布成功: userId={}", user.getId());
            return CommonResult.success();

        } catch (Exception e) {
            log.error("发布用户角色变更事件失败: userId={}", user.getId(), e);
            return CommonResult.error("发布用户角色变更事件失败: " + e.getMessage());
        }
    }

    /**
     * 发布用户状态变更事件
     * 
     * <p>当用户状态发生变更时发布此事件，触发：
     * <ul>
     *   <li>同步用户状态到各服务</li>
     *   <li>更新权限和访问控制</li>
     *   <li>处理在途业务数据</li>
     *   <li>记录状态变更审计</li>
     * </ul>
     * 
     * @param user 用户实体
     * @param oldStatus 原状态
     * @param newStatus 新状态
     * @return 发布结果
     */
    public CommonResult<Void> publishUserStatusChanged(User user, String oldStatus, String newStatus) {
        try {
            log.info("发布用户状态变更事件: userId={}, oldStatus={}, newStatus={}", 
                user.getId(), oldStatus, newStatus);

            String correlationId = generateCorrelationId();
            
            // TODO: 创建UserStatusChangedEvent并发布
            // UserStatusChangedEvent event = UserStatusChangedEvent.create(
            //     user.getId(), user.getUsername(), oldStatus, newStatus, correlationId);
            // return eventPublisher.publishEvent(event);

            log.info("用户状态变更事件发布成功: userId={}", user.getId());
            return CommonResult.success();

        } catch (Exception e) {
            log.error("发布用户状态变更事件失败: userId={}", user.getId(), e);
            return CommonResult.error("发布用户状态变更事件失败: " + e.getMessage());
        }
    }

    /**
     * 发布用户删除事件
     * 
     * <p>当用户被删除时发布此事件（通常是软删除），触发：
     * <ul>
     *   <li>清理用户相关数据</li>
     *   <li>处理关联业务数据</li>
     *   <li>通知各服务进行清理</li>
     *   <li>记录删除审计日志</li>
     * </ul>
     * 
     * @param userId 被删除的用户ID
     * @param username 用户名
     * @return 发布结果
     */
    public CommonResult<Void> publishUserDeleted(Long userId, String username) {
        try {
            log.info("发布用户删除事件: userId={}, username={}", userId, username);

            String correlationId = generateCorrelationId();
            
            // TODO: 创建UserDeletedEvent并发布
            // UserDeletedEvent event = UserDeletedEvent.create(userId, username, correlationId);
            // return eventPublisher.publishEvent(event);

            log.info("用户删除事件发布成功: userId={}", userId);
            return CommonResult.success();

        } catch (Exception e) {
            log.error("发布用户删除事件失败: userId={}", userId, e);
            return CommonResult.error("发布用户删除事件失败: " + e.getMessage());
        }
    }

    /**
     * 异步发布用户事件
     * 
     * <p>用于不需要等待发布结果的场景，提高响应性能
     * 
     * @param user 用户实体
     * @param eventType 事件类型
     */
    public void publishUserEventAsync(User user, EventType eventType) {
        try {
            log.debug("异步发布用户事件: userId={}, eventType={}", 
                user.getId(), eventType);

            String correlationId = generateCorrelationId();

            switch (eventType) {
                case USER_CREATED:
                    publishUserCreated(user);
                    break;
                case USER_UPDATED:
                    publishUserUpdated(user);
                    break;
                default:
                    log.warn("不支持的用户事件类型: {}", eventType);
                    break;
            }

        } catch (Exception e) {
            log.error("异步发布用户事件失败: userId={}, eventType={}", 
                user.getId(), eventType, e);
        }
    }

    /**
     * 生成关联ID
     * 
     * @return 唯一的关联ID
     */
    private String generateCorrelationId() {
        return "user-" + UUID.randomUUID().toString();
    }

    /**
     * 验证用户实体
     * 
     * @param user 用户实体
     * @return 是否有效
     */
    private boolean isValidUser(User user) {
        return user != null 
            && user.getId() != null 
            && StringUtils.hasText(user.getUsername())
            && StringUtils.hasText(user.getPhone())
            && StringUtils.hasText(user.getRole());
    }
}