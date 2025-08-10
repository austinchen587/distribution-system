package com.example.common.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * RabbitMQ配置类
 * 
 * <p>为事件驱动架构配置RabbitMQ的交换机、队列、绑定关系和消息转换器。
 * 该配置类按照业务域组织交换机和队列，支持主题路由和死信队列。
 * 
 * <p>架构设计：
 * <ul>
 *   <li>每个业务域使用独立的Topic Exchange</li>
 *   <li>支持灵活的路由键匹配（user.*, lead.*, etc.）</li>
 *   <li>所有队列配置死信队列进行错误处理</li>
 *   <li>使用JSON消息转换器支持对象序列化</li>
 *   <li>配置重试机制和确认模式</li>
 * </ul>
 * 
 * <p>交换机设计：
 * <ul>
 *   <li>user.exchange: 处理用户相关事件</li>
 *   <li>lead.exchange: 处理客资相关事件</li>
 *   <li>promotion.exchange: 处理推广相关事件</li>
 *   <li>reward.exchange: 处理奖励相关事件</li>
 *   <li>invitation.exchange: 处理邀请相关事件</li>
 *   <li>saga.exchange: 处理Saga事务协调事件</li>
 *   <li>system.exchange: 处理系统级事件</li>
 * </ul>
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Configuration
public class RabbitMQConfig {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);

    // ========== 交换机名称常量 ==========
    public static final String USER_EXCHANGE = "user.exchange";
    public static final String LEAD_EXCHANGE = "lead.exchange";
    public static final String PROMOTION_EXCHANGE = "promotion.exchange";
    public static final String REWARD_EXCHANGE = "reward.exchange";
    public static final String INVITATION_EXCHANGE = "invitation.exchange";
    public static final String SAGA_EXCHANGE = "saga.exchange";
    public static final String SYSTEM_EXCHANGE = "system.exchange";
    
    // ========== 死信交换机 ==========
    public static final String DLX_EXCHANGE = "dlx.exchange";
    public static final String DLX_QUEUE = "dlx.queue";
    public static final String DLX_ROUTING_KEY = "dlx.routing.key";

    // ========== 队列TTL和重试配置 ==========
    private static final int MESSAGE_TTL = 300000; // 5分钟
    private static final int MAX_RETRY_ATTEMPTS = 3;

    /**
     * JSON消息转换器
     * <p>配置Jackson序列化参数，支持LocalDateTime等Java 8时间类型
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 配置时间模块
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // 配置序列化选项
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        converter.setCreateMessageIds(true);
        
        return converter;
    }

    /**
     * RabbitTemplate配置
     * <p>设置消息转换器、确认模式和重试策略
     */
    @Bean
    @ConditionalOnMissingBean(RabbitTemplate.class)
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);

        // 开启发布确认
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                logger.debug("消息发布成功: correlationData={}", correlationData);
            } else {
                logger.error("消息发布失败: correlationData={}, cause={}", correlationData, cause);
            }
        });
        
        // 开启返回确认
        template.setReturnsCallback(returnedMessage -> {
            logger.error("消息路由失败: exchange={}, routingKey={}, message={}", 
                returnedMessage.getExchange(), 
                returnedMessage.getRoutingKey(), 
                new String(returnedMessage.getMessage().getBody()));
        });
        
        // 设置重试模板
        template.setRetryTemplate(retryTemplate());
        
        return template;
    }

    /**
     * 重试策略配置
     */
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        
        // 简单重试策略
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(MAX_RETRY_ATTEMPTS);
        retryTemplate.setRetryPolicy(retryPolicy);
        
        // 指数退避策略
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000L);  // 初始间隔1秒
        backOffPolicy.setMultiplier(2.0);         // 每次翻倍
        backOffPolicy.setMaxInterval(10000L);     // 最大间隔10秒
        retryTemplate.setBackOffPolicy(backOffPolicy);
        
        return retryTemplate;
    }

    /**
     * RabbitMQ监听器容器工厂
     */
    @Bean
    @ConditionalOnMissingBean(RabbitListenerContainerFactory.class)
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory, MessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        
        // 设置并发配置
        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(10);
        
        // 设置确认模式
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        
        // 设置预取数量
        factory.setPrefetchCount(10);
        
        return factory;
    }

    // ========== 死信队列配置 ==========
    
    /**
     * 死信交换机
     */
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    /**
     * 死信队列
     */
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLX_QUEUE).build();
    }

    /**
     * 死信队列绑定
     */
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(DLX_ROUTING_KEY);
    }

    // ========== 用户相关交换机和队列 ==========
    
    /**
     * 用户事件交换机
     */
    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE, true, false);
    }

    /**
     * 用户事件队列
     */
    @Bean
    public Queue userQueue() {
        return QueueBuilder.durable("user.queue")
                .withArgument("x-message-ttl", MESSAGE_TTL)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLX_ROUTING_KEY)
                .build();
    }

    /**
     * 用户事件绑定
     */
    @Bean
    public Binding userBinding() {
        return BindingBuilder.bind(userQueue())
                .to(userExchange())
                .with("user.*");
    }

    // ========== 客资相关交换机和队列 ==========
    
    /**
     * 客资事件交换机
     */
    @Bean
    public TopicExchange leadExchange() {
        return new TopicExchange(LEAD_EXCHANGE, true, false);
    }

    /**
     * 客资事件队列
     */
    @Bean
    public Queue leadQueue() {
        return QueueBuilder.durable("lead.queue")
                .withArgument("x-message-ttl", MESSAGE_TTL)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLX_ROUTING_KEY)
                .build();
    }

    /**
     * 客资事件绑定
     */
    @Bean
    public Binding leadBinding() {
        return BindingBuilder.bind(leadQueue())
                .to(leadExchange())
                .with("lead.*");
    }

    // ========== 推广相关交换机和队列 ==========
    
    /**
     * 推广事件交换机
     */
    @Bean
    public TopicExchange promotionExchange() {
        return new TopicExchange(PROMOTION_EXCHANGE, true, false);
    }

    /**
     * 推广事件队列
     */
    @Bean
    public Queue promotionQueue() {
        return QueueBuilder.durable("promotion.queue")
                .withArgument("x-message-ttl", MESSAGE_TTL)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLX_ROUTING_KEY)
                .build();
    }

    /**
     * 推广事件绑定
     */
    @Bean
    public Binding promotionBinding() {
        return BindingBuilder.bind(promotionQueue())
                .to(promotionExchange())
                .with("promotion.*");
    }

    // ========== 奖励相关交换机和队列 ==========
    
    /**
     * 奖励事件交换机
     */
    @Bean
    public TopicExchange rewardExchange() {
        return new TopicExchange(REWARD_EXCHANGE, true, false);
    }

    /**
     * 奖励事件队列
     */
    @Bean
    public Queue rewardQueue() {
        return QueueBuilder.durable("reward.queue")
                .withArgument("x-message-ttl", MESSAGE_TTL)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLX_ROUTING_KEY)
                .build();
    }

    /**
     * 奖励事件绑定
     */
    @Bean
    public Binding rewardBinding() {
        return BindingBuilder.bind(rewardQueue())
                .to(rewardExchange())
                .with("reward.*");
    }

    /**
     * 佣金事件绑定
     */
    @Bean
    public Binding commissionBinding() {
        return BindingBuilder.bind(rewardQueue())
                .to(rewardExchange())
                .with("commission.*");
    }

    // ========== 邀请相关交换机和队列 ==========
    
    /**
     * 邀请事件交换机
     */
    @Bean
    public TopicExchange invitationExchange() {
        return new TopicExchange(INVITATION_EXCHANGE, true, false);
    }

    /**
     * 邀请事件队列
     */
    @Bean
    public Queue invitationQueue() {
        return QueueBuilder.durable("invitation.queue")
                .withArgument("x-message-ttl", MESSAGE_TTL)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLX_ROUTING_KEY)
                .build();
    }

    /**
     * 邀请事件绑定
     */
    @Bean
    public Binding invitationBinding() {
        return BindingBuilder.bind(invitationQueue())
                .to(invitationExchange())
                .with("invitation.*");
    }

    // ========== Saga事务协调交换机和队列 ==========
    
    /**
     * Saga事务交换机
     */
    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(SAGA_EXCHANGE, true, false);
    }

    /**
     * Saga事务队列
     */
    @Bean
    public Queue sagaQueue() {
        return QueueBuilder.durable("saga.queue")
                .withArgument("x-message-ttl", MESSAGE_TTL)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLX_ROUTING_KEY)
                .build();
    }

    /**
     * Saga事务绑定
     */
    @Bean
    public Binding sagaBinding() {
        return BindingBuilder.bind(sagaQueue())
                .to(sagaExchange())
                .with("saga.*");
    }

    // ========== 系统事件交换机和队列 ==========
    
    /**
     * 系统事件交换机
     */
    @Bean
    public TopicExchange systemExchange() {
        return new TopicExchange(SYSTEM_EXCHANGE, true, false);
    }

    /**
     * 系统事件队列
     */
    @Bean
    public Queue systemQueue() {
        return QueueBuilder.durable("system.queue")
                .withArgument("x-message-ttl", MESSAGE_TTL)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLX_ROUTING_KEY)
                .build();
    }

    /**
     * 系统事件绑定
     */
    @Bean
    public Binding systemBinding() {
        return BindingBuilder.bind(systemQueue())
                .to(systemExchange())
                .with("system.*");
    }
}