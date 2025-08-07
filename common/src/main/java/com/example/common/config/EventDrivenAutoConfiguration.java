package com.example.common.config;

import com.example.common.saga.SagaCoordinator;
import com.example.common.saga.engine.SagaExecutionEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;

import javax.sql.DataSource;

/**
 * 事件驱动架构自动配置类
 * 
 * <p>Spring Boot自动配置类，负责自动装配事件驱动架构的核心组件。
 * 根据环境和依赖情况，智能地启用或禁用相关功能。
 * 
 * <p>自动配置的组件包括：
 * <ul>
 *   <li>RabbitMQ消息基础设施</li>
 *   <li>Saga事务协调器和执行引擎</li>
 *   <li>异步任务执行器</li>
 *   <li>事件发布器和监听器</li>
 *   <li>配置属性绑定</li>
 * </ul>
 * 
 * <p>条件配置：
 * <ul>
 *   <li>当RabbitMQ在类路径中时自动启用消息功能</li>
 *   <li>当Redis在类路径中时启用状态存储功能</li>
 *   <li>当DataSource可用时启用持久化功能</li>
 *   <li>通过配置属性控制功能开关</li>
 * </ul>
 * 
 * @author Event-Driven Architecture Team
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(EventDrivenProperties.class)
@AutoConfigureAfter({
    RabbitAutoConfiguration.class, 
    DataSourceAutoConfiguration.class,
    RedisAutoConfiguration.class
})
@ConditionalOnProperty(
    prefix = "event-driven", 
    name = "enabled", 
    havingValue = "true", 
    matchIfMissing = true
)
@Import({
    RabbitMQConfig.class,
    AsyncConfig.class
})
public class EventDrivenAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(EventDrivenAutoConfiguration.class);

    /**
     * Saga协调器Bean配置
     * 
     * <p>当RabbitTemplate可用且未定义SagaCoordinator时自动创建
     * 
     * @param rabbitTemplate RabbitMQ模板
     * @param eventDrivenProperties 事件驱动配置属性
     * @return Saga协调器实例
     */
    @Bean
    @ConditionalOnClass(RabbitTemplate.class)
    @ConditionalOnMissingBean(SagaCoordinator.class)
    @ConditionalOnProperty(prefix = "event-driven.saga", name = "enabled", havingValue = "true", matchIfMissing = true)
    public SagaCoordinator sagaCoordinator(RabbitTemplate rabbitTemplate,
                                         EventDrivenProperties eventDrivenProperties) {
        logger.info("自动配置Saga协调器");
        
        SagaCoordinator coordinator = new SagaCoordinator(rabbitTemplate);
        
        // 应用配置属性
        EventDrivenProperties.SagaConfig sagaConfig = eventDrivenProperties.getSaga();
        coordinator.setDefaultTimeoutMillis(sagaConfig.getDefaultTimeout());
        coordinator.setMaxRetries(sagaConfig.getMaxRetries());
        coordinator.setRetryIntervalMillis(sagaConfig.getRetryInterval());
        coordinator.setCompensationEnabled(sagaConfig.isCompensationEnabled());
        coordinator.setCleanupInterval(sagaConfig.getCleanupInterval());
        coordinator.setRetentionPeriod(sagaConfig.getRetentionPeriod());
        coordinator.setMaxConcurrentSagas(sagaConfig.getMaxConcurrentSagas());
        
        logger.info("Saga协调器配置完成: defaultTimeout={}ms, maxRetries={}, compensationEnabled={}", 
            sagaConfig.getDefaultTimeout(), sagaConfig.getMaxRetries(), sagaConfig.isCompensationEnabled());
        
        return coordinator;
    }

    /**
     * Saga执行引擎Bean配置
     * 
     * <p>当SagaCoordinator可用且未定义SagaExecutionEngine时自动创建
     * 
     * @param sagaCoordinator Saga协调器
     * @param eventDrivenProperties 事件驱动配置属性
     * @return Saga执行引擎实例
     */
    @Bean
    @ConditionalOnMissingBean(SagaExecutionEngine.class)
    @ConditionalOnProperty(prefix = "event-driven.saga", name = "enabled", havingValue = "true", matchIfMissing = true)
    public SagaExecutionEngine sagaExecutionEngine(SagaCoordinator sagaCoordinator,
                                                  EventDrivenProperties eventDrivenProperties) {
        logger.info("自动配置Saga执行引擎");
        
        SagaExecutionEngine engine = new SagaExecutionEngine(sagaCoordinator);
        
        // 应用配置属性
        EventDrivenProperties.SagaConfig sagaConfig = eventDrivenProperties.getSaga();
        engine.setStepTimeoutMillis(sagaConfig.getStepTimeout());
        engine.setMaxRetries(sagaConfig.getMaxRetries());
        engine.setRetryIntervalMillis(sagaConfig.getRetryInterval());
        
        // 设置服务发现配置
        EventDrivenProperties.ServiceDiscoveryConfig serviceConfig = eventDrivenProperties.getServiceDiscovery();
        engine.setServiceEndpoints(serviceConfig.getEndpoints());
        engine.setConnectTimeout(serviceConfig.getConnectTimeout());
        engine.setReadTimeout(serviceConfig.getReadTimeout());
        engine.setCircuitBreakerFailureThreshold(serviceConfig.getCircuitBreakerFailureThreshold());
        engine.setCircuitBreakerRecoveryTime(serviceConfig.getCircuitBreakerRecoveryTime());
        
        logger.info("Saga执行引擎配置完成: stepTimeout={}ms, serviceEndpoints={}", 
            sagaConfig.getStepTimeout(), serviceConfig.getEndpoints().size());
        
        return engine;
    }

    /**
     * Redis状态存储配置
     * 
     * <p>当Redis可用时为Saga提供状态持久化支持
     * 
     * @param redisTemplate Redis模板
     * @return Redis状态存储器
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
    @ConditionalOnMissingBean(name = "sagaStateStore")
    @ConditionalOnProperty(prefix = "event-driven.saga", name = "state-store", havingValue = "redis", matchIfMissing = true)
    public Object sagaStateStore(RedisTemplate<String, Object> redisTemplate) {
        logger.info("配置Redis作为Saga状态存储");
        
        // TODO: 实现SagaStateStore接口
        // return new RedisSagaStateStore(redisTemplate);
        return redisTemplate;
    }

    /**
     * 数据库状态存储配置
     * 
     * <p>当DataSource可用时为Saga提供数据库持久化支持
     * 
     * @param dataSource 数据源
     * @return 数据库状态存储器
     */
    @Bean
    @ConditionalOnClass(DataSource.class)
    @ConditionalOnMissingBean(name = "sagaDatabaseStore")
    @ConditionalOnProperty(prefix = "event-driven.saga", name = "state-store", havingValue = "database")
    public Object sagaDatabaseStore(DataSource dataSource) {
        logger.info("配置数据库作为Saga状态存储");
        
        // TODO: 实现SagaDatabaseStore接口
        // return new DatabaseSagaStateStore(dataSource);
        return dataSource;
    }

    /**
     * 事件发布性能监控配置
     * 
     * <p>启用事件发布的性能指标收集
     */
    @Bean
    @ConditionalOnProperty(prefix = "event-driven.monitoring", name = "enabled", havingValue = "true")
    public Object eventPublishingMetrics() {
        logger.info("启用事件发布性能监控");
        
        // TODO: 实现事件发布性能监控
        // return new EventPublishingMetrics();
        return new Object();
    }

    /**
     * Saga执行监控配置
     * 
     * <p>启用Saga执行的性能和状态监控
     */
    @Bean
    @ConditionalOnProperty(prefix = "event-driven.monitoring", name = "saga-enabled", havingValue = "true")
    public Object sagaExecutionMetrics() {
        logger.info("启用Saga执行监控");
        
        // TODO: 实现Saga执行监控
        // return new SagaExecutionMetrics();
        return new Object();
    }

    /**
     * 死信队列处理器配置
     * 
     * <p>处理消息处理失败后进入死信队列的情况
     */
    @Bean
    @ConditionalOnClass(RabbitTemplate.class)
    @ConditionalOnProperty(prefix = "event-driven.rabbitmq", name = "dead-letter-enabled", havingValue = "true", matchIfMissing = true)
    public Object deadLetterQueueHandler(RabbitTemplate rabbitTemplate) {
        logger.info("配置死信队列处理器");
        
        // TODO: 实现死信队列处理器
        // return new DeadLetterQueueHandler(rabbitTemplate);
        return rabbitTemplate;
    }

    /**
     * 事件去重处理器配置
     * 
     * <p>基于Redis的事件去重机制
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
    @ConditionalOnProperty(prefix = "event-driven.event-publish", name = "deduplication-enabled", havingValue = "true", matchIfMissing = true)
    public Object eventDeduplicationHandler(RedisTemplate<String, Object> redisTemplate,
                                           EventDrivenProperties eventDrivenProperties) {
        logger.info("配置事件去重处理器");
        
        EventDrivenProperties.EventPublishConfig publishConfig = eventDrivenProperties.getEventPublish();
        logger.info("事件去重窗口时间: {}ms", publishConfig.getDeduplicationWindow());
        
        // TODO: 实现事件去重处理器
        // return new EventDeduplicationHandler(redisTemplate, publishConfig.getDeduplicationWindow());
        return redisTemplate;
    }

    /**
     * 健康检查配置
     * 
     * <p>为事件驱动架构组件提供健康检查端点
     */
    @Bean
    @ConditionalOnProperty(prefix = "management.endpoint.saga", name = "enabled", havingValue = "true", matchIfMissing = true)
    public Object eventDrivenHealthIndicator(SagaCoordinator sagaCoordinator,
                                           RabbitTemplate rabbitTemplate) {
        logger.info("配置事件驱动架构健康检查");
        
        // TODO: 实现健康检查指示器
        // return new EventDrivenHealthIndicator(sagaCoordinator, rabbitTemplate);
        return new Object();
    }

    /**
     * 配置验证
     * 
     * <p>在应用启动时验证配置的正确性
     */
    @Bean
    public Object eventDrivenConfigurationValidator(EventDrivenProperties eventDrivenProperties) {
        logger.info("验证事件驱动架构配置");
        
        // 验证基本配置
        validateBasicConfiguration(eventDrivenProperties);
        
        // 验证Saga配置
        validateSagaConfiguration(eventDrivenProperties.getSaga());
        
        // 验证服务发现配置
        validateServiceDiscoveryConfiguration(eventDrivenProperties.getServiceDiscovery());
        
        logger.info("事件驱动架构配置验证通过");
        return new Object();
    }

    /**
     * 验证基本配置
     */
    private void validateBasicConfiguration(EventDrivenProperties properties) {
        EventDrivenProperties.RabbitMQConfig rabbitmqConfig = properties.getRabbitmq();
        
        if (rabbitmqConfig.getMessageTtl() <= 0) {
            throw new IllegalArgumentException("RabbitMQ消息TTL必须大于0");
        }
        
        if (rabbitmqConfig.getMaxRetries() < 0) {
            throw new IllegalArgumentException("RabbitMQ最大重试次数不能小于0");
        }
        
        if (rabbitmqConfig.getPrefetchCount() <= 0) {
            throw new IllegalArgumentException("RabbitMQ预取数量必须大于0");
        }
    }

    /**
     * 验证Saga配置
     */
    private void validateSagaConfiguration(EventDrivenProperties.SagaConfig sagaConfig) {
        if (sagaConfig.getDefaultTimeout() <= 0) {
            throw new IllegalArgumentException("Saga默认超时时间必须大于0");
        }
        
        if (sagaConfig.getStepTimeout() <= 0) {
            throw new IllegalArgumentException("Saga步骤超时时间必须大于0");
        }
        
        if (sagaConfig.getMaxConcurrentSagas() <= 0) {
            throw new IllegalArgumentException("最大并发Saga数量必须大于0");
        }
        
        if (sagaConfig.getRetentionPeriod() <= sagaConfig.getCleanupInterval()) {
            throw new IllegalArgumentException("Saga保留时间必须大于清理间隔");
        }
    }

    /**
     * 验证服务发现配置
     */
    private void validateServiceDiscoveryConfiguration(EventDrivenProperties.ServiceDiscoveryConfig serviceConfig) {
        if (serviceConfig.getEndpoints().isEmpty()) {
            logger.warn("未配置任何服务端点，Saga步骤执行可能失败");
        }
        
        if (serviceConfig.getConnectTimeout() <= 0) {
            throw new IllegalArgumentException("服务连接超时时间必须大于0");
        }
        
        if (serviceConfig.getReadTimeout() <= 0) {
            throw new IllegalArgumentException("服务读取超时时间必须大于0");
        }
        
        if (serviceConfig.getCircuitBreakerFailureThreshold() <= 0) {
            throw new IllegalArgumentException("熔断器失败阈值必须大于0");
        }
    }
}