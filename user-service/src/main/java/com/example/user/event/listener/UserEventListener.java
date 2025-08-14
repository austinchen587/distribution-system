package com.example.user.event.listener;

import com.example.common.event.domain.UserCreatedEvent;
import com.example.user.event.handler.UserCreatedEventHandler;
import com.example.user.event.handler.UserUpdatedEventHandler;
import com.example.user.event.handler.UserRoleChangedEventHandler;
import com.example.user.event.handler.UserStatusChangedEventHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 用户事件监听器
 * 
 * <p>监听用户相关的领域事件，处理用户生命周期中的各种业务逻辑。
 * 当用户事件发生时，将事件分发给对应的处理器进行处理。
 * 
 * <p>处理的事件类型：
 * <ul>
 *   <li>用户创建事件：初始化用户相关数据</li>
 *   <li>用户更新事件：同步用户信息变更</li>
 *   <li>用户状态变更事件：处理状态相关业务逻辑</li>
 *   <li>用户角色变更事件：处理角色和权限变更</li>
 * </ul>
 * 
 * <p>监听配置：
 * <ul>
 *   <li>Exchange: user.exchange</li>
 *   <li>Queue: user.service.queue</li>
 *   <li>RoutingKey: user.*</li>
 * </ul>
 * 
 * @author User Service Team
 * @version 1.0.0
 * @since 2025-08-12
 */
@Slf4j
@Component
public class UserEventListener {

    @Autowired
    private UserCreatedEventHandler userCreatedEventHandler;

    @Autowired
    private UserUpdatedEventHandler userUpdatedEventHandler;

    @Autowired
    private UserRoleChangedEventHandler userRoleChangedEventHandler;

