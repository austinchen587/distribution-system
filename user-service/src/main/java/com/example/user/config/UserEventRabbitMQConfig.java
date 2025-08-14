package com.example.user.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.example.common.config.RabbitMQConfig.*;

/**
 * 用户服务RabbitMQ配置
 * 
 * <p>为User服务配置专门的队列和绑定关系，用于处理用户相关的领域事件。
 * 该配置扩展了通用的RabbitMQ配置，为User服务创建专门的队列。
 * 
 * <p>队列设计：
 * <ul>
 *   <li>user.service.queue: 用户服务专用队列</li>
 *   <li>继承死信队列配置</li>
 *   <li>支持TTL和重试机制</li>
 * </ul>
 * 
 * <p>路由策略：
 * <ul>
 *   <li>监听所有user.*事件</li>
 *   <li>用户创建事件: user.created</li>
 *   <li>用户更新事件: user.updated</li>
 *   <li>用户删除事件: user.deleted</li>
 *   <li>用户状态变更: user.status.changed</li>
 *   <li>用户角色变更: user.role.changed</li>
 * </ul>
 * 
 * @author User Service Team
 * @version 1.0.0
 * @since 2025-08-12
 */
@Configuration
public class UserEventRabbitMQConfig {

    // ========== 用户服务队列常量 ==========
    public static final String USER_SERVICE_QUEUE = "user.service.queue";
    
    // ========== 路由键常量 ==========
    public static final String USER_CREATED_ROUTING_KEY = "user.created";
    public static final String USER_UPDATED_ROUTING_KEY = "user.updated";
    public static final String USER_DELETED_ROUTING_KEY = "user.deleted";
    public static final String USER_STATUS_CHANGED_ROUTING_KEY = "user.status.changed";
    public static final String USER_ROLE_CHANGED_ROUTING_KEY = "user.role.changed";
    
    // ========== 队列配置 ==========
    private static final int MESSAGE_TTL = 300000; // 5分钟
    
    /**
     * 用户服务专用队列
     * 
     * <p>该队列专门用于处理用户服务的事件，包括：
     * <ul>
     *   <li>用户创建后的业务处理</li>
     *   <li>用户信息变更后的同步</li>
     *   <li>用户状态变更的后续处理</li>
     *   <li>用户角色变更的权限更新</li>
     * </ul>
     * 
     * @return 用户服务队列
     */
    @Bean
    public Queue userServiceQueue() {
        return QueueBuilder.durable(USER_SERVICE_QUEUE)
                .withArgument("x-message-ttl", MESSAGE_TTL)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLX_ROUTING_KEY)
                .build();
    }
    
    /**
     * 用户服务队列绑定 - 监听所有用户事件
     * 
     * <p>使用通配符路由键"user.*"来监听所有用户相关事件
     * 
     * @return 绑定关系
     */
    @Bean
    public Binding userServiceBinding() {
        // 使用TopicExchange的Bean引用
        TopicExchange exchange = new TopicExchange(USER_EXCHANGE);
        return BindingBuilder.bind(userServiceQueue())
                .to(exchange)
                .with("user.*");
    }
    
    /**
     * 用户创建事件绑定
     * 
     * <p>专门绑定用户创建事件，确保优先级处理
     * 
     * @return 绑定关系
     */
    @Bean
    public Binding userCreatedBinding() {
        TopicExchange exchange = new TopicExchange(USER_EXCHANGE);
        return BindingBuilder.bind(userServiceQueue())
                .to(exchange)
                .with(USER_CREATED_ROUTING_KEY);
    }
    
    /**
     * 用户更新事件绑定
     * 
     * @return 绑定关系
     */
    @Bean
    public Binding userUpdatedBinding() {
        TopicExchange exchange = new TopicExchange(USER_EXCHANGE);
        return BindingBuilder.bind(userServiceQueue())
                .to(exchange)
                .with(USER_UPDATED_ROUTING_KEY);
    }
    
    /**
     * 用户删除事件绑定
     * 
     * @return 绑定关系
     */
    @Bean
    public Binding userDeletedBinding() {
        TopicExchange exchange = new TopicExchange(USER_EXCHANGE);
        return BindingBuilder.bind(userServiceQueue())
                .to(exchange)
                .with(USER_DELETED_ROUTING_KEY);
    }
    
    /**
     * 用户状态变更事件绑定
     * 
     * @return 绑定关系
     */
    @Bean
    public Binding userStatusChangedBinding() {
        TopicExchange exchange = new TopicExchange(USER_EXCHANGE);
        return BindingBuilder.bind(userServiceQueue())
                .to(exchange)
                .with(USER_STATUS_CHANGED_ROUTING_KEY);
    }
    
    /**
     * 用户角色变更事件绑定
     * 
     * @return 绑定关系
     */
    @Bean
    public Binding userRoleChangedBinding() {
        TopicExchange exchange = new TopicExchange(USER_EXCHANGE);
        return BindingBuilder.bind(userServiceQueue())
                .to(exchange)
                .with(USER_ROLE_CHANGED_ROUTING_KEY);
    }
}