    @Autowired
    private UserStatusChangedEventHandler userStatusChangedEventHandler;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 处理用户创建事件
     * 
     * <p>当新用户注册成功后，执行以下业务逻辑：
     * <ul>
     *   <li>为用户生成邀请码</li>
     *   <li>建立邀请关系（如果通过邀请注册）</li>
     *   <li>初始化用户统计数据</li>
     *   <li>发送欢迎通知</li>
     *   <li>同步用户信息到相关服务</li>
     * </ul>
     * 
     * @param event 用户创建事件
     */
    @RabbitListener(queues = "user.service.queue", containerFactory = "rabbitListenerContainerFactory")
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        try {
            log.info("接收到用户创建事件: userId={}, username={}, correlationId={}", 
                event.getUserId(), event.getUsername(), event.getCorrelationId());

            // 验证事件数据
            if (!event.isValid()) {
                log.error("用户创建事件数据无效: {}", event);
                return;
            }

            // 委托给专门的处理器处理
            userCreatedEventHandler.handle(event);

            log.info("用户创建事件处理完成: userId={}", event.getUserId());

        } catch (Exception e) {
            log.error("处理用户创建事件失败: userId={}, correlationId={}", 
                event.getUserId(), event.getCorrelationId(), e);
            // TODO: 发送到死信队列或重试机制
            throw e; // 重新抛出异常以触发消息重试
        }
    }

    /**
     * 处理用户更新事件
     * 
     * <p>当用户信息更新后，执行以下业务逻辑：
     * <ul>
     *   <li>同步用户信息到各个服务</li>
     *   <li>更新缓存数据</li>
     *   <li>通知相关系统</li>
     *   <li>记录变更审计日志</li>
     * </ul>
     * 
     * @param eventJson 用户更新事件JSON字符串
     */
    @RabbitListener(queues = "user.service.queue", containerFactory = "rabbitListenerContainerFactory")
    public void handleUserUpdatedEvent(String eventJson) {
        try {
            log.info("接收到用户更新事件: {}", eventJson);
            
            if (!StringUtils.hasText(eventJson)) {
                log.warn("接收到空的用户更新事件消息");
                return;
            }

            // TODO: 解析UserUpdatedEvent
            // UserUpdatedEvent event = objectMapper.readValue(eventJson, UserUpdatedEvent.class);
            
            // 验证事件数据
            // if (!event.isValid()) {
            //     log.error("用户更新事件数据无效: {}", event);
            //     return;
            // }

            // 委托给专门的处理器处理
            // userUpdatedEventHandler.handle(event);

            log.info("用户更新事件处理完成");

        } catch (Exception e) {
            log.error("处理用户更新事件失败: eventJson={}", eventJson, e);
            throw e; // 重新抛出异常以触发消息重试
        }
    }

    /**
     * 处理用户角色变更事件
     * 
     * <p>当用户角色发生变更时，执行以下业务逻辑：
     * <ul>
     *   <li>更新用户权限和数据访问范围</li>
     *   <li>重新计算佣金比例</li>
     *   <li>同步角色信息到各服务</li>
     *   <li>通知奖励系统重新计算</li>
     * </ul>
     * 
     * @param eventJson 用户角色变更事件JSON字符串
     */
    @RabbitListener(queues = "user.service.queue", containerFactory = "rabbitListenerContainerFactory")
    public void handleUserRoleChangedEvent(String eventJson) {
        try {
            log.info("接收到用户角色变更事件: {}", eventJson);
            
            if (!StringUtils.hasText(eventJson)) {
                log.warn("接收到空的用户角色变更事件消息");
                return;
            }

            // TODO: 解析UserRoleChangedEvent
            // UserRoleChangedEvent event = objectMapper.readValue(eventJson, UserRoleChangedEvent.class);
            
            // 验证事件数据
            // if (!event.isValid()) {
            //     log.error("用户角色变更事件数据无效: {}", event);
            //     return;
            // }

            // 委托给专门的处理器处理
            // userRoleChangedEventHandler.handle(event);

            log.info("用户角色变更事件处理完成");

        } catch (Exception e) {
            log.error("处理用户角色变更事件失败: eventJson={}", eventJson, e);
            throw e; // 重新抛出异常以触发消息重试
        }
    }

    /**
     * 处理用户状态变更事件
     * 
     * <p>当用户状态发生变更时，执行以下业务逻辑：
     * <ul>
     *   <li>同步用户状态到各服务</li>
     *   <li>更新权限和访问控制</li>
     *   <li>处理在途业务数据</li>
     *   <li>记录状态变更审计</li>
     * </ul>
     * 
     * @param eventJson 用户状态变更事件JSON字符串
     */
    @RabbitListener(queues = "user.service.queue", containerFactory = "rabbitListenerContainerFactory")
    public void handleUserStatusChangedEvent(String eventJson) {
        try {
            log.info("接收到用户状态变更事件: {}", eventJson);
            
            if (!StringUtils.hasText(eventJson)) {
                log.warn("接收到空的用户状态变更事件消息");
                return;
            }

            // TODO: 解析UserStatusChangedEvent
            // UserStatusChangedEvent event = objectMapper.readValue(eventJson, UserStatusChangedEvent.class);
            
            // 验证事件数据
            // if (!event.isValid()) {
            //     log.error("用户状态变更事件数据无效: {}", event);
            //     return;
            // }

            // 委托给专门的处理器处理
            // userStatusChangedEventHandler.handle(event);

            log.info("用户状态变更事件处理完成");

        } catch (Exception e) {
            log.error("处理用户状态变更事件失败: eventJson={}", eventJson, e);
            throw e; // 重新抛出异常以触发消息重试
        }
    }

    /**
     * 处理用户删除事件
     * 
     * <p>当用户被删除时，执行以下业务逻辑：
     * <ul>
     *   <li>清理用户相关数据</li>
     *   <li>处理关联业务数据</li>
     *   <li>通知各服务进行清理</li>
     *   <li>记录删除审计日志</li>
     * </ul>
     * 
     * @param eventJson 用户删除事件JSON字符串
     */
    @RabbitListener(queues = "user.service.queue", containerFactory = "rabbitListenerContainerFactory")
    public void handleUserDeletedEvent(String eventJson) {
        try {
            log.info("接收到用户删除事件: {}", eventJson);
            
            if (!StringUtils.hasText(eventJson)) {
                log.warn("接收到空的用户删除事件消息");
                return;
            }

            // TODO: 解析UserDeletedEvent
            // UserDeletedEvent event = objectMapper.readValue(eventJson, UserDeletedEvent.class);
            
            // 验证事件数据
            // if (!event.isValid()) {
            //     log.error("用户删除事件数据无效: {}", event);
            //     return;
            // }

            // TODO: 实现用户删除事件处理逻辑

            log.info("用户删除事件处理完成");

        } catch (Exception e) {
            log.error("处理用户删除事件失败: eventJson={}", eventJson, e);
            throw e; // 重新抛出异常以触发消息重试
        }
    }

    /**
     * 处理通用用户事件
     * 
     * <p>处理无法明确分类的用户事件，根据事件类型进行分发
     * 
     * @param eventJson 事件JSON字符串
     */
    @RabbitListener(queues = "user.service.queue", containerFactory = "rabbitListenerContainerFactory")
    public void handleGenericUserEvent(String eventJson) {
        try {
            log.debug("接收到通用用户事件: {}", eventJson);
            
            if (!StringUtils.hasText(eventJson)) {
                log.warn("接收到空的用户事件消息");
                return;
            }

            // TODO: 根据事件类型进行分发处理
            // 可以通过解析JSON中的eventType字段来判断事件类型
            
        } catch (Exception e) {
            log.error("处理通用用户事件失败: eventJson={}", eventJson, e);
            // 对于无法处理的事件，记录日志但不抛出异常，避免消息堆积
        }
    }
}